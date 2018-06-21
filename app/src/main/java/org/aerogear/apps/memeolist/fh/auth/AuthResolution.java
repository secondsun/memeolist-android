package org.aerogear.apps.memeolist.fh.auth;

import android.app.Activity;

import com.feedhenry.sdk.FHActCallback;
import com.feedhenry.sdk.FHResponse;
import com.feedhenry.sdk.api.FHAuthRequest;

import org.aerogear.apps.memeolist.fh.Resolution;

/**
 * Created by summers on 11/5/15.
 */
public class AuthResolution implements Resolution {
    private final FHAuthRequest authRequest;
    private Activity resolvingActivity;
    private final FHActCallback callback;

    public AuthResolution(FHAuthRequest authRequest, FHActCallback callback) {
        this.authRequest = authRequest;
        this.callback = callback;
    }

    @Override
    public void setup( Activity activity ) {
        this.resolvingActivity = activity ;
    }

    @Override
    public void run() {
        try {
            authRequest.setPresentingActivity(resolvingActivity);
            authRequest.executeAsync(callback);
        } catch (Exception e) {
            callback.fail(new FHResponse(null, null, e, e.getMessage()));
        }
    }
}
