package org.feedhenry.apps.arthenry.builder;

import org.feedhenry.apps.arthenry.FHClient;
import org.feedhenry.apps.arthenry.InitCallbackListener;

/**
 * Created by summers on 10/29/15.
 */
public interface FHBuilder<IMPLEMENTATION extends FHBuilder> {

    IMPLEMENTATION addInitCallback(InitCallbackListener listener);
    FHClient build();

}
