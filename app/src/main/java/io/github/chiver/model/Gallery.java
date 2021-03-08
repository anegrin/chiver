package io.github.chiver.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

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

    public Gallery(String title, String imageSource, String link) {
        this.title = title;
        this.imageSource = imageSource;
        this.link = link;
    }

    private Gallery(Parcel in) {
        this(in.readString(), in.readString(), in.readString());
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
    }
}
