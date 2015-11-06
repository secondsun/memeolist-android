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
import com.squareup.otto.Bus;
import com.squareup.otto.Produce;

import org.feedhenry.apps.arthenry.events.InitFailed;
import org.feedhenry.apps.arthenry.events.InitSuccessful;
import org.feedhenry.apps.arthenry.events.ProjectsAvailable;
import org.feedhenry.apps.arthenry.fh.auth.FHAuthClientConfig;
import org.feedhenry.apps.arthenry.fh.auth.FHAuthUtil;
import org.feedhenry.apps.arthenry.fh.sync.FHSyncClientConfig;
import org.feedhenry.apps.arthenry.vo.Account;
import org.feedhenry.apps.arthenry.vo.Project;
import org.json.fh.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class FHClient {

    private static final String TAG = FHClient.class.getSimpleName();
    private Context appContext;
    private FHSyncListener syncListener;
    private FHSyncClient syncClient;
    private FHAuthClientConfig authConfig;
    private FHSyncClientConfig syncBuilder;
    private Account account;
    private boolean isConnected = false;
    private boolean isConnecting = false;
    private final Bus bus;
    private InitFailed failure = null;
    private InitSuccessful success = null;

    private FHClient(Bus bus) {
        this.bus = bus;
        bus.register(this);
    }

    public void connect() {
        if (isConnected || isConnecting) {
            return;
        }
        isConnecting = true;
        FH.init(appContext, new FHActCallback() {


            @Override
            public void success(final FHResponse fhResponse) {

                if (authConfig != null) {
                    performAuthThenSync(fhResponse);
                } else {
                    try {
                        setupSync(fhResponse);
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
        com.feedhenry.sdk.FHHttpClient.setTimeout(60000);
    }

    public void refreshMemes() {
        try {
           getSyncClient().forceSync("photos");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
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
                                        setupSync(fhResponse);
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
                        postAuthenticationRequired(fhResponse, new AuthCompleteCallback(session));
                    }
                }, false);


            } catch (Exception e) {
                postConnectFailureRunner(new FHResponse(null, null, e, e.getMessage()));
            }

        }
    }

    @Produce
    public ProjectsAvailable getProjectsAvailable() {
        if (syncClient != null) {
            JSONObject allData = syncClient.list("photos");

            Iterator<String> it = allData.keys();
            List<Project> itemsToSync = new ArrayList<>();

            while (it.hasNext()) {
                String key = it.next();
                JSONObject data = allData.getJSONObject(key);
                JSONObject dataObj = data.getJSONObject("data");
                Project item = new Gson().fromJson(dataObj.toString(), Project.class);
                item.setId(key);
                itemsToSync.add(item);
            }

            return new ProjectsAvailable(itemsToSync);
        } else return null;

    }

    @Produce
    public InitFailed produceFailure() {
        return failure;
    }

    @Produce
    public InitSuccessful produceSuccessful() {
        return success;
    }

    private void postCheckAccount(FHResponse fhResponse) {
        try {
            FH.cloud("/account/login", "POST", new Header[0], fhResponse.getJson(), new FHActCallback() {
                @Override
                public void success(FHResponse fhResponse) {
                    setAccount(new Gson().fromJson(fhResponse.getJson().toString(), Account.class));
                    setupSync(fhResponse);

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

    private void setupSync(FHResponse fhResponse) {
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
                postConnectSuccessRunner(fhResponse);
            } catch (Exception e) {
                postConnectFailureRunner(new FHResponse(null, null, e, e.getMessage()));
            }
        }
    }


    private void postConnectSuccessRunner(FHResponse fhResponse) {
        isConnected = true;
        isConnecting = false;
        this.failure = null;
        bus.post(this.success = new InitSuccessful(fhResponse));
    }

    private void postConnectFailureRunner(FHResponse fhResponse) {
        isConnecting = false;
        this.success = null;
        bus.post(this.failure = new InitFailed(new ConnectionFailure(fhResponse)));
    }

    private void postConnectFailureRunner(ConnectionFailure failure) {
        isConnecting = false;
        this.success = null;
        bus.post(this.failure = new InitFailed(failure));
    }

    private void postAuthenticationRequired(FHResponse fhResponse, final FHActCallback callback) {

        Resolution authResolver = null;
        try {
            authResolver = FHAuthUtil.buildAuthResolver(this.authConfig, callback);
        } catch (FHNotReadyException e) {
            postConnectFailureRunner(new FHResponse(null, null, e, e.getMessage()));
        }
        ConnectionFailure failure = new ConnectionFailure(fhResponse, authResolver, FHAuthUtil.SIGN_IN_REQUIRED);
        postConnectFailureRunner(failure);
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

    public URL getCloudUrl(String path) {
        try {
            return new URL(FH.getCloudHost() + path);
        } catch (MalformedURLException | FHNotReadyException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RuntimeException(e);
        }

    }

    public static class Builder {
        protected FHSyncClientConfig syncBuilder;
        protected FHAuthClientConfig authBuilder;

        private final Context context;
        private final Bus bus;

        public Builder(Context context, Bus bus) {
            this.context = context.getApplicationContext();
            this.bus = bus;
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
            FHClient client = new FHClient(bus);
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

    public boolean isConnected() {
        return isConnected;
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

