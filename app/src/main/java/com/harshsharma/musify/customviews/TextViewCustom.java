package com.harshsharma.musify.customviews;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import com.harshsharma.musify.R;


// CustomTextView with dynamic font in layouts
@SuppressLint("AppCompatCustomView")
public class TextViewCustom extends TextView {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TextViewCustom(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    public TextViewCustom(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public TextViewCustom(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TextViewCustom(Context context) {
        super(context);
        init(null);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            @SuppressLint("CustomViewStyleable") TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.Custom);
            String fontName = a.getString(R.styleable.Custom_fonts);

            try {
                if (fontName != null) {
                    Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + fontName);
                    setTypeface(myTypeface);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            a.recycle();
        }
    }
}