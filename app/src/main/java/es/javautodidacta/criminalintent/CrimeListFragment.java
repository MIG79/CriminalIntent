package es.javautodidacta.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CrimeListFragment extends Fragment {

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private RecyclerView mCrimeRecyclerView;
    private TextView mNoCrimeTitle;
    private Button mNoCrimeButton;
    private CrimeAdapter mAdapter;

    private boolean mSubtitleVisible;

    private Callbacks mCallBacks;

    /**
     * Required interface for hosting activities
     */
    public interface Callbacks {
        void onCrimeSelected(Crime crime);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallBacks = (Callbacks) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallBacks = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mNoCrimeTitle = view.findViewById(R.id.no_crimes);
        mNoCrimeButton = view.findViewById(R.id.no_crimes_button);
        mNoCrimeButton.setContentDescription(getString(R.string.no_crimes_button_description));
        mNoCrimeButton.setOnClickListener(v -> {
            Crime crime = new Crime();
            CrimeLab.get(getActivity()).addCrime(crime);
            Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
            startActivity(intent);
        });

        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        mCrimeRecyclerView = view.findViewById(R.id.crime_recycler_view);

        updateUI();

        ItemTouchHelper.Callback swipeController =
                new SwipeController(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(swipeController);
        touchHelper.attachToRecyclerView(mCrimeRecyclerView);

        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    public void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if(crimes.size() == 0) {
            mNoCrimeButton.setVisibility(View.VISIBLE);
            mNoCrimeTitle.setVisibility(View.VISIBLE);
            mCrimeRecyclerView.setVisibility(View.GONE);
        } else {
            mCrimeRecyclerView.setVisibility(View.VISIBLE);
            mNoCrimeButton.setVisibility(View.GONE);
            mNoCrimeTitle.setVisibility(View.GONE);
        }

        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            // mAdapter.notifyItemChanged(indexSelected); "Delete crime" does not work with this.
            mAdapter.setCrimes(crimes);
            mAdapter.notifyDataSetChanged();
        }

        updateSubtitle();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    /**
     * Defines the behavior of the menu buttons
     *
     * @param item {@code MenuItem} object which created from the item selected.
     * @return true if not default behavior (nothing).
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                updateUI();
                mCallBacks.onCrimeSelected(crime);
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                Objects.requireNonNull(getActivity()).invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This method shows a subtitle (number of crimes) under the title in the toolbar.
     */
    private void updateSubtitle() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getResources()
                .getQuantityString(R.plurals.subtitle_plural, crimeCount, crimeCount);

        if (!mSubtitleVisible) {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        assert activity != null;
        Objects.requireNonNull(activity.getSupportActionBar()).setSubtitle(subtitle);
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private Crime mCrime;
        private ImageView mSolvedImageView;

        CrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_crime, parent, false));
            //itemView is inherited from ViewHolder.
            mTitleTextView = itemView.findViewById(R.id.crime_title);
            mDateTextView = itemView.findViewById(R.id.crime_date);
            mSolvedImageView = itemView.findViewById(R.id.crime_solved);

            itemView.setOnClickListener(this);
        }

        public void bind(Crime crime) {
            mCrime = crime;
            String dateToPrint = mCrime.getDate();
            mTitleTextView.setText(mCrime.getTitle());
            mTitleTextView.setContentDescription(mCrime.getTitle());
            mDateTextView.setText(dateToPrint);
            mDateTextView.setContentDescription(getString(R.string.Crime_commited) + dateToPrint);
            if(crime.isSolved()) {
                mSolvedImageView.setVisibility(View.VISIBLE);
                mSolvedImageView.setContentDescription(getString(R.string.crime_solved));
            } else {
                mSolvedImageView.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onClick(View view) {
            mCallBacks.onCrimeSelected(mCrime);
        }
    }

    private class CrimeAdapter
            extends RecyclerView.Adapter<CrimeHolder>
            implements ItemTouchHelperAdapter {

        private List<Crime> mCrimes;

        CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        void setCrimes(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new CrimeHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        @Override
        public void onItemMove(int fromPosition, int toPosition) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(mCrimes, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(mCrimes, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);

            for(Crime crime : mCrimes) {
                CrimeLab.get(getActivity()).deleteCrime(crime);
                CrimeLab.get(getActivity()).addCrime(crime);
            }

        }

        @Override
        public void onItemDismiss(int position) {
            Crime crime = mCrimes.remove(position);
            notifyItemRemoved(position);
            CrimeLab.get(getActivity()).deleteCrime(crime);
            updateUI();
        }
    }
}