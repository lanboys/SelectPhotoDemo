package com.bing.lan.selectphoto;

import android.net.Uri;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by win7 on 2017/4/25.
 */
public interface UploadListener {

    void uploadAvatar(ImageView viewById, File source);

    void uploadAvatar(ImageView viewById, Uri source);
}
