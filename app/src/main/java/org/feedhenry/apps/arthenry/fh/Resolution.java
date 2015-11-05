package org.feedhenry.apps.arthenry.fh;

import android.app.Activity;

public interface Resolution extends Runnable {

    /**
     * Arguments to setup a particular resolution
     *
     * @param activity activity context the resolution is run in
     */
    void setup(Activity activity);

}
