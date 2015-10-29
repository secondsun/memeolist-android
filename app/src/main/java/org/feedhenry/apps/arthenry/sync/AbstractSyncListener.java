package org.feedhenry.apps.arthenry.sync;

import com.feedhenry.sdk.sync.FHSyncListener;
import com.feedhenry.sdk.sync.NotificationMessage;

/**
 * Created by summers on 10/29/15.
 */
public class AbstractSyncListener implements FHSyncListener {
    @Override
    public synchronized void onSyncStarted(NotificationMessage notificationMessage) {

    }

    @Override
    public synchronized void onSyncCompleted(NotificationMessage notificationMessage) {

    }

    @Override
    public synchronized void onUpdateOffline(NotificationMessage notificationMessage) {

    }

    @Override
    public synchronized void onCollisionDetected(NotificationMessage notificationMessage) {

    }

    @Override
    public synchronized void onRemoteUpdateFailed(NotificationMessage notificationMessage) {

    }

    @Override
    public synchronized void onRemoteUpdateApplied(NotificationMessage notificationMessage) {

    }

    @Override
    public synchronized void onLocalUpdateApplied(NotificationMessage notificationMessage) {

    }

    @Override
    public synchronized void onDeltaReceived(NotificationMessage notificationMessage) {

    }

    @Override
    public synchronized void onSyncFailed(NotificationMessage notificationMessage) {

    }

    @Override
    public synchronized void onClientStorageFailed(NotificationMessage notificationMessage) {

    }
}
