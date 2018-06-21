package org.aerogear.apps.memeolist.view;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.aerogear.apps.memeolist.MemeolistApplication;
import org.aerogear.apps.memeolist.MainActivity;
import org.aerogear.apps.memeolist.R;
import org.aerogear.apps.memeolist.service.UploadService;

import java.io.FileNotFoundException;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by summers on 11/5/15.
 */
public class CreateProjectDialog extends DialogFragment {

    private static final String IMAGE_URI = "CreateProjectDialog.IMAGE_URI";

    private Uri imageUri;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.top_comment)
    EditText topComment;

    @Bind(R.id.bottom_comment)
    EditText bottomComment;

    @Bind(R.id.imageView)
    ImageView image;

    @Inject
    Picasso picasso;


    public static CreateProjectDialog newInstance(Uri imageUri) {
        CreateProjectDialog dialog = new CreateProjectDialog();
        Bundle args = new Bundle();
        args.putParcelable(IMAGE_URI, imageUri);
        dialog.setArguments(args);
        return dialog;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MemeolistApplication)activity.getApplicationContext()).getObjectGraph().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_project_layout, null);
        ButterKnife.bind(this, view);
        toolbar.setTitle("Create Meme");
        imageUri = getArguments().getParcelable(IMAGE_URI);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MainActivity.MY_PERMISSIONS_REQUEST_READ_STORAGE);
        } else {
            AssetFileDescriptor fileDescriptor = null;
            try {
                fileDescriptor = getActivity().getContentResolver().openAssetFileDescriptor(imageUri, "r");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            picasso.with(getActivity()).load(imageUri).into(image);
        }
    }

    @OnClick(R.id.submit_button)
    public void submitProject() {

        Intent intent = new Intent( getActivity(), UploadService.class);
        intent.putExtra(UploadService.FILE_URI, imageUri);
        intent.putExtra(UploadService.TOP_MESSAGE, topComment.getText().toString());
        intent.putExtra(UploadService.BOTTOM_MESSAGE, bottomComment.getText().toString());

        getActivity().startService(intent);

        if (((MainActivity)getActivity()).isTablet) {
            dismiss();
        } else {
            getActivity().getFragmentManager().popBackStack();
        }
    }

}
