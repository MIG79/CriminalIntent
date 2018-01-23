package es.javautodidacta.criminalintent.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.util.Log;

import java.util.Date;
import java.util.UUID;

import es.javautodidacta.criminalintent.Crime;
import es.javautodidacta.criminalintent.database.CrimeDbSchema.CrimeTable.Cols;

/**
 * This class takes care of the {@code Cursor}.
 *
 * @author Miguel Callejón Berenguer
 * @version 2018.01
 */

public class CrimeCursorWrapper extends CursorWrapper {

    private static final String TAG = "CrimeCursorWrapper";

    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        String uuidString = getString(getColumnIndex(Cols.UUID));
        String title = getString(getColumnIndex(Cols.TITLE));
        long date = getLong(getColumnIndex(Cols.DATE));
        int isSolved = getInt(getColumnIndex(Cols.SOLVED));
        String suspect = getString(getColumnIndex(Cols.SUSPECT));

        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        Log.e(TAG, "getCrime: " + date); // BAD DATE
        crime.setDate(new Date(date));
        crime.setSolved(isSolved != 0);
        crime.setSuspect(suspect);

        return crime;
    }
}
