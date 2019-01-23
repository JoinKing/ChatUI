package com.king.chatui.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.hwq.lib_common.utils.Utils;
import com.king.chatui.R;
import com.king.chatui.entity.MessageEntity;
import com.king.chatui.listenter.ChatListenter;
import com.king.chatui.utils.CornerTransform;
import com.king.chatui.utils.MessageType;

public class SendHolder extends BaseViewHolder<MessageEntity> {
    //文本消息
    private TextView tvContent;
    //头像
    private ImageView ivHead;
    private ImageView ivImage;
    private ChatListenter listenter;

    public SendHolder(View view, int viewType,ChatListenter listenter) {
        super(view);
        this.listenter = listenter;
        findViewBy(itemView, viewType);
    }

    private void findViewBy(View itemView, int viewType) {
        tvContent = itemView.findViewById(R.id.tvContent);

        switch (viewType) {
            //RIGHT
            case MessageType.RIGHT_CHAT_FILE_TYPE_TEXT:
                ivHead = itemView.findViewById(R.id.ivHeadPortrait);
                tvContent = itemView.findViewById(R.id.tvContent);
                break;
            case MessageType.RIGHT_CHAT_FILE_TYPE_FILE:
                ivHead = itemView.findViewById(R.id.ivHeadPortrait);
                break;
            case MessageType.RIGHT_CHAT_FILE_TYPE_IMAGE:
                ivHead = itemView.findViewById(R.id.ivHeadPortrait);
                ivImage = itemView.findViewById(R.id.ivImage);

                break;
            case MessageType.RIGHT_CHAT_FILE_TYPE_VOICE:
                ivHead = itemView.findViewById(R.id.ivHeadPortrait);
                tvContent = itemView.findViewById(R.id.tvContent);
                break;
            case MessageType.RIGHT_CHAT_FILE_TYPE_CONTACT:
                ivHead = itemView.findViewById(R.id.ivHeadPortrait);

                break;
            case MessageType.RIGHT_CHAT_FILE_TYPE_LINK:
                ivHead = itemView.findViewById(R.id.ivHeadPortrait);
                break;
        }
    }

    @Override
    public void setData(final MessageEntity data) {
        initHead(data.getHeader(),ivHead);
        switch (data.getType()) {
            case MessageType.RIGHT_CHAT_FILE_TYPE_TEXT://文本消息
                tvContent.setText(data.getContent());
                break;
            case MessageType.RIGHT_CHAT_FILE_TYPE_FILE:
                break;
            case MessageType.RIGHT_CHAT_FILE_TYPE_IMAGE:
                Glide.with(Utils.getContext())
                        .load(data.getFilepath())
                        .apply(new RequestOptions()
                                .placeholder(R.mipmap.ic_launcher)
                                .transform(new RoundedCorners(15)))
                        .into(ivImage);
                ivImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (null!=listenter){
                            listenter.chatImageOnClickListenter(data.getFilepath());
                        }

                    }
                });

                break;
            case MessageType.RIGHT_CHAT_FILE_TYPE_VOICE:
                tvContent.setText(com.king.chatui.utils.Utils.formatTime(data.getVoiceTime()));
                tvContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (null!=listenter){
                            listenter.chatVoiceListenter(data.getFilepath());
                        }

                    }
                });
                break;
            case MessageType.RIGHT_CHAT_FILE_TYPE_CONTACT:

                break;
            case MessageType.RIGHT_CHAT_FILE_TYPE_LINK:

                break;
        }
    }

    private void initHead(String header, ImageView ivHead) {
        Glide.with(Utils.getContext())
                .load(header)
                .apply(new RequestOptions().placeholder(R.mipmap.icon_default_head).circleCrop())
                .into(ivHead);
    }

}
