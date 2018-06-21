package org.aerogear.apps.memeolist.util.adapter;


import com.feedhenry.sdk.FH;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by summers on 11/5/15.
 */
public class PicassoDownloader {
    public static final OkHttpClient picassoClient = new OkHttpClient();

    static {
        picassoClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {

                try {
                    Request.Builder newRequest = chain.request().newBuilder();

                    Header[] headers = new Header[0];

                    headers = FH.getDefaultParamsAsHeaders(null);
                    for (Header header : headers) {
                        newRequest.addHeader(header.getName(), header.getValue());
                    }

                    return chain.proceed(newRequest.build());
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }

            }
        });
    }

}
