package org.feedhenry.apps.arthenry;

import com.feedhenry.sdk.FHResponse;

/**
 * Created by summers on 10/29/15.
 */
public interface InitCallbackListener {
    public void onInit(FHResponse fhResponse);
    public void onInitError(FHResponse fhResponse);
}
