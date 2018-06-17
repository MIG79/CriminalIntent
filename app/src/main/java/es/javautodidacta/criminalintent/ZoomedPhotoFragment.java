package es.javautodidacta.criminalintent;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.util.Objects;

/**
 * This class opens a Dialog with the zoomed-in thumbnail
 *
 * @author Miguel Callejón Berenguer
 * @version 2018.01
 */

public class ZoomedPhotoFragment extends DialogFragment {

    public static final String ARG_FILE = "file";

    private static final String TAG = "ZoomedPhotoFragment";

    private ImageView mImageView;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String id = (String) Objects.requireNonNull(getArguments()).getSerializable(ARG_FILE);
        Crime mCrime = CrimeLab.get(getActivity()).getCrime(id);

        File mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);

        @SuppressLint("InflateParams")
        View v = LayoutInflater.from(getActivity())
                               .inflate(R.layout.dialog_photo, null);

        mImageView = v.findViewById(R.id.dialog_photo);

        Bitmap bitmap = BitmapFactory.decodeFile(mPhotoFile.toString());

        mImageView.setImageBitmap(bitmap);

        return new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setView(v)
                .setTitle(R.string.detail)
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }

    public static ZoomedPhotoFragment newInstance(String crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_FILE, crimeId);

        ZoomedPhotoFragment fragment = new ZoomedPhotoFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
