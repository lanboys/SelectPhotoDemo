package com.bing.lan.selectphoto;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
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
public class PhotoSelectUtil1 {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private Activity mContext;
    private ImageView mImageView;
    private Uri mCurrentPhotoUri;
    private UploadListener mUploadListener;

    public void setUploadListener(UploadListener uploadListener) {
        mUploadListener = uploadListener;
    }

    public PhotoSelectUtil1(Activity context) {
        mContext = context;
    }

    public void showSelectAvatarPopup(ImageView imageView) {

        if (imageView == null) {
            Toast.makeText(mContext, "ImageView不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        mImageView = imageView;

        PhotoSelectPopupWindow popupWindow = new PhotoSelectPopupWindow(mContext);
        popupWindow.setOnItemClickListener(new PhotoSelectPopupWindow.OnItemClickListener() {
            @Override
            public void onItemClickListener(@PhotoSelectPopupWindow.PopupItemType.Type int type) {
                if (type == PhotoSelectPopupWindow.PopupItemType.TAKE_PHOTO) {
                    dispatchTakePictureIntent();
                } else if (type == PhotoSelectPopupWindow.PopupItemType.SELECT_ALBUM) {
                    selectAvatarFromAlbum();
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
                photoFile = createImageFile();
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
                mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,mCurrentPhotoUri));
                beginCrop(mCurrentPhotoUri);
            } else if (requestCode == Crop.REQUEST_PICK) {
                beginCrop(data.getData());
            }
        }

        if (requestCode == Crop.REQUEST_CROP) {

            handleCrop(resultCode, data);
        }
    }



    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(mContext.getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(mContext);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            mImageView.setImageDrawable(null);
            mImageView.setImageURI(Crop.getOutput(result));
            if (mUploadListener != null) {
                mUploadListener.uploadAvatar(mImageView, Crop.getOutput(result));
            }
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(mContext, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // 创建图片路径
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,      /* prefix */
                ".jpg",             /* suffix */
                storageDir          /* directory */
        );

        return image;
    }

    // 选择头像
    private void selectAvatarFromAlbum() {
        //mImageView.setImageDrawable(null);
        Crop.pickImage(mContext);
    }
}
