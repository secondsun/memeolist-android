package org.feedhenry.apps.arthenry.fh.sync;

import com.feedhenry.sdk.sync.FHSyncListener;

/**
 * Created by summers on 10/29/15.
 */
public class FHSyncClientConfig {

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

    private final FHSyncListener syncListener;

    public FHSyncClientConfig(FHSyncListener syncListener) {
        this.syncListener = syncListener;
    }


    public FHSyncClientConfig setSyncFrequencySeconds(int syncFrequencySeconds) {
        this.syncFrequencySeconds = syncFrequencySeconds;
        return this;
    }

    public FHSyncClientConfig setAutoSyncLocalUpdates(boolean autoSyncLocalUpdates) {
        this.autoSyncLocalUpdates = autoSyncLocalUpdates;
        return this;
    }

    public FHSyncClientConfig setNotifySyncStarted(boolean notifySyncStarted) {
        this.notifySyncStarted = notifySyncStarted;
        return this;
    }

    public FHSyncClientConfig setNotifySyncComplete(boolean notifySyncComplete) {
        this.notifySyncComplete = notifySyncComplete;
        return this;
    }

    public FHSyncClientConfig setNotifySyncCollisions(boolean notifySyncCollisions) {
        this.notifySyncCollisions = notifySyncCollisions;
        return this;
    }

    public FHSyncClientConfig setNotifyOfflineUpdate(boolean notifyOfflineUpdate) {
        this.notifyOfflineUpdate = notifyOfflineUpdate;
        return this;
    }

    public FHSyncClientConfig setNotifyRemoteUpdateFailed(boolean notifyRemoteUpdateFailed) {
        this.notifyRemoteUpdateFailed = notifyRemoteUpdateFailed;
        return this;
    }

    public FHSyncClientConfig setNotifyRemoteUpdateApplied(boolean notifyRemoteUpdateApplied) {
        this.notifyRemoteUpdateApplied = notifyRemoteUpdateApplied;
        return this;
    }

    public FHSyncClientConfig setNotifyLocalUpdateApplied(boolean notifyLocalUpdateApplied) {
        this.notifyLocalUpdateApplied = notifyLocalUpdateApplied;
        return this;
    }

    public FHSyncClientConfig setNotifyDeltaReceived(boolean notifyDeltaReceived) {
        this.notifyDeltaReceived = notifyDeltaReceived;
        return this;
    }

    public FHSyncClientConfig setNotifySyncFailed(boolean notifySyncFailed) {
        this.notifySyncFailed = notifySyncFailed;
        return this;
    }

    public FHSyncClientConfig setNotifyClientStorageFailed(boolean notifyClientStorageFailed) {
        this.notifyClientStorageFailed = notifyClientStorageFailed;
        return this;
    }

    public FHSyncClientConfig setCrashCountWait(int crashCountWait) {
        this.crashCountWait = crashCountWait;
        return this;
    }

    public FHSyncClientConfig setResendCrashedUpdates(boolean resendCrashedUpdates) {
        this.resendCrashedUpdates = resendCrashedUpdates;
        return this;
    }

    public FHSyncClientConfig setUseCustomSync(boolean useCustomSync) {
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

}
