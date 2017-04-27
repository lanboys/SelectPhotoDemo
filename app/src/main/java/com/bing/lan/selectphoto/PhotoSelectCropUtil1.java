package com.bing.lan.selectphoto;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

/**
 * Author: 蓝兵
 * Email: lan_bing2013@163.com
 * Time: 2017/4/13  12:19
 */

/**
 * 注意注册activity
 * <p>
 * <activity
 * android:name="com.soundcloud.android.crop.CropImageActivity"
 * android:screenOrientation="portrait">
 * </activity>
 */

public class PhotoSelectCropUtil1 {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    protected final LogUtil log = LogUtil.getLogUtil(getClass(), LogUtil.LOG_VERBOSE);
    private Activity mContext;
    private ImageView mImageView;
    private Uri mCurrentPhotoUri;
    private UploadListener mUploadListener;

    public PhotoSelectCropUtil1(Activity context) {
        mContext = context;
    }

    public void setUploadListener(UploadListener uploadListener) {
        mUploadListener = uploadListener;
    }

    public void showSelectAvatarPopup(ImageView imageView) {

        if (imageView == null) {
            Toast.makeText(mContext, "ImageView不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        mImageView = imageView;

        final PhotoSelectPopupWindow popupWindow = new PhotoSelectPopupWindow(mContext);
        popupWindow.setOnItemClickListener(new PhotoSelectPopupWindow.OnItemClickListener() {
            @Override
            public void onItemClickListener(@PhotoSelectPopupWindow.PopupItemType.Type int type) {
                if (type == PhotoSelectPopupWindow.PopupItemType.TAKE_PHOTO) {
                    //拍照
                    dispatchTakePictureIntent();
                } else if (type == PhotoSelectPopupWindow.PopupItemType.SELECT_ALBUM) {
                    //相册 选择
                    selectAvatarFromAlbum();
                } else if (type == PhotoSelectPopupWindow.PopupItemType.CANCEL) {
                    popupWindow.dismiss();
                }
            }
        });

        popupWindow.showAtLocation(mImageView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    // 拍照
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile(null);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (photoFile != null) {
                //  Uri photoUri = FileProvider.getUriForFile(mContext, "com.yujingtravelagent.globaltrip.ui.mine.fileprovider", photoFile);

                Uri photoUri = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

                mCurrentPhotoUri = photoUri;

                mContext.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    // 拍照返回
    public void onSelectActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                //系统拍照  界面返回
                //log.e("onSelectActivityResult(): mCurrentPhotoUri---" + mCurrentPhotoUri);
                //file:///storage/emulated/0/Android/data/com.bing.lan.comm/files/Pictures/JPEG_20170426_144058_435020664.jpg
                beginCrop(mCurrentPhotoUri);
            } else if (requestCode == Crop.REQUEST_PICK) {
                //系统选择照片  界面返回
                beginCrop(data.getData());
            }
        }

        //裁剪图片 界面返回
        if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }
    }

    //进入裁剪页面
    private void beginCrop(Uri source) {

        //File file = new File(mContext.getCacheDir(), "cropped");
        String path = source.getPath();
        String substring = path.substring(0, path.lastIndexOf("."));
        String newPath = substring + "_cropped.jpg";
        File file = new File(newPath);

        Uri destination = Uri.fromFile(file);
        log.e("beginCrop()--------source--Uri: " + source);//     file:///storage/emulated/0/Android/data/com.bing.lan.comm/files/Pictures/JPEG_20170426_144058_435020664.jpg
        log.e("beginCrop()---destination--Uri: " + destination);//   file:///data/user/0/com.bing.lan.comm/cache/cropped
        log.e("beginCrop()--destination--file: " + file);//    /data/user/0/com.bing.lan.comm/cache/cropped

        Crop.of(source, destination).asSquare().start(mContext);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            mImageView.setImageDrawable(null);
            Uri uri = Crop.getOutput(result);
            //mImageView.setImageURI(uri);

            String uriPath = uri.getPath();
            File file = new File(uriPath);
            log.e("handleCrop(): 选择或者拍照图片地址：" + file);//  /data/user/0/com.bing.lan.comm/cache/cropped

            File sourceFile = null;
            try {

                //http://blog.csdn.net/infsafe/article/details/7744582  bitmap 将图片压缩到指定的大小

                //BitmapFactory.Options options = new BitmapFactory.Options();
                //options.inJustDecodeBounds = false;
                //Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                //long bitmapsize = BitmapUtil.getBitmapsize(bitmap);

                BitmapFactory.Options opts = new BitmapFactory.Options();

                //原始图片
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
                long bitmapsize = BitmapUtil.getBitmapsize(bitmap);
                String fileSize = Formatter.formatFileSize(mContext, bitmapsize / 8);
                log.e("handleCrop(): 选择或者拍照图片的大小---压缩前:" + fileSize);
                //log.e("handleCrop(): 选择或者拍照图片的大小---压缩前:" + bitmapsize);

                if (bitmapsize > 12582912) {//大于 1.5 m

                    String substring = uriPath.substring(0, uriPath.lastIndexOf("."));
                    String newPath = substring + "_small.jpg";

                    //压缩后图片保存
                    File smallFile = BitmapUtil.savePicToSdcard(bitmap, new File(newPath), 20);

                    BitmapFactory.Options opts1 = new BitmapFactory.Options();
                    opts1.inSampleSize = 36;//缩小四倍
                    opts1.inPreferredConfig = Bitmap.Config.RGB_565;

                    //压缩后图片显示
                    Bitmap bitmap1 = BitmapFactory.decodeFile(smallFile.getAbsolutePath(), opts1);

                    sourceFile = smallFile;
                    mImageView.setImageBitmap(bitmap1);
                    String fileSize1 = Formatter.formatFileSize(mContext, BitmapUtil.getBitmapsize(bitmap1) / 8);
                    log.e("handleCrop(): 选择或者拍照图片的大小---压缩后:" + fileSize1);

                    bitmap.recycle();
                } else {
                    sourceFile = file;
                    mImageView.setImageBitmap(bitmap);
                }
            } catch (Exception e) {
                log.e("handleCrop():  " + e.getLocalizedMessage());
            }

            //回调
            if (mUploadListener != null) {
                mUploadListener.uploadAvatar(mImageView, sourceFile);
            }
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(mContext, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // 创建图片路径
    private File createImageFile(String str) throws IOException {
        String timeStamp = new SimpleDateFormat("MMddHHmmss").format(new Date());//yyyyMMdd_

        //log.e("createImageFile() timeStamp : " + timeStamp);//  20170426_144058

        String imageFileName = "J_" + timeStamp + "_";
        if (str != null) {
            imageFileName += str;
        }
        //log.e("createImageFile() imageFileName: " + imageFileName);//  JPEG_20170426_144058_
        File storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,      /* prefix */
                ".jpg",             /* suffix */
                storageDir          /* directory */
        );

        //  log.e("createImageFile()选择图片路径: " + image);
        ///storage/emulated/0/Android/data/com.bing.lan.comm/files/Pictures/JPEG_20170426_144058_435020664.jpg

        return image;
    }

    // 选择头像
    private void selectAvatarFromAlbum() {
        //mImageView.setImageDrawable(null);
        Crop.pickImage(mContext);
    }
}
