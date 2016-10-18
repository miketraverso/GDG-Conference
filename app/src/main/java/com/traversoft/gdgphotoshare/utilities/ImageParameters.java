package com.traversoft.gdgphotoshare.utilities;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;
import lombok.Setter;


public class ImageParameters implements Parcelable {

    @Getter @Setter private int displayOrientation;
    @Getter @Setter private int layoutOrientation;
    @Getter @Setter private int coverHeight;
    @Getter @Setter private int coverWidth;
    @Getter @Setter private int previewHeight;
    @Getter @Setter private int previewWidth;

    public ImageParameters(Parcel in) {
        this.displayOrientation = in.readInt();
        this.layoutOrientation = in.readInt();
        this.coverHeight = in.readInt();
        this.coverWidth = in.readInt();
        this.previewHeight = in.readInt();
        this.previewWidth = in.readInt();
    }

    public ImageParameters() {}

    public int calculateCoverWidthHeight() {
        return Math.abs(previewHeight - previewWidth);
    }

    public ImageParameters createCopy() {
        ImageParameters imageParameters = new ImageParameters();
        imageParameters.displayOrientation = this.displayOrientation;
        imageParameters.layoutOrientation = this.layoutOrientation;
        imageParameters.coverHeight = this.coverHeight;
        imageParameters.coverWidth = this.coverWidth;
        imageParameters.previewHeight = this.previewHeight;
        imageParameters.previewWidth = this.previewWidth;

        return imageParameters;
    }

    public String getStringValues() {
        return "cover height: " + this.coverHeight + " width: " + this.coverWidth
                + "\npreview height: " + this.previewHeight + " width: " + this.previewWidth;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.displayOrientation);
        dest.writeInt(this.layoutOrientation);
        dest.writeInt(this.coverHeight);
        dest.writeInt(this.coverWidth);
        dest.writeInt(this.previewHeight);
        dest.writeInt(this.previewWidth);
    }

    public static final Creator<ImageParameters> CREATOR = new Parcelable.Creator<ImageParameters>() {

        @Override
        public ImageParameters createFromParcel(Parcel source) {
            return new ImageParameters(source);
        }

        @Override
        public ImageParameters[] newArray(int size) {
            return new ImageParameters[size];
        }
    };
}

