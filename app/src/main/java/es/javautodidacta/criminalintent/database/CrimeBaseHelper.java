package es.javautodidacta.criminalintent.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static es.javautodidacta.criminalintent.database.CrimeDbSchema.CrimeTable.NAME;

/**
 * This class checks whether the database already exists:
 * * If it does, opens it up and see what version of the {@link CrimeDbSchema} it has. If it is an
 * old version, it upgrades to a newer version.
 * * If it doesn't, creates it and creates the tables and initial data it needs.
 *
 * @author Miguel Callejón Berenguer
 * @version 2018.01
 */

public class CrimeBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "crimeBase.db";


    public CrimeBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + NAME + "(" +
            " _id integer primary key autoincrement, " +
                CrimeDbSchema.CrimeTable.Cols.UUID + ", " +
                CrimeDbSchema.CrimeTable.Cols.TITLE + ", " +
                CrimeDbSchema.CrimeTable.Cols.DATE + ", " +
                CrimeDbSchema.CrimeTable.Cols.SOLVED + ", " +
                CrimeDbSchema.CrimeTable.Cols.SUSPECT + ", " +
                CrimeDbSchema.CrimeTable.Cols.PHONE_NUMBER + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
