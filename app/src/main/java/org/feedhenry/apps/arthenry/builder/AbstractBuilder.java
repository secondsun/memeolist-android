package org.feedhenry.apps.arthenry.builder;

import org.feedhenry.apps.arthenry.FHClient;

/**
 * Created by summers on 10/29/15.
 */
public abstract class AbstractBuilder implements FHBuilder<FHClient.Builder> {



    public FHSyncClientBuilder addSync() {
        return new FHSyncClientBuilder(this);
    }

    public FHAuthClientBuilder addAuth() {
        return new FHAuthClientBuilder(this);
    }
}
