package com.king.chatui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hwq.lib_common.base.BaseFragment;
import com.hwq.lib_common.bus.RxBus;
import com.hwq.lib_common.rxpermissions.RxPermissions;
import com.hwq.lib_common.utils.ImageUtils;
import com.hwq.lib_common.utils.KLog;
import com.hwq.lib_common.utils.ToastUtils;
import com.king.chatui.R;
import com.king.chatui.entity.MessageEntity;
import com.king.chatui.utils.MessageType;
import com.king.chatui.utils.PhotoBitmapUtils;
import com.king.chatui.utils.PhotoUtils;

import java.io.File;

import io.reactivex.functions.Consumer;

/**
 * 功能面板
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class ChatFunctionFragment extends Fragment implements View.OnClickListener {
    private static final int PHOTO = 0X001;
    private static final int CAMERA = 0X002;
    private TextView chat_function_album;
    private TextView chat_function_capture;
    private TextView chat_function_contact;
    private TextView chat_function_file;
    private File fileUri = null;
    private Uri imageUri;
    private View view;

    public ChatFunctionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat_function, container, false);
        // Inflate the layout for this fragment
        initView(view);
        return view;
    }

    private void initView(View view) {
        chat_function_album = view.findViewById(R.id.chat_function_album);
        chat_function_capture = view.findViewById(R.id.chat_function_capture);
        chat_function_contact = view.findViewById(R.id.chat_function_contact);
        chat_function_file = view.findViewById(R.id.chat_function_file);
        chat_function_album.setOnClickListener(this);
        chat_function_capture.setOnClickListener(this);
        chat_function_contact.setOnClickListener(this);
        chat_function_file.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.chat_function_album:
                openPhoto();
                break;
            case R.id.chat_function_capture:
                openCamera();
                break;
            case R.id.chat_function_contact:
                ToastUtils.showShort("名片");
                break;
            case R.id.chat_function_file:
                ToastUtils.showShort("文件");
                break;

        }

    }

    //打开相机
    private void openCamera() {
        if (isPermissions()) {
            fileUri = ImageUtils.createFile(new File(Environment.getExternalStorageDirectory().getPath() + "/huang"), "", ".jpg");
            imageUri = Uri.fromFile(fileUri);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                imageUri = FileProvider.getUriForFile(getContext(), "com.king.chatui.fileprovider", fileUri);//通过FileProvider创建一个content类型的Uri
            PhotoUtils.takePicture(ChatFunctionFragment.this, imageUri, CAMERA);
        }

    }

    private boolean isPermission = false;

    @SuppressLint("CheckResult")
    public boolean isPermissions() {
        String photoPermissions[] = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        RxPermissions permissions = new RxPermissions(getActivity());
        permissions.request(photoPermissions)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            isPermission = true;
                        } else {
                            isPermission = false;
                            ToastUtils.showShort("权限被拒绝");

                        }
                    }
                });
        return isPermission;
    }

    /**
     * 打开相册
     */
    private void openPhoto() {
        if (isPermissions()) {
            PhotoUtils.openPic(getActivity(), PHOTO);
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        MessageEntity messageEntity = new MessageEntity();
                        messageEntity.setFilepath(getFilePath(data.getData()));
                        messageEntity.setType(MessageType.RIGHT_CHAT_FILE_TYPE_IMAGE);
                        RxBus.getDefault().post(messageEntity);
                    } catch (Exception e) {
                        ToastUtils.showShort("图片损坏，请重新选择");
                    }
                }
                break;
            case CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    MessageEntity messageInfo = new MessageEntity();
                    messageInfo.setType(MessageType.RIGHT_CHAT_FILE_TYPE_IMAGE);
                    messageInfo.setFilepath(fileUri.getAbsolutePath());
                    File file = new File(messageInfo.getFilepath());
                    RxBus.getDefault().post(messageInfo);//发送图片
                }
                break;
        }


    }

    public String getFilePath(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
}
