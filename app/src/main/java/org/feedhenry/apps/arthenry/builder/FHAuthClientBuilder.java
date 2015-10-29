package org.feedhenry.apps.arthenry.builder;

import org.feedhenry.apps.arthenry.FHClient;
import org.feedhenry.apps.arthenry.FHClient.Builder;
import org.feedhenry.apps.arthenry.InitCallbackListener;

/**
 * Created by summers on 10/29/15.
 */
public class FHAuthClientBuilder implements FHBuilder<Builder> {

    private final FHBuilder<FHClient.Builder> parent;
    private String authPolicyId;

    public FHAuthClientBuilder(FHBuilder<FHClient.Builder> parent) {
        this.parent = parent;
    }

    public FHAuthClientBuilder setAuthPolicyId(String authPolicyId) {
        this.authPolicyId = authPolicyId;
        return this;
    }

    public String getAuthPolicyId() {
        return authPolicyId;
    }

    @Override
    public Builder addInitCallback(InitCallbackListener listener) {
        return parent.addInitCallback(listener);
    }

    @Override
    public FHClient build() {
        return parent.build();
    }
}
