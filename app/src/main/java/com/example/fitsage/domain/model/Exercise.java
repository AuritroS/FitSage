package com.example.fitsage.domain.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Exercise implements Parcelable {
    private final String name;
    private final Integer sets;
    private final Integer reps;
    private final String duration;

    public Exercise(String name, int sets, int reps) {
        this.name     = name;
        this.sets     = sets;
        this.reps     = reps;
        this.duration = null;
    }

    public Exercise(String name, String duration) {
        this.name     = name;
        this.sets     = null;
        this.reps     = null;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public Integer getSets() {
        return sets;
    }

    public Integer getReps() {
        return reps;
    }

    public String getDuration() {
        return duration;
    }


    protected Exercise(Parcel in) {
        name = in.readString();
        if (in.readByte() == 0) {
            sets = null;
        } else {
            sets = in.readInt();
        }
        if (in.readByte() == 0) {
            reps = null;
        } else {
            reps = in.readInt();
        }
        duration = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        if (sets == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(sets);
        }
        if (reps == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(reps);
        }
        dest.writeString(duration);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Exercise> CREATOR = new Creator<Exercise>() {
        @Override
        public Exercise createFromParcel(Parcel in) {
            return new Exercise(in);
        }

        @Override
        public Exercise[] newArray(int size) {
            return new Exercise[size];
        }
    };
}
