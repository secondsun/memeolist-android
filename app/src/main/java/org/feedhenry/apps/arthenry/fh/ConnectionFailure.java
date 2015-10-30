package org.feedhenry.apps.arthenry.fh;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.feedhenry.sdk.FHResponse;

public class ConnectionFailure {


    private int responseCode;
    private final FHResponse fhResponse;
    private Runnable resolution;

    public ConnectionFailure(FHResponse fhResponse) {
        this.fhResponse = fhResponse;
    }

    public ConnectionFailure(FHResponse fhResponse, Runnable resolution, int responseCode) {
        this.responseCode = responseCode;
        this.fhResponse = fhResponse;
        this.resolution = resolution;
    }

    public boolean hasResolution() {
        return resolution != null;
    }

    public void resolve(Activity activity, int requestCode) {
        if (resolution == null) {
            throw new IllegalStateException("Can not resolve a failure with no resolution");
        } else {
            new Handler(activity.getMainLooper()).post(resolution);
        }
    }

    public FHResponse getResponse() {
        return fhResponse;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
}
