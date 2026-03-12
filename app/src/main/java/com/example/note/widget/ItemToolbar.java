package com.example.note.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.note.R;

public class ItemToolbar extends ConstraintLayout {

    private ImageView ivBack;
    private TextView tvTitle;

    public ItemToolbar(Context context) {
        super(context);
        init(context);
    }

    public ItemToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.item_toolbar, this, true);
        ivBack = findViewById(R.id.ivBack);
        tvTitle = findViewById(R.id.tvTitle);
    }

    public void setTitleText(CharSequence title) {
        tvTitle.setText(title);
    }

    public void setBackClickListener(OnClickListener listener) {
        ivBack.setOnClickListener(listener);
    }

    public void setBackVisible(boolean visible) {
        ivBack.setVisibility(visible ? VISIBLE : GONE);
    }
}