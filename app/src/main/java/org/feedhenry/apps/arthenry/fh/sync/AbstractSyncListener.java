package org.feedhenry.apps.arthenry.fh.sync;

import com.feedhenry.sdk.sync.FHSyncListener;
import com.feedhenry.sdk.sync.NotificationMessage;

public abstract class AbstractSyncListener implements FHSyncListener {
    @Override
    public void onSyncStarted(NotificationMessage notificationMessage) {

    }

    @Override
    public void onSyncCompleted(NotificationMessage notificationMessage) {

    }

    @Override
    public void onUpdateOffline(NotificationMessage notificationMessage) {

    }

    @Override
    public void onCollisionDetected(NotificationMessage notificationMessage) {

    }

    @Override
    public void onRemoteUpdateFailed(NotificationMessage notificationMessage) {

    }

    @Override
    public void onRemoteUpdateApplied(NotificationMessage notificationMessage) {

    }

    @Override
    public void onLocalUpdateApplied(NotificationMessage notificationMessage) {

    }

    @Override
    public void onDeltaReceived(NotificationMessage notificationMessage) {

    }

    @Override
    public void onSyncFailed(NotificationMessage notificationMessage) {

    }

    @Override
    public void onClientStorageFailed(NotificationMessage notificationMessage) {

    }
}
