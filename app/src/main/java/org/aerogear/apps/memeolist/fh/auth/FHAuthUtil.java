package org.aerogear.apps.memeolist.fh.auth;

import com.feedhenry.sdk.FH;
import com.feedhenry.sdk.FHActCallback;
import com.feedhenry.sdk.FHResponse;
import com.feedhenry.sdk.api.FHAuthRequest;
import com.feedhenry.sdk.exceptions.FHNotReadyException;

import org.aerogear.apps.memeolist.fh.Resolution;

/**
 * Created by secondsun on 10/30/15.
 */
public class FHAuthUtil {
    public static final int SIGN_IN_REQUIRED = 0x8083;
    private static final String TAG = "FHAuthUtil";

    public static Resolution buildAuthResolver(FHAuthClientConfig authConfig, final FHActCallback callback) throws FHNotReadyException {
        final FHAuthRequest authRequest = FH.buildAuthRequest();
        authRequest.setPresentingActivity(authConfig.getCallingActivity());
        authRequest.setAuthPolicyId(authConfig.getAuthPolicyId());

        return new AuthResolution(authRequest, callback);

    }

}
