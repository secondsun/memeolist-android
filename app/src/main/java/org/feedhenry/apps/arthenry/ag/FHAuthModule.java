package org.feedhenry.apps.arthenry.ag;

import com.feedhenry.sdk.FH;

import org.feedhenry.apps.arthenry.fh.FHClient;
import org.jboss.aerogear.android.pipe.http.HttpException;
import org.jboss.aerogear.android.pipe.module.ModuleFields;
import org.jboss.aerogear.android.pipe.module.PipeModule;

import java.net.URI;

import cz.msebera.android.httpclient.Header;

/**
 * Created by summers on 11/5/15.
 */
public class FHAuthModule implements PipeModule {
    public FHAuthModule(FHClient fhClient) {
    }

    @Override
    public ModuleFields loadModule(URI relativeURI, String httpMethod, byte[] requestBody) {
        try {
            ModuleFields fields = new ModuleFields();
            Header[] headers = FH.getDefaultParamsAsHeaders(null);
            for (Header header : headers) {
                fields.addHeader(header.getName(), header.getValue());
            }
            return fields;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean handleError(HttpException exception) {
        return false;
    }
}
