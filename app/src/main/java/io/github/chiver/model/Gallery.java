package io.github.chiver.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Gallery implements Parcelable {

    public static final Parcelable.Creator<Gallery> CREATOR
            = new Parcelable.Creator<Gallery>() {
        public Gallery createFromParcel(Parcel in) {
            return new Gallery(in);
        }

        public Gallery[] newArray(int size) {
            return new Gallery[size];
        }
    };

    public final String title;
    public final String imageSource;
    public final String link;
    public final List<GalleryItem> galleryItems;

    public Gallery(String title, String imageSource, String link, List<GalleryItem> galleryItems) {
        this.title = title;
        this.imageSource = imageSource;
        this.link = link;
        this.galleryItems = galleryItems;
    }

    private Gallery(Parcel in) {
        this(in.readString(), in.readString(), in.readString(), toGalleryItems(in.createStringArray()));
    }

    private static List<GalleryItem> toGalleryItems(String[] urls) {
        return Arrays.stream(urls).map(GalleryItem::new).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Gallery gallery = (Gallery) o;
        return Objects.equals(link, gallery.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(link);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(imageSource);
        dest.writeString(link);
        dest.writeStringArray(galleryItems.stream().map(gi -> gi.imageSource).toArray(String[]::new));
    }
}
