package org.aerogear.apps.memeolist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.aerogear.apps.memeolist.events.InitSuccessful;
import org.aerogear.apps.memeolist.events.InitFailed;
import org.aerogear.apps.memeolist.fh.ConnectionFailure;
import org.aerogear.apps.memeolist.fh.FHClient;
import org.aerogear.apps.memeolist.fh.auth.FHAuthUtil;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SplashScreen extends AppCompatActivity {

    private static final int SIGN_IN = 0xdeadbeef;

    @Bind(R.id.progress_bar) ProgressBar progress;
    @Bind(R.id.login_button) Button logInButton;

    @Inject
    FHClient fhClient;
    @Inject
    Bus bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spash_screen);
        ButterKnife.bind(this);
        initInjection();

    }

    private void initInjection() {
        ((MemeolistApplication)getApplicationContext()).getObjectGraph().inject(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (!fhClient.isConnected()) {
            fhClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Subscribe
    public void onInit(InitSuccessful event) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Subscribe
    public void onInitError(final InitFailed event) {
        final ConnectionFailure failure = event.getConnectionFailure();
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
