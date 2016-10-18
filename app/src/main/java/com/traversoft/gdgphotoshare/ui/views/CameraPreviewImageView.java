package com.traversoft.gdgphotoshare.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CameraPreviewImageView
        extends ImageView {

    public CameraPreviewImageView(Context context) {
        super(context);
    }

    public CameraPreviewImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraPreviewImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        if (width > height * (3.0/4.0)) {
            width = (int) (height * (3.0/4.0) + 0.5);
        } else {
            height = (int) (width / (3.0/4.0) + 0.5);
        }
        setMeasuredDimension(width, height);
    }
}
