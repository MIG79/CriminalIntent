package es.javautodidacta.criminalintent;

import android.util.Log;

import java.sql.Time;
import java.util.Date;
import java.util.UUID;

public class Crime {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private Time mTime;
    private boolean mSolved;
    private boolean mRequiresPolice;

    public Crime() {
        mId = UUID.randomUUID();
        mDate = new Date();
        mTime = new Time(mDate.getTime());
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public boolean isRequiresPolice() {
        return mRequiresPolice;
    }

    public void setRequiresPolice(boolean requiresPolice) {
        mRequiresPolice = requiresPolice;
    }

    public void setTime(long time) {
        mTime = new Time(time);
    }

    public Time getTime() {
        return mTime;
    }
}
