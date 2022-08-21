package me.goldze.mvvmhabit.base;

import android.content.Context;
import androidx.annotation.NonNull;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.module.AppGlideModule;

@GlideModule
public class MyGlideModule extends AppGlideModule {
    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        // 设置磁盘缓存为50M，缓存在内部缓存目录
        int cacheSize100MegaBytes = 50 * 1024 * 1024;
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, cacheSize100MegaBytes));
        //builder.setDiskCache(new ExternalCacheDiskCacheFactory(context, cacheSize100MegaBytes));

        // 20%大的内存缓存作为 Glide 的默认值
        MemorySizeCalculator calculator = new MemorySizeCalculator.Builder(context).build();
        int defaultMemoryCacheSize = calculator.getMemoryCacheSize();
        int defaultBitmapPoolSize = calculator.getBitmapPoolSize();
        int customMemoryCacheSize = (int) (0.8 * defaultMemoryCacheSize);
        int customBitmapPoolSize = (int) (0.8 * defaultBitmapPoolSize);

        builder.setMemoryCache(new LruResourceCache(customMemoryCacheSize));
        builder.setBitmapPool(new LruBitmapPool(customBitmapPoolSize));
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
