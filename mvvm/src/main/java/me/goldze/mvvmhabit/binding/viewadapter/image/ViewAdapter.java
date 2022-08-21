package me.goldze.mvvmhabit.binding.viewadapter.image;


import androidx.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import me.goldze.mvvmhabit.R;

/**
 * Created by goldze on 2017/6/18.
 */
public final class ViewAdapter {
    @BindingAdapter(value = {"url", "placeholderRes"}, requireAll = false)
    public static void setImageUri(ImageView imageView, String url, int placeholderRes) {
        // if (!TextUtils.isEmpty(url)) {
        //使用Glide框架加载图片
        if (imageView == null || url == null) {
            return;
        }
        if (url.startsWith("http") && !url.contains("?x-oss-process=image/resize,w_260,h_260,m_fill/auto-orient,0/quality,q_90/format,src")) {
            url = url + "?x-oss-process=image/resize,w_260,h_260,m_fill/auto-orient,0/quality,q_90/format,src";
        }
        Glide.with(imageView.getContext())
                .load(url)
                .apply(new RequestOptions().placeholder(placeholderRes != 0 ? placeholderRes : R.drawable.image_default).error(placeholderRes != 0 ? placeholderRes : R.drawable.image_default))
                .into(imageView);
        //}
    }
}

