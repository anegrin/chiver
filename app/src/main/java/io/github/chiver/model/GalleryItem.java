package io.github.chiver.model;

import android.util.Log;

import java.net.URL;
import java.util.Objects;

import io.github.chiver.Constants;

public class GalleryItem {

    public enum Type {

        STATIC, ANIMATED, UNKNOWN;

        public static Type forURL(String src) {
            if (src != null) {
                try {
                    String file = new URL(src).getFile();
                    return file.toLowerCase().endsWith(".gif") ? ANIMATED : STATIC;
                } catch (Throwable t) {
                    Log.e(Constants.TAG, "Can't get item type from src", t);
                }
            }

            return UNKNOWN;
        }
    }

    public final String imageSource;
    public final Type type;

    public GalleryItem(String imageSource) {
        this.imageSource = imageSource;
        this.type = Type.forURL(imageSource);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GalleryItem that = (GalleryItem) o;
        return Objects.equals(imageSource, that.imageSource);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageSource);
    }

}
