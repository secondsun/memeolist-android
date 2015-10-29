package org.feedhenry.apps.arthenry;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.feedhenry.sdk.FH;
import com.feedhenry.sdk.FHActCallback;
import com.feedhenry.sdk.FHResponse;
import com.feedhenry.sdk.api2.FHAuthSession;
import com.feedhenry.sdk.sync.FHSyncClient;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements InitCallbackListener {

    private FHClient fhClient;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.fab) FloatingActionButton fab;
    @Bind(R.id.empty) View emptyView;
    @Bind(R.id.scrollView) View scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        this.fhClient = new FHClient.Builder(this)
                            .addInitCallback(this)
                            .addFeature(FHAuthSession.class)
                            .addFeature(FHSyncClient.class)
                            .build();

    }

    @OnClick(R.id.fab)
    public void fabClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        fhClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        fhClient.disconnect();
    }

    @Override
    public void onInit(FHResponse fhResponse) {
        Toast.makeText(this, "Success", Toast.LENGTH_LONG).show();
        emptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onInitError(FHResponse fhResponse) {
        Toast.makeText(this, "Failure", Toast.LENGTH_LONG).show();
    }
}
