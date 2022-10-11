package com.jiayen.broadcast;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Text implements Parcelable {
    private String string = "hello,cb";

    public Text() {
        setString("hello,cb");
    }

    public Text(Parcel parcel) {
        setString(parcel.readString());
    }

    public static final Parcelable.Creator<Text> CREATOR = new Parcelable.Creator<Text>() {

        public Text createFromParcel(Parcel source) {
            return new Text(source);
        }

        public Text[] newArray(int size) {
            return new Text[size];
        }
    };

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(string);
    }

    @NonNull
    @Override
    public String toString() {
        return "string=" + this.string;
    }
}

