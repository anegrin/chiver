package io.github.chiver.util;

import android.graphics.Bitmap;

import com.android.volley.toolbox.ImageLoader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import cz.fhucho.android.util.SimpleDiskCache;

public class CachingImageLoader implements ImageLoader.ImageCache {

    private final SimpleDiskCache simpleDiskCache;

    public CachingImageLoader(SimpleDiskCache simpleDiskCache) {
        this.simpleDiskCache = simpleDiskCache;
    }

    @Override
    public Bitmap getBitmap(String url) {
        try {
            if (simpleDiskCache.contains(url)) {
                return simpleDiskCache.getBitmap(url).getBitmap();
            } else {
                return null;
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, out);
            byte[] imageArray = out.toByteArray();
            simpleDiskCache.put(url, new ByteArrayInputStream(imageArray));
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

}
