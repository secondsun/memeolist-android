package org.feedhenry.apps.arthenry.fh.auth;

import android.content.Intent;
import android.util.Log;

import com.feedhenry.sdk.FH;
import com.feedhenry.sdk.FHActCallback;
import com.feedhenry.sdk.FHResponse;
import com.feedhenry.sdk.api.FHAuthRequest;
import com.feedhenry.sdk.exceptions.FHNotReadyException;

/**
 * Created by secondsun on 10/30/15.
 */
public class FHAuthUtil {
    public static final int SIGN_IN_REQUIRED = 0x8083;
    private static final String TAG = "FHAuthUtil";

    public static Runnable buildAuthResolver(FHAuthClientConfig authConfig, final FHActCallback callback) throws FHNotReadyException {
        final FHAuthRequest authRequest = FH.buildAuthRequest();
        authRequest.setPresentingActivity(authConfig.getCallingActivity());
        authRequest.setAuthPolicyId(authConfig.getAuthPolicyId());

        return new Runnable() {
            @Override
            public void run() {
                try {
                    authRequest.executeAsync(callback);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                    callback.fail(new FHResponse(null, null, e, e.getMessage()));
                }
            }
        };

    }

//
//    protected void doOAuth() {
//        try {
//            FHAuthRequest authRequest = FH.buildAuthRequest();
//            authRequest.setPresentingActivity(this);
//            authRequest.setAuthPolicyId(FH_AUTH_POLICY);
//            authRequest.executeAsync(new FHActCallback() {
//
//                @Override
//                public void success(FHResponse resp) {
//                    onSessionValid(FHAuthSession.getToken());
//                }
//
//                @Override
//                public void fail(FHResponse resp) {
//                    Toast.makeText(FHOAuth.this, "Log in failed", Toast.LENGTH_LONG).show();
//                    Log.d(TAG, resp.getErrorMessage());
//                    onNotLoggedIn();
//                }
//            });
//        } catch (Exception e) {
//            Toast.makeText(FHOAuth.this, e.getMessage(), Toast.LENGTH_LONG).show();
//            Log.e(TAG, e.getMessage(), e);
//            onNotLoggedIn();
//        }
//    }
}
