package es.javautodidacta.criminalintent;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;
import java.util.UUID;
@Entity
public class Crime {
    @PrimaryKey
    @NonNull
    private String mId;
    @ColumnInfo(name="title")
    private String mTitle = "";
    @ColumnInfo(name="date")
    private String mDate;
    @ColumnInfo(name="solved")
    private boolean mSolved;
    @ColumnInfo(name="suspect")
    private String mSuspect;

    public Crime() {
        this(UUID.randomUUID().toString());
    }

    public Crime(@NonNull String id) {
        mId = id;
        mDate = new Date().toString();
    }

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    public String getPhotoFilename(){
        return "IMG_" + getId() + ".jpg";
    }
    public void setId(@NonNull String id) {
        mId = id;
    }
    @NonNull
    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }
}
