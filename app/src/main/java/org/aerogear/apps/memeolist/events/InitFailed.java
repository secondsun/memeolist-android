package org.aerogear.apps.memeolist.events;

import org.aerogear.apps.memeolist.fh.ConnectionFailure;

/**
 * Created by summers on 11/5/15.
 */
public class InitFailed {
    private final ConnectionFailure connectionFailure;

    public InitFailed(ConnectionFailure connectionFailure) {
        this.connectionFailure = connectionFailure;
    }

    public ConnectionFailure getConnectionFailure() {
        return connectionFailure;
    }
}
