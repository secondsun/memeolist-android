package org.feedhenry.apps.arthenry.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by summers on 11/5/15.
 */
public class GsonUtil {
    public static final Gson GSON;

    static {
        GSON = new GsonBuilder().setDateFormat("MMM dd, yyyy hh:mm:ss a z").create();
    }

}
