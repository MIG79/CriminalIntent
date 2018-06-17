package es.javautodidacta.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class CrimeListActivity extends SingleFragmentActivity
        implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks {
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    public void onCrimeSelected(Crime crime) {
        if (findViewById(R.id.detail_fragment) == null) {
            Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
            startActivity(intent);
        } else {
            getDetail(crime.getId());
        }
    }

    private void getDetail(String id) {
        Fragment newDetail = CrimeFragment.newInstance(id);
        getSupportFragmentManager().beginTransaction()
                                   .replace(R.id.detail_fragment, newDetail)
                                   .commit();
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        updateListUI();
    }

    /**
     * Method to be executed when double layout used.
     *
     * @param activity The container of the fragment.
     */
    @Override
    public void onCrimeDeleted(Activity activity) {
        getDetail(null);
        updateListUI();
    }

    private void updateListUI() {
        CrimeListFragment listFragment = (CrimeListFragment)
                getSupportFragmentManager().findFragmentById(R.id.list_fragment);
        listFragment.updateUI();
    }
}
