package org.feedhenry.apps.arthenry;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.feedhenry.sdk.FHResponse;

import org.feedhenry.apps.arthenry.fh.ConnectionFailure;
import org.feedhenry.apps.arthenry.fh.FHClient;
import org.feedhenry.apps.arthenry.fh.auth.FHAuthClientConfig;
import org.feedhenry.apps.arthenry.fh.auth.FHAuthUtil;
import org.feedhenry.apps.arthenry.util.InitCallbackListener;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SplashScreen extends AppCompatActivity implements InitCallbackListener {

    private static final int SIGN_IN = 0xdeadbeef;
    @Bind(R.id.progress_bar) ProgressBar progress;
    @Bind(R.id.login_button) Button logInButton;
    private FHClient fhClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spash_screen);
        ButterKnife.bind(this);

        initFeedHenry();

    }

    private void initFeedHenry() {
        this.fhClient = new FHClient.Builder(this)
                .addInitCallback(this)
                .addFeature(new FHAuthClientConfig("Google")
                        .setCallingActivity(this))
                .build();

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
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onInitError(final ConnectionFailure failure) {
        if (failure.getResponseCode() == FHAuthUtil.SIGN_IN_REQUIRED) {
            progress.setVisibility(View.GONE);
            logInButton.setVisibility(View.VISIBLE);
            logInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        failure.resolve(SplashScreen.this, SIGN_IN);
                }
            });
        } else {
            Log.e("SPLASH", failure.getResponse().getJson().toString());
            throw new RuntimeException(failure.getResponse().getErrorMessage());
        }
    }

}
