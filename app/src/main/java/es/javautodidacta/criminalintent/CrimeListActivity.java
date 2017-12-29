package es.javautodidacta.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by octubre on 29/12/17.
 */

public class CrimeListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
