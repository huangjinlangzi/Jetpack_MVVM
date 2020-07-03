package com.example.wushengqing.utils;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.example.wushengqing.R;

public class BindingImageUtil {
    @BindingAdapter("avatar")
    public static void showImageByUrl(final ImageView imageView, int resId){
        Glide.with(imageView.getContext()).load(resId)
                .placeholder(R.mipmap.img_collect_commodity_icon)
                .error(R.mipmap.img_collect_commodity_icon)
                .into(imageView);
    }
}
