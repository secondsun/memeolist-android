package org.feedhenry.apps.arthenry.util;

import com.feedhenry.sdk.FHResponse;

import org.feedhenry.apps.arthenry.fh.ConnectionFailure;

/**
 * Created by summers on 10/29/15.
 */
public interface InitCallbackListener {
    public void onInit(FHResponse fhResponse);
    public void onInitError(ConnectionFailure failure);
}
