package com.bing.lan.selectphoto;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    public Activity mContext;
    private ImageView mViewById;
    private Uri mCurrentPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        mViewById = (ImageView) findViewById(R.id.iv);

        mViewById.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        showSelectAvatarPopup();
    }

    private void showSelectAvatarPopup() {
        PhotoSelectPopupWindow popupWindow = new PhotoSelectPopupWindow(mContext);
        popupWindow.setOnItemClickeListener(new PhotoSelectPopupWindow.OnItemClickListener() {
            @Override
            public void onItemClickListener(@PhotoSelectPopupItemType.Type int type) {
                if (type == PhotoSelectPopupItemType.TAKE_PHOTO) {
                    dispatchTakePictureIntent();
                } else if (type == PhotoSelectPopupItemType.SELECT_ALBUM) {
                    selectAvatarFromAlbum();
                }
            }
        });

        popupWindow.showAtLocation(mViewById, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
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

                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    // 拍照返回
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
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
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            mViewById.setImageDrawable(null);
            mViewById.setImageURI(Crop.getOutput(result));
            uploadAvatar(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(mContext, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // 上传头像
    private void uploadAvatar(Uri source) {
        // File file = new File(source.getPath());
        // mMinePresenter.uploadAvatar(file);

        Toast.makeText(this, "上传图片", Toast.LENGTH_SHORT).show();
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
        mViewById.setImageDrawable(null);
        Crop.pickImage(mContext);
    }
}
