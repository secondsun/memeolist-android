package org.feedhenry.apps.arthenry.builder;

import com.feedhenry.sdk.sync.FHSyncListener;

import org.feedhenry.apps.arthenry.FHClient;
import org.feedhenry.apps.arthenry.InitCallbackListener;

/**
 * Created by summers on 10/29/15.
 */
public class FHSyncClientBuilder implements FHBuilder<FHClient.Builder> {

    private int syncFrequencySeconds = 10;
    private boolean autoSyncLocalUpdates = false;
    private boolean notifySyncStarted = false;
    private boolean notifySyncComplete = false;
    private boolean notifySyncCollisions = false;
    private boolean notifyOfflineUpdate = false;
    private boolean notifyRemoteUpdateFailed = false;
    private boolean notifyRemoteUpdateApplied = false;
    private boolean notifyLocalUpdateApplied = false;
    private boolean notifyDeltaReceived = false;
    private boolean notifySyncFailed = false;
    private boolean notifyClientStorageFailed = false;
    private int crashCountWait = 10;
    private boolean resendCrashedUpdates = true;
    private boolean useCustomSync = false;

    private final FHBuilder<FHClient.Builder> parent;
    private FHSyncListener syncListener;

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

    public FHSyncClientBuilder setSyncFrequencySeconds(int syncFrequencySeconds) {
        this.syncFrequencySeconds = syncFrequencySeconds;
        return this;
    }

    public FHSyncClientBuilder setAutoSyncLocalUpdates(boolean autoSyncLocalUpdates) {
        this.autoSyncLocalUpdates = autoSyncLocalUpdates;
        return this;
    }

    public FHSyncClientBuilder setNotifySyncStarted(boolean notifySyncStarted) {
        this.notifySyncStarted = notifySyncStarted;
        return this;
    }

    public FHSyncClientBuilder setNotifySyncComplete(boolean notifySyncComplete) {
        this.notifySyncComplete = notifySyncComplete;
        return this;
    }

    public FHSyncClientBuilder setNotifySyncCollisions(boolean notifySyncCollisions) {
        this.notifySyncCollisions = notifySyncCollisions;
        return this;
    }

    public FHSyncClientBuilder setNotifyOfflineUpdate(boolean notifyOfflineUpdate) {
        this.notifyOfflineUpdate = notifyOfflineUpdate;
        return this;
    }

    public FHSyncClientBuilder setNotifyRemoteUpdateFailed(boolean notifyRemoteUpdateFailed) {
        this.notifyRemoteUpdateFailed = notifyRemoteUpdateFailed;
        return this;
    }

    public FHSyncClientBuilder setNotifyRemoteUpdateApplied(boolean notifyRemoteUpdateApplied) {
        this.notifyRemoteUpdateApplied = notifyRemoteUpdateApplied;
        return this;
    }

    public FHSyncClientBuilder setNotifyLocalUpdateApplied(boolean notifyLocalUpdateApplied) {
        this.notifyLocalUpdateApplied = notifyLocalUpdateApplied;
        return this;
    }

    public FHSyncClientBuilder setNotifyDeltaReceived(boolean notifyDeltaReceived) {
        this.notifyDeltaReceived = notifyDeltaReceived;
        return this;
    }

    public FHSyncClientBuilder setNotifySyncFailed(boolean notifySyncFailed) {
        this.notifySyncFailed = notifySyncFailed;
        return this;
    }

    public FHSyncClientBuilder setNotifyClientStorageFailed(boolean notifyClientStorageFailed) {
        this.notifyClientStorageFailed = notifyClientStorageFailed;
        return this;
    }

    public FHSyncClientBuilder setCrashCountWait(int crashCountWait) {
        this.crashCountWait = crashCountWait;
        return this;
    }

    public FHSyncClientBuilder setResendCrashedUpdates(boolean resendCrashedUpdates) {
        this.resendCrashedUpdates = resendCrashedUpdates;
        return this;
    }

    public FHSyncClientBuilder setUseCustomSync(boolean useCustomSync) {
        this.useCustomSync = useCustomSync;
        return this;
    }

    public int getSyncFrequencySeconds() {
        return syncFrequencySeconds;
    }

    public boolean isAutoSyncLocalUpdates() {
        return autoSyncLocalUpdates;
    }

    public boolean isNotifySyncStarted() {
        return notifySyncStarted;
    }

    public boolean isNotifySyncComplete() {
        return notifySyncComplete;
    }

    public boolean isNotifySyncCollisions() {
        return notifySyncCollisions;
    }

    public boolean isNotifyOfflineUpdate() {
        return notifyOfflineUpdate;
    }

    public boolean isNotifyRemoteUpdateFailed() {
        return notifyRemoteUpdateFailed;
    }

    public boolean isNotifyRemoteUpdateApplied() {
        return notifyRemoteUpdateApplied;
    }

    public boolean isNotifyLocalUpdateApplied() {
        return notifyLocalUpdateApplied;
    }

    public boolean isNotifyDeltaReceived() {
        return notifyDeltaReceived;
    }

    public boolean isNotifySyncFailed() {
        return notifySyncFailed;
    }

    public boolean isNotifyClientStorageFailed() {
        return notifyClientStorageFailed;
    }

    public int getCrashCountWait() {
        return crashCountWait;
    }

    public boolean isResendCrashedUpdates() {
        return resendCrashedUpdates;
    }

    public boolean isUseCustomSync() {
        return useCustomSync;
    }

    public FHSyncListener getSyncListener() {
        return syncListener;
    }

    public FHSyncClientBuilder setSyncListener(FHSyncListener syncListener) {
        this.syncListener = syncListener;
        return this;
    }
}
