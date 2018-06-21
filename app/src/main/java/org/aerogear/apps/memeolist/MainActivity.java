package org.aerogear.apps.memeolist;

import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;

import com.squareup.otto.Bus;
import com.squareup.picasso.Picasso;

import org.aerogear.apps.memeolist.util.ImagePickerUtil;
import org.aerogear.apps.memeolist.view.CreateProjectDialog;
import org.aerogear.apps.memeolist.view.MainFragment;
import org.aerogear.apps.memeolist.view.ProjectDetailDialog;
import org.aerogear.apps.memeolist.vo.Project;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    public static final int PICK_IMAGE_ID = 0x5309;
    public static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 0x42;
    private static final String IS_TABLET = "MainActivity.IS_TABLET";

    public boolean isTablet = false;


    @Nullable
    @Bind(R.id.wide)
    View wide;

    @Nullable @Bind(R.id.narrow)
    View narrow;

    @Inject
    Bus bus;

    @Inject
    Picasso picasso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        ((MemeolistApplication) getApplicationContext()).getObjectGraph().inject(this);

        isTablet = wide != null;//if wide the is tablet, if not then not tablet

        setupView();

    }

    private void setupView() {
        FragmentTransaction tx = getFragmentManager().beginTransaction();
        tx.add(R.id.fragment_container, MainFragment.newInstance(isTablet));
        tx.addToBackStack(null);
        tx.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (!isTablet && keyCode == KeyEvent.KEYCODE_BACK)
        {
            if (getFragmentManager().getBackStackEntryCount() == 0)
            {
                this.finish();
                return false;
            }
            else
            {
                getFragmentManager().popBackStack();

                return false;
            }



        }
        return super.onKeyDown(keyCode, event);
    }



    public void showPopup(Project project) {

        DialogFragment dialog = ProjectDetailDialog.newInstance(project);
        if (isTablet) {
            dialog.show(getFragmentManager(), "DETAIL");
        }
        else {
            FragmentTransaction tx = getFragmentManager().beginTransaction();
            tx.replace(R.id.fragment_container, dialog, "DETAIL");
            tx.addToBackStack(null);
            tx.commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case PICK_IMAGE_ID:

                Uri uri = ImagePickerUtil.getImageUriFromResult(this, resultCode, data);
                if (uri != null) {
                    DialogFragment dialog = CreateProjectDialog.newInstance(uri);
                    if (isTablet) {
                        dialog.show(getFragmentManager(), "CREATE");
                    }
                    else {
                        FragmentTransaction tx = getFragmentManager().beginTransaction();
                        tx.replace(R.id.fragment_container, dialog, "CREATE");
                        tx.addToBackStack(null);
                        tx.commit();
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_STORAGE: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    ((CreateProjectDialog)getFragmentManager().findFragmentByTag("CREATE")).onResume();
                } else {
                    if (isTablet) {
                        ((DialogFragment) getFragmentManager().findFragmentByTag("CREATE")).dismiss();
                    } else {
                        getFragmentManager().popBackStack();
                    }

                }
                return;
            }


        }
    }



    @OnClick(R.id.fab)
    public void fabClick(View view) {
        Intent chooseImageIntent = ImagePickerUtil.getPickImageIntent(this);
        startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);

    }


}



