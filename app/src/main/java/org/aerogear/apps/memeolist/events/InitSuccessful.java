package org.aerogear.apps.memeolist.events;

import com.feedhenry.sdk.FHResponse;

public class InitSuccessful {
    private final FHResponse fhResponse;

    public InitSuccessful(FHResponse fhResponse) {
        this.fhResponse = fhResponse;
    }

    public FHResponse getFhResponse() {
        return fhResponse;
    }
}
