package com.bing.lan.selectphoto;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Author: yxhuang
 * Date: 2016/12/28
 * Email: yxhuang@gmail.com
 */

/**
 *  选择头像来源的类型
 */
public class PhotoSelectPopupItemType {

    @IntDef({TAKE_PHOTO, SELECT_ALBUM, CANCEL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type{}

    public static final int TAKE_PHOTO = 0;         // 拍照
    public static final int SELECT_ALBUM = 1;      // 相册
    public static final int CANCEL = 2;             // 取消
}
