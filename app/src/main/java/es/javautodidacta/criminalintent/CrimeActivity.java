package es.javautodidacta.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.util.UUID;

/**
 * Clase sustituida por CrimePagerActivity.
 * Clase reutilizada para el segundo challenge de la página 245.
 */

public class CrimeActivity extends SingleFragmentActivity {

    private static final String EXTRA_CRIME_ID = "es.javautodidacta.criminalintent.crime_id";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //DatePickerFragment.newInstance()
    }

    @Override
    protected Fragment createFragment() {
        String crimeId = (String) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        return CrimeFragment.newInstance(crimeId);
    }
}
