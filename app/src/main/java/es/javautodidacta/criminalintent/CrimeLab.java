package es.javautodidacta.criminalintent;

import android.annotation.SuppressLint;
import android.arch.persistence.room.Room;
import android.content.Context;

import java.io.File;
import java.util.List;

import es.javautodidacta.criminalintent.database.CrimeDao;
import es.javautodidacta.criminalintent.database.CrimeDatabase;

public class CrimeLab {
    @SuppressLint("StaticFieldLeak")
    private static CrimeLab sCrimeLab;
    private static CrimeDao mCrimeDao;
    private Context mContext;

    public static CrimeLab get(Context context) {
        if(sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        //Allow Main Thread Queries for small databases.
        CrimeDatabase database = Room.databaseBuilder(mContext,
                                                      CrimeDatabase.class,
                                                      "crimes")
                                     .allowMainThreadQueries().build();
        mCrimeDao = database.getCrimeDao();
    }

    public void addCrime(Crime c) {
        mCrimeDao.addCrime(c);
    }

    public List<Crime> getCrimes() {
        return mCrimeDao.getCrimes();
    }

    public Crime getCrime(String id) {
        return mCrimeDao.getCrime(id);
    }

    public File getPhotoFile(Crime crime) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, crime.getPhotoFilename());
    }

    public void deleteCrime(Crime crime) {
        mCrimeDao.deleteCrime(crime);
    }

    public void updateCrime(Crime crime) {
        mCrimeDao.updateCrime(crime);
    }
}
