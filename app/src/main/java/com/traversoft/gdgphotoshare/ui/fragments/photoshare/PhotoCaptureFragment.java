package com.traversoft.gdgphotoshare.ui.fragments.photoshare;


import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;

import com.crashlytics.android.Crashlytics;
import com.traversoft.gdgphotoshare.MainActivity;
import com.traversoft.gdgphotoshare.R;
import com.traversoft.gdgphotoshare.databinding.FragmentPhotoCaptureBinding;
import com.traversoft.gdgphotoshare.ui.fragments.GDGBaseFragment;
import com.traversoft.gdgphotoshare.utilities.ImageParameters;
import com.traversoft.gdgphotoshare.utilities.ImageUtility;

import java.io.IOException;
import java.util.List;


public class PhotoCaptureFragment
        extends GDGBaseFragment implements SurfaceHolder.Callback, Camera.PictureCallback {

    public static final String TAG = PhotoCaptureFragment.class.getSimpleName();
    public static final String CAMERA_ID_KEY = "camera_id";
    public static final String CAMERA_FLASH_KEY = "flash_mode";
    public static final String BITMAP_KEY = "bitmap_byte_array";
    public static final String CAMERA_ID = "camera_id";
    public static final String FROM_GALLERY = "from_gallery";
    public static final String IMAGE_INFO = "image_info";

    private static int IMAGE_PICKER_SELECT = 1;
    private static final int PICTURE_SIZE_MAX_WIDTH = 1280;
    private static final int PREVIEW_SIZE_MAX_WIDTH = 640;

    private int cameraID;
    private String flashMode;
    private Camera camera;
    private SurfaceHolder surfaceHolder;
    private ImageParameters mImageParameters;
    private FragmentPhotoCaptureBinding viewHolder;
    private Bundle savedBundle;

    public PhotoCaptureFragment() { }

    private final BroadcastReceiver permissionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            cameraAccessApproved();
        }
    };

    public static Fragment newInstance(byte[] bitmapByteArray, boolean fromGallery, int cameraId, @NonNull ImageParameters parameters) {

        Fragment fragment = new PhotoCaptureFragment();
        Bundle args = new Bundle();
        args.putByteArray(BITMAP_KEY, bitmapByteArray);
        args.putParcelable(IMAGE_INFO, parameters);
        args.putInt(CAMERA_ID, cameraId);
        args.putBoolean(FROM_GALLERY, fromGallery);
        fragment.setArguments(args);
        return fragment;
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewHolder = FragmentPhotoCaptureBinding.inflate(inflater, container, false);
        getGdgActivity().setFabVisibility(false);
        return viewHolder.getRoot();
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        if (savedInstanceState == null) {
            cameraID = getBackCameraID();
            flashMode = Camera.Parameters.FLASH_MODE_AUTO;
            mImageParameters = new ImageParameters();
        } else {
            cameraID = savedInstanceState.getInt(CAMERA_ID_KEY);
            flashMode = savedInstanceState.getString(CAMERA_FLASH_KEY);
            mImageParameters = savedInstanceState.getParcelable(IMAGE_INFO);
        }
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.savedBundle = savedInstanceState;
        checkCameraPermission();
    }

    @Override public void onStop() {
        if (camera != null) {
            stopCameraPreview();
            camera.release();
            camera = null;
        }
        super.onStop();
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        outState.putInt(CAMERA_ID_KEY, cameraID);
        outState.putString(CAMERA_FLASH_KEY, flashMode);
        outState.putParcelable(IMAGE_INFO, mImageParameters);
        super.onSaveInstanceState(outState);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == IMAGE_PICKER_SELECT && resultCode == Activity.RESULT_OK) {

            String path = getPathFromCameraData(data, this.getActivity());
            Log.i("PICTURE", "Path: " + path);
            if (path != null) {

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(path, options);

                byte[] bitmapData = ImageUtility.convertBitmapToByteArray(bitmap);
                Fragment fragment = PhotoCaptureFragment.newInstance(bitmapData, true, cameraID, mImageParameters.createCopy());
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.content_main, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        }
    }

    public static String getPathFromCameraData(Intent data, Context context) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return picturePath;
    }

    // Camera methods

    private void getCamera(int cameraID) {
        try {
            camera = Camera.open(cameraID);
            viewHolder.cameraPreviewView.setCamera(camera);
        } catch (Exception e) {
            Log.d(TAG, "Can't open camera with id " + cameraID);
            e.printStackTrace();
            Crashlytics.log("User cannot open camera " + cameraID + " " + e.getLocalizedMessage());
        }
    }

    private void startCameraPreview() {
        determineDisplayOrientation();
        setupCamera();
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Can't start camera preview due to IOException " + e);
            e.printStackTrace();
        }
    }

    private void stopCameraPreview() {
        camera.stopPreview();
        viewHolder.cameraPreviewView.setCamera(null);
    }

    private void determineDisplayOrientation() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraID, cameraInfo);

        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0: {
                degrees = 0;
                break;
            }
            case Surface.ROTATION_90: {
                degrees = 90;
                break;
            }
            case Surface.ROTATION_180: {
                degrees = 180;
                break;
            }
            case Surface.ROTATION_270: {
                degrees = 270;
                break;
            }
        }

        int displayOrientation;

        // CameraInfo.Orientation is the angle relative to the natural position of the device
        // in clockwise rotation (angle that is rotated clockwise from the natural position)
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            // Orientation is angle of rotation when facing the camera for
            // the camera image to match the natural orientation of the device
            displayOrientation = (cameraInfo.orientation + degrees) % 360;
            displayOrientation = (360 - displayOrientation) % 360;
        } else {
            displayOrientation = (cameraInfo.orientation - degrees + 360) % 360;
        }

        mImageParameters.setDisplayOrientation(displayOrientation);
        mImageParameters.setLayoutOrientation(degrees);
        camera.setDisplayOrientation(mImageParameters.getDisplayOrientation());
    }

    private void setupCamera() {
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size bestPreviewSize = determineBestPreviewSize(parameters);
        Camera.Size bestPictureSize = determineBestPictureSize(parameters);
        parameters.setPreviewSize(bestPreviewSize.width, bestPreviewSize.height);
        parameters.setPictureSize(bestPictureSize.width, bestPictureSize.height);

        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }

        final ImageButton btnFlash = (ImageButton)getView().findViewById(R.id.btnFlash);
        List<String> flashModes = parameters.getSupportedFlashModes();

        if (flashModes != null && flashModes.contains(flashMode)) {
            parameters.setFlashMode(flashMode);
            btnFlash.setVisibility(View.VISIBLE);
        } else {
            btnFlash.setVisibility(View.INVISIBLE);
        }

        camera.setParameters(parameters);
    }

    private Camera.Size determineBestPreviewSize(Camera.Parameters parameters) {
        return determineBestSize(parameters.getSupportedPreviewSizes(), PREVIEW_SIZE_MAX_WIDTH);
    }

    private Camera.Size determineBestPictureSize(Camera.Parameters parameters) {
        return determineBestSize(parameters.getSupportedPictureSizes(), PICTURE_SIZE_MAX_WIDTH);
    }

    private Camera.Size determineBestSize(List<Camera.Size> sizes, int widthMaximum) {
        Camera.Size bestSize = null;
        Camera.Size size;
        int numOfSizes = sizes.size();

        for (int i = 0; i < numOfSizes; i++) {
            size = sizes.get(i);
            boolean isDesireRatio = (size.width / 4) == (size.height / 3);
            boolean isBetterSize = (bestSize == null) || size.width > bestSize.width;

            if (isDesireRatio && isBetterSize) {
                bestSize = size;
            }
        }

        if (bestSize == null) {
            return sizes.get(sizes.size() - 1); // Get last size since couldn't find best
        }

        return bestSize;
    }

    private void restartPreview() {
        if (camera != null) {
            stopCameraPreview();
            camera.release();
            camera = null;
        }

        getCamera(cameraID);
        startCameraPreview();
    }

    private int getFrontCameraID() {
        PackageManager pm = getActivity().getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            return Camera.CameraInfo.CAMERA_FACING_FRONT;
        }
        return getBackCameraID();
    }

    private int getBackCameraID() {
        return Camera.CameraInfo.CAMERA_FACING_BACK;
    }

    private void takePicture() {
        Camera.ShutterCallback shutterCallback = () -> {
            Log.i(TAG, "Shutter");
        };
        Camera.PictureCallback raw = null;
        Camera.PictureCallback postView = null;

        // jpeg callback occurs when the compressed image is available
        camera.takePicture(shutterCallback, raw, postView, this);
    }

    @Override public void surfaceCreated(SurfaceHolder holder) {
        surfaceHolder = holder;
        getCamera(cameraID);
        if (camera !=null) {
            startCameraPreview();
        }
    }

    @Override public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        return;
    }

    @Override public void surfaceDestroyed(SurfaceHolder holder) {
        return;
    }

    @Override public void onPictureTaken(byte[] data, Camera camera) {
        Fragment fragment = PhotoPreviewFragment.newInstance(data, false, cameraID, mImageParameters.createCopy());
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content_main, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void checkCameraPermission() {

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ((MainActivity) getActivity()).requestPermission(Manifest.permission.CAMERA, "Can we have access to your camera to allow you to snap photos and share with other attendees");
        } else {
            cameraAccessApproved();
        }
    }

    private void cameraAccessDenied() {
        // Go back
    }

    private void cameraAccessApproved() {

        viewHolder.cameraPreviewView.getHolder().addCallback(PhotoCaptureFragment.this);
        if (this.savedBundle == null) {

            ViewTreeObserver observer = viewHolder.cameraPreviewView.getViewTreeObserver();
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {

                    mImageParameters.setPreviewWidth(viewHolder.cameraPreviewView.getWidth());
                    mImageParameters.setPreviewHeight(viewHolder.cameraPreviewView.getHeight());
                    mImageParameters.setCoverWidth(mImageParameters.calculateCoverWidthHeight());
                    mImageParameters.setCoverHeight(mImageParameters.calculateCoverWidthHeight());

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        viewHolder.cameraPreviewView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        viewHolder.cameraPreviewView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            });
        }

        viewHolder.btnToggleCamera.setOnClickListener(v -> {
            toggleCamera();
        });

        flashMode = Camera.Parameters.FLASH_MODE_AUTO;
        viewHolder.btnFlash.setOnClickListener(v -> {
            toggleFlash();
        });

        viewHolder.btnTakePhoto.setOnClickListener(v -> takePicture());
        viewHolder.btnViewGallery.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, IMAGE_PICKER_SELECT);
        });
    }

    private void toggleFlash() {
        if (flashMode.equalsIgnoreCase(Camera.Parameters.FLASH_MODE_AUTO)) {
            flashMode = Camera.Parameters.FLASH_MODE_ON;
            viewHolder.btnFlash.setImageDrawable(getResources().getDrawable(R.drawable.camera_flash_on));
        } else if (flashMode.equalsIgnoreCase(Camera.Parameters.FLASH_MODE_ON)) {
            flashMode = Camera.Parameters.FLASH_MODE_OFF;
            viewHolder.btnFlash.setImageDrawable(getResources().getDrawable(R.drawable.camera_flash_off));
        } else if (flashMode.equalsIgnoreCase(Camera.Parameters.FLASH_MODE_OFF)) {
            flashMode = Camera.Parameters.FLASH_MODE_AUTO;
            viewHolder.btnFlash.setImageDrawable(getResources().getDrawable(R.drawable.camera_flash_auto));
        }

        setupCamera();
    }

    private void toggleCamera() {
        if (cameraID == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            cameraID = getBackCameraID();
        } else {
            cameraID = getFrontCameraID();
        }

        restartPreview();
    }
}