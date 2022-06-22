package kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.utils;

import android.net.Uri;

public class FileInfoVO {
    private Uri uri = null;
    private String name = null;
    private String size = null;
    private String uid = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
