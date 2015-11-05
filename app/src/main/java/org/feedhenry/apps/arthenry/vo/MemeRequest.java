package org.feedhenry.apps.arthenry.vo;

import org.jboss.aerogear.android.core.RecordId;

import java.io.File;

/**
 * Created by summers on 11/5/15.
 */
public class MemeRequest {
    @RecordId
    private String id = null;

    private final String ownerId, topMessage, bottomMessage;
    private final File image;

    private MemeRequest(String ownerId, String topMessage, String bottomMessage, File image) {
        this.ownerId = ownerId;
        this.topMessage = topMessage;
        this.bottomMessage = bottomMessage;
        this.image = image;

    }

    public File getImage() {
        return image;
    }

    public String getId() {
        return null;
    }

    public void setId(String id) {
        //noop
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getTopMessage() {
        return topMessage;
    }

    public String getBottomMessage() {
        return bottomMessage;
    }

    public static class Builder {
        private String ownerId, topMessage, bottomMessage;
        private File image;

        public String getOwnerId() {
            return ownerId;
        }

        public Builder setOwnerId(String ownerId) {
            this.ownerId = ownerId;
            return this;
        }

        public String getTopMessage() {
            return topMessage;
        }

        public Builder setTopMessage(String topMessage) {
            this.topMessage = topMessage;
            return this;
        }

        public String getBottomMessage() {
            return bottomMessage;
        }

        public Builder setBottomMessage(String bottomMessage) {
            this.bottomMessage = bottomMessage;
            return this;
        }

        public File getImage() {
            return image;
        }

        public Builder setImage(File image) {
            this.image = image;
            return this;
        }

        public MemeRequest build() {
            return new MemeRequest(ownerId, topMessage, bottomMessage, image);
        }

    }

}
