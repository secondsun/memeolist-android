package org.feedhenry.apps.arthenry.fh;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.feedhenry.sdk.FH;
import com.feedhenry.sdk.FHActCallback;
import com.feedhenry.sdk.FHResponse;
import com.feedhenry.sdk.api2.FHAuthSession;
import com.feedhenry.sdk.exceptions.FHNotReadyException;
import com.feedhenry.sdk.sync.FHSyncClient;
import com.feedhenry.sdk.sync.FHSyncConfig;
import com.feedhenry.sdk.sync.FHSyncListener;
import com.feedhenry.sdk.utils.DataManager;
import com.feedhenry.sdk2.FHHttpClient;

import org.feedhenry.apps.arthenry.InitCallbackListener;
import org.feedhenry.apps.arthenry.fh.auth.FHAuthClientConfig;
import org.feedhenry.apps.arthenry.fh.auth.FHAuthUtil;
import org.feedhenry.apps.arthenry.fh.sync.FHSyncClientConfig;

import java.util.ArrayList;

/**
 * Created by summers on 10/29/15.
 */
public class FHClient {

    private Looper looper;
    private ArrayList<InitCallbackListener> initCallbacks = new ArrayList<>();
    private Context appContext;
    private FHSyncConfig syncConfig;
    private FHSyncListener syncListener;
    private FHSyncClient syncClient;
    private FHAuthClientConfig authConfig;

    private FHClient() {

    }

    public void connect() {
        FH.init(appContext, new FHActCallback() {


            @Override
            public void success(FHResponse fhResponse) {

                if (authConfig != null) {
                    FHAuthSession session = new FHAuthSession(DataManager.getInstance(), new FHHttpClient());
                    if (!session.exists()) {
                        postAuthenticationRequired(fhResponse);
                    } else {
                        postConnectSuccessRunner(fhResponse);

                        if (syncConfig != null) {
                            syncClient = FHSyncClient.getInstance();
                            syncClient.init(appContext, syncConfig, syncListener);
                        }
                    }

                } else {

                    postConnectSuccessRunner(fhResponse);

                    if (syncConfig != null) {
                        syncClient = FHSyncClient.getInstance();
                        syncClient.init(appContext, syncConfig, syncListener);
                    }
                }
            }

            @Override
            public void fail(FHResponse fhResponse) {
                postConnectFailureRunner(fhResponse);
            }
        });
    }

    private void postAuthenticationRequired(FHResponse fhResponse) {
        Runnable authResolver = null;
        try {
            authResolver = FHAuthUtil.buildAuthResolver(this.authConfig, new FHActCallback() {
                @Override
                public void success(FHResponse fhResponse) {
                    postAuthenticationRequired(fhResponse);
                }

                @Override
                public void fail(FHResponse fhResponse) {
                    postConnectFailureRunner(fhResponse);
                }
            });
        } catch (FHNotReadyException e) {
            postConnectFailureRunner(new FHResponse(null, null, e, e.getMessage()));
        }
        ConnectionFailure failure = new ConnectionFailure(fhResponse, authResolver, FHAuthUtil.SIGN_IN_REQUIRED);
        postConnectFailureRunner(failure);
    }

    private void postConnectSuccessRunner(FHResponse fhResponse) {
        new Handler(looper).post(callSuccessCallbacks(fhResponse));
    }

    private Runnable callSuccessCallbacks(final FHResponse fhResponse) {

        return new Runnable() {
            @Override
            public void run() {
                for (InitCallbackListener listener : initCallbacks) {
                    listener.onInit(fhResponse);
                }
            }
        };
    }


    private Runnable callFailureCallbacks(final ConnectionFailure failure) {

        return new Runnable() {
            @Override
            public void run() {
                for (InitCallbackListener listener : initCallbacks) {
                    listener.onInitError(failure);
                }
            }
        };
    }

    private Runnable callFailureCallbacks(final FHResponse fhResponse) {

        return new Runnable() {
            @Override
            public void run() {
                for (InitCallbackListener listener : initCallbacks) {
                    listener.onInitError(new ConnectionFailure(fhResponse));
                }
            }
        };
    }

    private void postConnectFailureRunner(FHResponse fhResponse) {
        new Handler(looper).post(callFailureCallbacks(fhResponse));
    }

    private void postConnectFailureRunner(ConnectionFailure failure) {
        new Handler(looper).post(callFailureCallbacks(failure));
    }


    public void disconnect() {
        if (syncClient != null) {
            syncClient.destroy();
        }
        FH.stop();
    }

    public static class Builder {
        protected FHSyncClientConfig syncBuilder;
        protected FHAuthClientConfig authBuilder;

        private final Context context;
        private final Looper looper;
        private ArrayList<InitCallbackListener> initCallbacks = new ArrayList<>();

        public Builder(Context context) {
            this.context = context.getApplicationContext();
            this.looper = context.getMainLooper();
        }


        public Builder addInitCallback(InitCallbackListener listener) {
            this.initCallbacks.add(listener);
            return this;
        }

        public Builder addFeature(FHAuthClientConfig authBuilder) {
            this.authBuilder = authBuilder;
            return this;
        }

        public Builder addFeature(FHSyncClientConfig syncBuilder) {
            this.syncBuilder = syncBuilder;
            return this;
        }

        public FHClient build() {
            FHClient client = new FHClient();
            client.looper = looper;
            client.initCallbacks = initCallbacks;
            client.appContext = context;

            if (this.syncBuilder != null) {
                FHSyncConfig syncConfig = new FHSyncConfig();
                syncConfig.setAutoSyncLocalUpdates(syncBuilder.isAutoSyncLocalUpdates());
                syncConfig.setCrashCountWait(syncBuilder.getCrashCountWait());
                syncConfig.setNotifyClientStorageFailed(syncBuilder.isNotifyClientStorageFailed());
                syncConfig.setNotifyDeltaReceived(syncBuilder.isNotifyDeltaReceived());
                syncConfig.setNotifyLocalUpdateApplied(syncBuilder.isNotifyLocalUpdateApplied());
                syncConfig.setNotifyOfflineUpdate(syncBuilder.isNotifyOfflineUpdate());
                syncConfig.setNotifyRemoteUpdateApplied(syncBuilder.isNotifyRemoteUpdateApplied());
                syncConfig.setNotifySyncStarted(syncBuilder.isNotifySyncStarted());
                syncConfig.setNotifySyncFailed(syncBuilder.isNotifySyncFailed());
                syncConfig.setNotifySyncComplete(syncBuilder.isNotifySyncComplete());
                syncConfig.setNotifySyncCollisions(syncBuilder.isNotifySyncCollisions());
                syncConfig.setNotifyUpdateFailed(syncBuilder.isNotifyRemoteUpdateFailed());
                syncConfig.setResendCrashedUpdates(syncBuilder.isResendCrashedUpdates());
                syncConfig.setSyncFrequency(this.syncBuilder.getSyncFrequencySeconds());
                syncConfig.setUseCustomSync(syncBuilder.isUseCustomSync());
                client.syncListener = syncBuilder.getSyncListener();
                client.syncConfig = syncConfig;
            }

            if (this.authBuilder != null) {
                client.authConfig = authBuilder;
            }

            return client;
        }


    }

}

