package com.traversoft.gdgphotoshare.ui.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import com.traversoft.gdgphotoshare.R;

import butterknife.ButterKnife;

public class GDGButton
        extends AppCompatButton {

    private String font;
    private Context context;

    public GDGButton(Context context) {
        super(context);
        this.context = context;
    }

    public GDGButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setCustomFont(attrs);
        initializeWidget();
    }

    public GDGButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        setCustomFont(attrs);
        initializeWidget();
    }

    private void setCustomFont(AttributeSet attrs) {
        TypedArray a = this.context.getTheme().obtainStyledAttributes(attrs, R.styleable.GDGButton, 0, 0);
        try {
            this.font = a.getString(R.styleable.GDGButton_font_file);
        } finally {
            a.recycle();
        }
    }

    private void initializeWidget() {
        ButterKnife.bind(this);
        if (!this.font.isEmpty()) {
            setTypeface(TypeFaceManager.get(this.context, this.font));
        }
    }
}