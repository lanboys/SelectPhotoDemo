package com.bing.lan.selectphoto;

/**
 * Author: 蓝兵
 * Email: lan_bing2013@163.com
 * Time: 2017/4/13  10:58
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

/**
 * 头像选择
 */
public class PhotoSelectPopupWindow extends PopupWindow implements View.OnClickListener {

    private Context mContext;
    private Button btn_take_photo;
    private Button btn_photo_album;
    private Button btn_cancel;

    private OnItemClickListener mItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    public PhotoSelectPopupWindow(Context context) {
        mContext = context;

        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.photo_selecte_popup, null);

        btn_take_photo = (Button) view.findViewById(R.id.btn_take_photo);
        btn_photo_album = (Button) view.findViewById(R.id.btn_photo_album);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);

        this.setContentView(view);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setAnimationStyle(R.style.PopupWindowAnimation);
        // ColorDrawable dw = new ColorDrawable(0xb0000000);
        // this.setBackgroundDrawable(dw);

        btn_take_photo.setOnClickListener(this);
        btn_photo_album.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_take_photo:
                itemCallBack(PhotoSelectPopupItemType.TAKE_PHOTO);
                break;
            case R.id.btn_photo_album:
                itemCallBack(PhotoSelectPopupItemType.SELECT_ALBUM);
                break;
            case R.id.btn_cancel:
                itemCallBack(PhotoSelectPopupItemType.TAKE_PHOTO);
                break;
            default:
                break;
        }

        dismiss();
    }

    private void itemCallBack(int type) {
        if (mItemClickListener != null) {
            mItemClickListener.onItemClickListener(type);
        }
    }

    public interface OnItemClickListener {

        void onItemClickListener(@PhotoSelectPopupItemType.Type int type);
    }
}
