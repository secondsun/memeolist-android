package org.feedhenry.apps.arthenry;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.feedhenry.sdk.FHResponse;
import com.feedhenry.sdk.sync.FHSyncListener;
import com.feedhenry.sdk.sync.NotificationMessage;

import org.feedhenry.apps.arthenry.fh.ConnectionFailure;
import org.feedhenry.apps.arthenry.fh.FHClient;
import org.feedhenry.apps.arthenry.fh.auth.FHAuthClientConfig;
import org.feedhenry.apps.arthenry.fh.sync.FHSyncClientConfig;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements InitCallbackListener, FHSyncListener {

    private FHClient fhClient;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.empty)
    View emptyView;
    @Bind(R.id.scrollView)
    View scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        this.fhClient = new FHClient.Builder(this)
                .addInitCallback(this)
                .addFeature(new FHSyncClientConfig(this)
                        .addDataSet("photos")
                        .setNotifySyncStarted(true)
                        .setAutoSyncLocalUpdates(true)
                        .setNotifySyncComplete(true)
                        .setSyncFrequencySeconds(10))
                .addFeature(new FHAuthClientConfig("Google")
                        .setCallingActivity(this))
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
        emptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onInitError(ConnectionFailure fhResponse) {
        if (fhResponse.hasResolution()) {
            fhResponse.resolve(this, fhResponse.getResponseCode());
        } else {
            Toast.makeText(this, "Failure", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSyncStarted(NotificationMessage notificationMessage) {
        Toast.makeText(this, "Sync Started", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSyncCompleted(NotificationMessage notificationMessage) {

    }

    @Override
    public void onUpdateOffline(NotificationMessage notificationMessage) {

    }

    @Override
    public void onCollisionDetected(NotificationMessage notificationMessage) {

    }

    @Override
    public void onRemoteUpdateFailed(NotificationMessage notificationMessage) {

    }

    @Override
    public void onRemoteUpdateApplied(NotificationMessage notificationMessage) {

    }

    @Override
    public void onLocalUpdateApplied(NotificationMessage notificationMessage) {

    }

    @Override
    public void onDeltaReceived(NotificationMessage notificationMessage) {

    }

    @Override
    public void onSyncFailed(NotificationMessage notificationMessage) {

    }

    @Override
    public void onClientStorageFailed(NotificationMessage notificationMessage) {

    }
}
