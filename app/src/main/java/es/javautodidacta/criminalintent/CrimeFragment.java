package es.javautodidacta.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class CrimeFragment extends Fragment {

    public static final int REQUEST_PHOTO = 2;
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_PHOTO = "DialogPhoto";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;

    private Crime mCrime;
    private CrimeLab crimeLab;
    private File mPhotoFile;

    private TextView crimeLabel;
    private EditText mTitleField;
    private TextView crimeDetailsLabel;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;

    private Point mPhotoViewSize;
    private Callbacks mCallbacks;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        crimeLab = CrimeLab.get(getActivity());
        assert getArguments() != null;
        String crimeId = (String) getArguments().getSerializable(ARG_CRIME_ID);
        if (crimeId != null) {
            mCrime = crimeLab.getCrime(crimeId);
            mPhotoFile = crimeLab.getPhotoFile(mCrime);
        }
        setHasOptionsMenu(true);
    }

    public static CrimeFragment newInstance(String crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCrime != null) {
            crimeLab.updateCrime(mCrime);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        crimeLabel = v.findViewById(R.id.crime_title_label);
        mTitleField = v.findViewById(R.id.crime_title);
        crimeDetailsLabel = v.findViewById(R.id.details_label);
        mSuspectButton = v.findViewById(R.id.crime_suspect);
        mReportButton = v.findViewById(R.id.crime_report);
        mDateButton = v.findViewById(R.id.crime_date);
        mSolvedCheckBox = v.findViewById(R.id.crime_solved);
        mPhotoButton = v.findViewById(R.id.crime_camera);
        mPhotoView = v.findViewById(R.id.crime_photo);

        if (mCrime != null) {
            crimeLabel.setText(getString(R.string.crime_title_label));

            mTitleField.setText(mCrime.getTitle());
            mTitleField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // This space intentionally left blank
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mCrime.setTitle(s.toString());
                    updateCrime();
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    // This space intentionally left blank
                }
            });

            final Intent pickContact = new Intent(Intent.ACTION_PICK,
                                                  ContactsContract.Contacts.CONTENT_URI);
            mSuspectButton.setOnClickListener(view ->
                                                      startActivityForResult(pickContact,
                                                                             REQUEST_CONTACT));

            if (mCrime.getSuspect() != null) {
                mSuspectButton.setText(mCrime.getSuspect());
            }

            PackageManager packageManager =
                    Objects.requireNonNull(getActivity()).getPackageManager();
            if (packageManager.resolveActivity(pickContact,
                                               PackageManager.MATCH_DEFAULT_ONLY) == null) {
                mSuspectButton.setEnabled(false);
            }

            mReportButton.setOnClickListener(view -> {

                Intent i = ShareCompat.IntentBuilder.from(getActivity())
                                                    .setText(getCrimeReport())
                                                    .setSubject(getString(
                                                            R.string.crime_report_subject))
                                                    .setType("text/plain").getIntent();
                i = Intent.createChooser(i, getString(R.string.send_report));
                if (i.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(i);
                }

            });

            updateDate();
            mDateButton.setOnClickListener(view -> {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                assert manager != null;
                dialog.show(manager, DIALOG_DATE);
            });

            mSolvedCheckBox.setChecked(mCrime.isSolved());
            mSolvedCheckBox.setOnCheckedChangeListener(
                    (CompoundButton compoundButton, boolean isChecked) -> {
                        mCrime.setSolved(isChecked);
                        updateCrime();
                    });


            final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            boolean canTakePhoto = mPhotoFile != null &&
                                   captureImage.resolveActivity(packageManager) != null;
            mPhotoButton.setEnabled(canTakePhoto);
            mPhotoButton.setOnClickListener(view -> {
                Uri uri = FileProvider.getUriForFile(getActivity(),
                                                     "es.javautodidacta.criminalintent.fileprovider",
                                                     mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage,
                                                                   PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                                                     uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                startActivityForResult(captureImage, REQUEST_PHOTO);
            });

            mPhotoView.setOnClickListener(view -> {
                FragmentManager manager = getFragmentManager();
                ZoomedPhotoFragment dialog = ZoomedPhotoFragment.newInstance(mCrime.getId());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_PHOTO);
                assert manager != null;
                dialog.show(manager, DIALOG_PHOTO);
            });

            mPhotoView.getViewTreeObserver()
                      .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                          @Override
                          public void onGlobalLayout() {
                              boolean isFirstPass = (mPhotoViewSize == null);
                              mPhotoViewSize = new Point();
                              mPhotoViewSize.set(mPhotoView.getWidth(), mPhotoView.getHeight());

                              if (isFirstPass) {
                                  updatePhotoView();
                              }

                              mPhotoView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                          }
                      });

            visibleViews(true);

            updatePhotoView();
        } else {
            visibleViews(false);
        }
        return v;
    }

    private void visibleViews(boolean visible) {
        int visibilidad = visible ? View.VISIBLE : View.GONE;
        crimeLabel.setVisibility(visibilidad);
        crimeDetailsLabel.setVisibility(visibilidad);
        mTitleField.setVisibility(visibilidad);
        mDateButton.setVisibility(visibilidad);
        mSolvedCheckBox.setVisibility(visibilidad);
        mReportButton.setVisibility(visibilidad);
        mSuspectButton.setVisibility(visibilidad);
        mPhotoButton.setVisibility(visibilidad);
        mPhotoView.setVisibility(visibilidad);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_crime:
                crimeLab.deleteCrime(mCrime);
                mCallbacks.onCrimeDeleted(getActivity());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateCrime() {
        crimeLab.updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK) {
            return;
        }

        if(requestCode == REQUEST_DATE) {
            String date = (String) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateCrime();
            updateDate();
        } else if(requestCode == REQUEST_CONTACT && data != null) {

            Uri contactUri = data.getData();

            // Specify which fields you want your query to return values for
            String[] queryFields = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME
            };

            // Perform your query - the contactUri is like a "where" clause here
            try(Cursor c = Objects.requireNonNull(getActivity()).getContentResolver()
                                  .query(Objects.requireNonNull(contactUri), queryFields, null, null, null)) {

                // Double-check that you actually got results
                if(Objects.requireNonNull(c).getCount() == 0) {
                    return;
                }

                // Pull out the first column of the first row of data - that is your suspect's name
                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                updateCrime();
                mSuspectButton.setText(suspect);
            }
        } else if(requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(Objects.requireNonNull(getActivity()),
                                                 "es.javautodidacta.criminalintent.fileprovider",
                                                 mPhotoFile);
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            updateCrime();
            updatePhotoView();
        }
    }

    /**
     * Required interface for hosting activities.
     */
    public interface Callbacks {
        void onCrimeUpdated(Crime crime);

        void onCrimeDeleted(Activity activity);
    }

    private void updatePhotoView() {
        if(mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
            mPhotoView.setContentDescription(getString(R.string.crime_photo_no_image_description));
        } else {

            Bitmap bitmap = (mPhotoViewSize == null) ?
                    PictureUtils
                            .getScaledBitmap(mPhotoFile.getPath(),
                                             Objects.requireNonNull(getActivity())) :
                    PictureUtils
                            .getScaledBitmap(mPhotoFile.getPath(),
                                    mPhotoViewSize.x, mPhotoViewSize.y);
            mPhotoView.setImageBitmap(bitmap);
            mPhotoView.setContentDescription(getString(R.string.crime_photo_image_description));
        }
    }

    private void updateDate() {
        mDateButton.setText(mCrime.getDate());
    }

    private String getCrimeReport() {
        String solvedString;
        if(mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateString = mCrime.getDate();

        String suspect = mCrime.getSuspect();
        if(suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        return getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, suspect);
    }
}