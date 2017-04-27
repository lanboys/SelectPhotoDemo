package com.bing.lan.selectphoto;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener,  UploadListener {

    private ImageView mViewById;
    private PhotoSelectUtil mSelectPhotoUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mViewById = (ImageView) findViewById(R.id.iv);
        mViewById.setOnClickListener(this);
        mSelectPhotoUtil = new PhotoSelectUtil(this);
        mSelectPhotoUtil.setUploadListener(this);
    }

    @Override
    public void onClick(View view) {

        mSelectPhotoUtil.showSelectAvatarPopup(mViewById);
    }

    // 拍照返回
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mSelectPhotoUtil.onSelectActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void uploadAvatar(ImageView viewById, File source) {

        Toast.makeText(this, "上传图片", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void uploadAvatar(ImageView viewById, Uri source) {
        Toast.makeText(this, "上传图片", Toast.LENGTH_SHORT).show();
    }
}
