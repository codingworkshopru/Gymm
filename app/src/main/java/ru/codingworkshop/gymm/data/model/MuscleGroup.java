package ru.codingworkshop.gymm.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Радик on 14.02.2017.
 */

public final class MuscleGroup implements Parcelable {
    private long id;
    private String name;

    public MuscleGroup(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Parcelable implementation
    private MuscleGroup(Parcel in) {
        id = in.readLong();
        name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
    }

    public static final Creator<MuscleGroup> CREATOR = new Creator<MuscleGroup>() {
        @Override
        public MuscleGroup createFromParcel(Parcel in) {
            return new MuscleGroup(in);
        }

        @Override
        public MuscleGroup[] newArray(int size) {
            return new MuscleGroup[size];
        }
    };
}
