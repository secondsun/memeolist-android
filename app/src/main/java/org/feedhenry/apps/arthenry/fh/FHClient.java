package org.feedhenry.apps.arthenry.fh;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

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
import com.google.gson.Gson;

import org.feedhenry.apps.arthenry.InitCallbackListener;
import org.feedhenry.apps.arthenry.fh.auth.FHAuthClientConfig;
import org.feedhenry.apps.arthenry.fh.auth.FHAuthUtil;
import org.feedhenry.apps.arthenry.fh.sync.FHSyncClientConfig;
import org.feedhenry.apps.arthenry.vo.Account;
import org.json.fh.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by summers on 10/29/15.
 */
public class FHClient {

    private Looper looper;
    private ArrayList<InitCallbackListener> initCallbacks = new ArrayList<>();
    private Context appContext;
    private FHSyncListener syncListener;
    private FHSyncClient syncClient;
    private FHAuthClientConfig authConfig;
    private FHSyncClientConfig syncBuilder;
    private Account account;

    private FHClient() {

    }

    public void connect() {
        FH.init(appContext, new FHActCallback() {


            @Override
            public void success(final FHResponse fhResponse) {

                if (authConfig != null) {
                    performAuthThenSync(fhResponse);
                } else {
                    try {
                        setupSync();
                        postConnectSuccessRunner(fhResponse);
                    } catch (Exception e) {
                        postConnectFailureRunner(new FHResponse(null, null, e, e.getMessage()));
                    }
                }
            }

            @Override
            public void fail(FHResponse fhResponse) {
                postConnectFailureRunner(fhResponse);
            }
        });
    }

    private void performAuthThenSync(final FHResponse fhResponse) {
        final FHAuthSession session = new FHAuthSession(DataManager.getInstance(), new FHHttpClient());
        if (!session.exists()) {
            postAuthenticationRequired(fhResponse, new AuthCompleteCallback(session));
        } else {
            try {
                session.verify(new com.feedhenry.sdk.api.FHAuthSession.Callback() {

                    @Override
                    public void handleSuccess(boolean b) {
                        if (!b) {
                            postAuthenticationRequired(fhResponse, new AuthCompleteCallback(session));
                        } else {
                            try {
                                FH.cloud("/account/me", "GET", null, null, new FHActCallback() {
                                    @Override
                                    public void success(FHResponse fhResponse) {
                                        setAccount(new Gson().fromJson(fhResponse.getJson().toString(), Account.class));
                                        if (syncBuilder != null) {
                                            syncBuilder.addMetaData(FHAuthSession.SESSION_TOKEN_KEY, session.getToken());
                                        }
                                        setupSync();
                                        postConnectSuccessRunner(fhResponse);
                                    }

                                    @Override
                                    public void fail(FHResponse fhResponse) {
                                        postAuthenticationRequired(fhResponse, new AuthCompleteCallback(session));
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                                postAuthenticationRequired(fhResponse, new AuthCompleteCallback(session));
                            }

                        }
                    }

                    @Override
                    public void handleError(FHResponse fhResponse) {
                        postAuthenticationRequired(fhResponse, new AuthCompleteCallback(session));}
                }, false);


            } catch (Exception e) {
                postConnectFailureRunner(new FHResponse(null, null, e, e.getMessage()));
            }

        }
    }

    private void postCheckAccount(FHResponse fhResponse) {
        try {
            FH.cloud("/account/login", "POST", new Header[0], fhResponse.getJson(), new FHActCallback() {
                @Override
                public void success(FHResponse fhResponse) {
                    setAccount(new Gson().fromJson(fhResponse.getJson().toString(), Account.class));
                    setupSync();
                    postConnectSuccessRunner(fhResponse);
                }

                @Override
                public void fail(FHResponse fhResponse) {
                    throw new RuntimeException(fhResponse.getErrorMessage());
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void setupSync() {
        if (syncBuilder != null) {
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
            syncConfig.setSyncFrequency(syncBuilder.getSyncFrequencySeconds());
            syncConfig.setUseCustomSync(syncBuilder.isUseCustomSync());

            syncClient = FHSyncClient.getInstance();
            syncClient.init(appContext, syncConfig, syncListener);

            JSONObject queryParams = syncBuilder.getQueryParams();
            JSONObject metaData = syncBuilder.getMetaData();

            try {
                for (String dataSet : syncBuilder.getDataSets()) {


                    syncClient.manage(dataSet, null, queryParams, metaData);

                }
            } catch (Exception e) {
                postConnectFailureRunner(new FHResponse(null, null, e, e.getMessage()));
            }
        }
    }

    private void postAuthenticationRequired(FHResponse fhResponse, final FHActCallback callback) {
        Runnable authResolver = null;
        try {
            authResolver = FHAuthUtil.buildAuthResolver(this.authConfig, callback);
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

    public void setAccount(Account account) {
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }

    public FHSyncClient getSyncClient() {
        return syncClient;
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

                client.syncListener = syncBuilder.getSyncListener();
                client.syncBuilder = syncBuilder;
            }

            if (this.authBuilder != null) {
                client.authConfig = authBuilder;
            }

            return client;
        }


    }

    private class AuthCompleteCallback implements FHActCallback {

        private final FHAuthSession session;

        public AuthCompleteCallback(FHAuthSession session) {
            this.session = session;
        }

        @Override
            public void success(FHResponse fhAuthResponse) {
                try {
                    if (syncBuilder != null) {
                        syncBuilder.addMetaData(FHAuthSession.SESSION_TOKEN_KEY, session.getToken());
                    }
                    Log.d("Connect", fhAuthResponse.getJson().toString());
                    postCheckAccount(fhAuthResponse);
                } catch (Exception e) {
                    postConnectFailureRunner(new FHResponse(null, null, e, e.getMessage()));
                }
            }

            @Override
            public void fail(FHResponse fhResponse) {
                postConnectFailureRunner(fhResponse);
            }

    }
}

