package org.feedhenry.apps.arthenry.builder;

import org.feedhenry.apps.arthenry.FHClient;

/**
 * Created by summers on 10/29/15.
 */
public abstract class AbstractBuilder implements FHBuilder<FHClient.Builder> {

    protected FHSyncClientBuilder syncBuilder;
    protected FHAuthClientBuilder authBuilder;

    public FHSyncClientBuilder addSync() {
        return this.syncBuilder = new FHSyncClientBuilder(this);
    }

    public FHAuthClientBuilder addAuth() {
        return this.authBuilder = new FHAuthClientBuilder(this);
    }
}
