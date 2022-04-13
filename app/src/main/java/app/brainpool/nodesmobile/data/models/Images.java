package app.brainpool.nodesmobile.data.models;

import android.net.Uri;

/**
 * Created by MAYURA on 10/17/2017.
 */

public class Images {

    Uri uri;
    String url;
    boolean isOnline, isEditable;

    public Images(Uri uri, String url, boolean isOnline, boolean isEditable) {
        this.uri = uri;
        this.url = url;
        this.isOnline = isOnline;
        this.isEditable = isEditable;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }
}
