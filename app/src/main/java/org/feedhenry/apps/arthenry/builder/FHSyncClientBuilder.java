package org.feedhenry.apps.arthenry.builder;

import android.content.Context;

import org.feedhenry.apps.arthenry.FHClient;
import org.feedhenry.apps.arthenry.InitCallbackListener;

/**
 * Created by summers on 10/29/15.
 */
public class FHSyncClientBuilder  implements FHBuilder<FHClient.Builder> {

    private final FHBuilder<FHClient.Builder> parent;

    public FHSyncClientBuilder(FHBuilder<FHClient.Builder> parent) {
        this.parent = parent;
    }

    @Override
    public FHClient.Builder addInitCallback(InitCallbackListener listener) {
        return parent.addInitCallback(listener);
    }

    @Override
    public FHClient build() {
        return parent.build();
    }

}
