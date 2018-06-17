package es.javautodidacta.criminalintent.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import es.javautodidacta.criminalintent.Crime;

/**
 * This class generates the database entity.
 *
 * @author Miguel Callejón Berenguer
 * @version 2018.06
 */
@Database(entities = {Crime.class}, version = 1)
public abstract class CrimeDatabase extends RoomDatabase {
    public abstract CrimeDao getCrimeDao();
}
