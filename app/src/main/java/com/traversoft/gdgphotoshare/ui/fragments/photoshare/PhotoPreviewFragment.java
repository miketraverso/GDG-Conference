package com.traversoft.gdgphotoshare.ui.fragments.photoshare;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.traversoft.gdgphotoshare.MainActivity;
import com.traversoft.gdgphotoshare.R;
import com.traversoft.gdgphotoshare.databinding.FragmentPhotoPreviewBinding;
import com.traversoft.gdgphotoshare.ui.fragments.GDGBaseFragment;
import com.traversoft.gdgphotoshare.utilities.ImageParameters;
import com.traversoft.gdgphotoshare.utilities.ImageUtility;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;


public class PhotoPreviewFragment
        extends GDGBaseFragment {

    private static final int RESULT_TWEET_SUCCESS = -1;
    private static final int RESULT_TWEET_CANCELLED = 0;
    public static final int TWITTER_REQUEST_CODE = 100;

    public static final String BITMAP_KEY = "bitmap_byte_array";
    public static final String IMAGE_INFO = "image_info";
    public static final String CAMERA_ID = "camera_id";
    public static final String FROM_GALLERY = "from_gallery";

    private Uri mUriToImage;
    private Bitmap mOriginalBitmap, mBitmap;
    private Bitmap bitmap = null, bmp = null, overlay = null, und = null;
    private FragmentPhotoPreviewBinding viewHolder;

    private final BroadcastReceiver permissionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            writeExternalApproved();
        }
    };

    public PhotoPreviewFragment() {}

    public static Fragment newInstance(byte[] bitmapByteArray, boolean fromGallery, int cameraId, @NonNull ImageParameters parameters) {

        Fragment fragment = new PhotoPreviewFragment();

        Bundle args = new Bundle();
        args.putByteArray(BITMAP_KEY, bitmapByteArray);
        args.putParcelable(IMAGE_INFO, parameters);
        args.putInt(CAMERA_ID, cameraId);
        args.putBoolean(FROM_GALLERY, fromGallery);

        fragment.setArguments(args);
        return fragment;
    }

    @Override public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewHolder = FragmentPhotoPreviewBinding.inflate(inflater, container, false);
        setupUI();
        return viewHolder.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedBundleInstance) {
        super.onViewCreated(view, savedBundleInstance);
        addBottomOverlay();
        checkWritePermission();
    }

    private void checkWritePermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ((MainActivity) getActivity()).requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, "Can we have access to your save your photos?");
        } else {
            writeExternalApproved();
        }
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        bitmap.recycle();
        overlay.recycle();
        und.recycle();
    }

    private void setupUI() {
        byte[] data = getArguments().getByteArray(BITMAP_KEY);
        ImageParameters imageParameters = getArguments().getParcelable(IMAGE_INFO);

        mBitmap = ImageUtility.decodeSampledBitmapFromByte(getActivity(), data);
        Bitmap oldBitmap = mBitmap;
        Matrix matrix = new Matrix();

        if (getArguments()!=null) {

            if (getArguments().getBoolean(FROM_GALLERY)) {

            }
            else {
                int cameraId = getArguments().getInt(CAMERA_ID);
                if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {

                    matrix.postRotate(90);
                } else {

                    matrix.setScale(1, -1);
                    matrix.postRotate(270);
                }
            }
        }

        mBitmap = Bitmap.createBitmap(oldBitmap, 0, 0, oldBitmap.getWidth(), oldBitmap.getHeight(), matrix, false);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.overlay);

        int width = Math.round(mBitmap.getWidth() * Math.min(getResources().getDisplayMetrics().density, 2.0f));
        int height = Math.round(mBitmap.getHeight() * Math.min(getResources().getDisplayMetrics().density, 2.0f));

        bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888); // the bitmap you paint to
        overlay = Bitmap.createScaledBitmap(bitmap , width, width/3, true);
        und = Bitmap.createScaledBitmap(mBitmap, width, height, true);
        und.setDensity(overlay.getDensity());
        mOriginalBitmap = bmp.copy(Bitmap.Config.ARGB_8888, false);

        viewHolder.previewImage.setImageBitmap(bmp);
        viewHolder.btnShare.setOnClickListener(v -> {

            mUriToImage = ImageUtility.turnBitmapToUri(getContext(), ((BitmapDrawable)viewHolder.previewImage.getDrawable()).getBitmap());
            if (mUriToImage == null) {
                Toast.makeText(getContext(),
                        "Sorry, but we can't save the image to tweet it. Please check your app permissions.",
                        Toast.LENGTH_LONG).show();
                return;
            }

            TweetComposer.Builder builder = new TweetComposer.Builder(getContext());
            if (mUriToImage != null) {
                builder.text(getResources().getString(R.string.hashtag)  + " ")
                        .image(mUriToImage);
            } else {
                builder.text(getResources().getString(R.string.hashtag) + " ");
            }

            builder.createIntent();
            startActivityForResult(builder.createIntent(), TWITTER_REQUEST_CODE);
        });
    }

    public void addBottomOverlay() {

        try {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.overlay);
            int width = Math.round(mBitmap.getWidth() * getResources().getDisplayMetrics().density);
            int height = Math.round(mBitmap.getHeight() * Math.min(getResources().getDisplayMetrics().density, 2.0f));
            bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmp);

            overlay = Bitmap.createScaledBitmap(bitmap, width, width / 3, true);
            und = Bitmap.createScaledBitmap(mBitmap, width, height, true);
            und.setDensity(overlay.getDensity());

            canvas.drawBitmap(und, 0, 0, null);
            canvas.drawBitmap(overlay, 0, bmp.getHeight() - overlay.getHeight(), null);

            viewHolder.previewImage.setImageBitmap(bmp);
            overlay.recycle();
        }
        catch (OutOfMemoryError error) {

            error.printStackTrace();
        }
    }

//    public static Bitmap drawText(String text, int textWidth, int textSize) {
//        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
//        textPaint.setStyle(Paint.Style.FILL);
//        textPaint.setColor(Color.WHITE);
//        textPaint.setTextSize(textSize);
//        StaticLayout mTextLayout = new StaticLayout(text, textPaint, textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
//
//        Bitmap b = Bitmap.createBitmap(textWidth, mTextLayout.getHeight(), Bitmap.Config.RGB_565);
//        Canvas c = new Canvas(b);
//
//        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
//        paint.setStyle(Paint.Style.FILL);
//        paint.setColor(Color.WHITE);
//        c.drawPaint(paint);
//
//        c.save();
//        c.translate(0, 0);
//        mTextLayout.draw(c);
//        c.restore();
//
//        return b;
//    }

    public void removeOverlay() {

        viewHolder.previewImage.setImageBitmap(mOriginalBitmap);
        bitmap.recycle();
        overlay.recycle();
        und.recycle();
        bmp.recycle();
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TWITTER_REQUEST_CODE) {
            if (resultCode == RESULT_TWEET_CANCELLED) {
                Log.i(this.getClass().toString(), "Tweet Cancelled");
            } else if (resultCode == RESULT_TWEET_SUCCESS) {
                getActivity().onBackPressed();
            }
        }
    }

    private void writeExternalDenied() {
    }

    private void writeExternalApproved() {
    }
}
