package org.feedhenry.apps.arthenry;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.feedhenry.sdk.FH;
import com.feedhenry.sdk.FHActCallback;
import com.feedhenry.sdk.FHResponse;
import com.feedhenry.sdk.sync.FHSyncClient;
import com.feedhenry.sdk.sync.FHSyncConfig;

import org.feedhenry.apps.arthenry.builder.AbstractBuilder;
import org.feedhenry.apps.arthenry.builder.FHAuthClientBuilder;
import org.feedhenry.apps.arthenry.builder.FHBuilder;
import org.feedhenry.apps.arthenry.builder.FHSyncClientBuilder;

import java.util.ArrayList;

/**
 * Created by summers on 10/29/15.
 */
public class FHClient {

    private Looper looper;
    private ArrayList<InitCallbackListener> initCallbacks = new ArrayList<>();
    private Context appContext;

    private FHClient() {
        
    }

    public void connect() {
        FH.init(appContext, new FHActCallback() {
            @Override
            public void success(FHResponse fhResponse) {
                postConnectSuccessRunner(fhResponse);
            }

            @Override
            public void fail(FHResponse fhResponse) {
                postConnectFailureRunner(fhResponse);
            }
        });
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

    private Runnable callFailureCallbacks(final FHResponse fhResponse) {

        return new Runnable() {
            @Override
            public void run() {
                for (InitCallbackListener listener : initCallbacks) {
                    listener.onInitError(fhResponse);
                }
            }
        };
    }

    private void postConnectFailureRunner(FHResponse fhResponse) {
        new Handler(looper).post(callFailureCallbacks(fhResponse));
    }


    public void disconnect() {
        FH.stop();
    }
    
    public static class Builder extends AbstractBuilder {

        private final Context context;
        private final Looper looper;
        private ArrayList<InitCallbackListener> initCallbacks = new ArrayList<>();

        public Builder(Context context) {
            this.context = context.getApplicationContext();
            this.looper = context.getMainLooper();
        }

        @Override
        public Builder addInitCallback(InitCallbackListener listener) {
            this.initCallbacks.add(listener);
            return this;
        }

        @Override
        public FHClient build() {
            FHClient client = new FHClient();
            client.looper = looper;
            client.initCallbacks = initCallbacks;
            client.appContext = context;
            return client;
        }


    }

}
