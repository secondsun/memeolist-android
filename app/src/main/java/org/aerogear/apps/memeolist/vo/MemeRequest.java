package org.aerogear.apps.memeolist.vo;

import org.jboss.aerogear.android.core.RecordId;

import java.io.InputStream;

/**
 * Created by summers on 11/5/15.
 */
public class MemeRequest {
    @RecordId
    private String id = null;

    private String ownerId, topMessage, bottomMessage, fileNameResult;
    private InputStream image;

    private MemeRequest(String ownerId, String topMessage, String bottomMessage, InputStream image) {
        this.ownerId = ownerId;
        this.topMessage = topMessage;
        this.bottomMessage = bottomMessage;
        this.image = image;

    }

    public InputStream getImage() {
        return image;
    }

    public String getId() {
        return null;
    }

    public void setId(String id) {
        //noop
    }

    public void setImage(InputStream image) {
        this.image = image;
    }

    public String getFileNameResult() {
        return fileNameResult;
    }

    public void setFileNameResult(String fileNameResult) {
        this.fileNameResult = fileNameResult;
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

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public void setTopMessage(String topMessage) {
        this.topMessage = topMessage;
    }

    public void setBottomMessage(String bottomMessage) {
        this.bottomMessage = bottomMessage;
    }

    public static class Builder {
        private String ownerId, topMessage, bottomMessage;
        private InputStream image;

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

        public InputStream getImage() {
            return image;
        }

        public Builder setImage(InputStream image) {
            this.image = image;
            return this;
        }

        public MemeRequest build() {
            return new MemeRequest(ownerId, topMessage, bottomMessage, image);
        }

    }

}
