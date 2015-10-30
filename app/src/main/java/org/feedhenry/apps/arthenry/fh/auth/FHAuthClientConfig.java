package org.feedhenry.apps.arthenry.fh.auth;

import android.app.Activity;

/**
 * Created by summers on 10/29/15.
 */
public class FHAuthClientConfig {


    private String authPolicyId;
    private Activity callingActivity;

    public FHAuthClientConfig(String authPolicyId) {
        this.authPolicyId = authPolicyId;
    }

    public FHAuthClientConfig setAuthPolicyId(String authPolicyId) {
        this.authPolicyId = authPolicyId;
        return this;
    }

    public String getAuthPolicyId() {
        return authPolicyId;
    }

    public Activity getCallingActivity() {
        return callingActivity;
    }

    public FHAuthClientConfig setCallingActivity(Activity callingActivity) {
        this.callingActivity = callingActivity;
        return this;
    }
}
