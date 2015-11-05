package org.feedhenry.apps.arthenry.fh.sync;

import com.feedhenry.sdk.sync.FHSyncListener;
import com.feedhenry.sdk.sync.NotificationMessage;

/**
 * Created by summers on 10/29/15.
 */
public class InjectableSyncListener implements FHSyncListener {

    private FHSyncListener attatchedListener;

    @Override
    public void onSyncStarted(NotificationMessage notificationMessage) {
        if (attatchedListener != null) attatchedListener.onSyncStarted(notificationMessage);
    }

    @Override
    public void onSyncCompleted(NotificationMessage notificationMessage) {
        if (attatchedListener != null) attatchedListener.onSyncCompleted(notificationMessage);
    }

    @Override
    public void onUpdateOffline(NotificationMessage notificationMessage) {
        if (attatchedListener != null) attatchedListener.onUpdateOffline(notificationMessage);
    }

    @Override
    public void onCollisionDetected(NotificationMessage notificationMessage) {
        if (attatchedListener != null) attatchedListener.onCollisionDetected(notificationMessage);
    }

    @Override
    public void onRemoteUpdateFailed(NotificationMessage notificationMessage) {
        if (attatchedListener != null) attatchedListener.onRemoteUpdateFailed(notificationMessage);
    }

    @Override
    public void onRemoteUpdateApplied(NotificationMessage notificationMessage) {
        if (attatchedListener != null) attatchedListener.onRemoteUpdateApplied(notificationMessage);
    }

    @Override
    public void onLocalUpdateApplied(NotificationMessage notificationMessage) {
        if (attatchedListener != null) attatchedListener.onLocalUpdateApplied(notificationMessage);
    }

    @Override
    public void onDeltaReceived(NotificationMessage notificationMessage) {
        if (attatchedListener != null) attatchedListener.onDeltaReceived(notificationMessage);
    }

    @Override
    public void onSyncFailed(NotificationMessage notificationMessage) {
        if (attatchedListener != null) attatchedListener.onSyncFailed(notificationMessage);
    }

    @Override
    public void onClientStorageFailed(NotificationMessage notificationMessage) {
        attatchedListener.onClientStorageFailed(notificationMessage);
    }

    public synchronized void attachListener(FHSyncListener listener) {
        this.attatchedListener = listener;
    }

    public synchronized void detachListener() {
        this.attatchedListener = null;
    }

}
