package es.javautodidacta.criminalintent.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;
import java.util.UUID;

import es.javautodidacta.criminalintent.Crime;

/**
 * This interface handles the database.
 *
 * @author Miguel Callejón Berenguer
 * @version 2018.06
 */
@Dao
public interface CrimeDao {
    @Query("SELECT * FROM crime")
    List<Crime> getCrimes();
    @Query("SELECT * FROM crime WHERE mId LIKE :id")
    Crime getCrime(String id);
    @Insert
    void addCrime(Crime crime);
    @Delete
    void deleteCrime(Crime crime);
    @Update
    void updateCrime(Crime crime);
}
