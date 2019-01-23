package com.king.chatui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.king.chatui.R;
import com.king.chatui.entity.MessageEntity;
import com.king.chatui.holder.BaseViewHolder;
import com.king.chatui.holder.ReceiveHolder;
import com.king.chatui.holder.SendHolder;
import com.king.chatui.listenter.ChatListenter;
import com.king.chatui.utils.MessageType;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<MessageEntity> messageEntityList;
    private ChatListenter listenter;

    public ChatAdapter(List<MessageEntity> messageEntityList) {
        this.messageEntityList = messageEntityList;
        notifyDataSetChanged();
    }

    public void setListenter(ChatListenter listenter) {
        this.listenter = listenter;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder viewHolder = null;
        View view ;
        switch (viewType) {
            //LEFT
            case MessageType.LEFT_CHAT_FILE_TYPE_TEXT:
                view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.item_left_chat_file_type_text, parent, false);
                viewHolder = new ReceiveHolder(view,viewType,listenter);
                break;
            case MessageType.LEFT_CHAT_FILE_TYPE_FILE:
                view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.item_left_chat_file_type_file, parent, false);
                viewHolder = new ReceiveHolder(view,viewType,listenter);
                break;
            case MessageType.LEFT_CHAT_FILE_TYPE_IMAGE:
                view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.item_left_chat_file_type_image, parent, false);
                viewHolder = new ReceiveHolder(view,viewType,listenter);
                break;
            case MessageType.LEFT_CHAT_FILE_TYPE_VOICE:
                view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.item_left_chat_file_type_voice, parent, false);
                viewHolder = new ReceiveHolder(view,viewType,listenter);
                break;
            case MessageType.LEFT_CHAT_FILE_TYPE_CONTACT:
                view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.item_left_chat_file_type_contact, parent, false);
                viewHolder = new ReceiveHolder(view,viewType,listenter);
                break;
            case MessageType.LEFT_CHAT_FILE_TYPE_LINK:
                view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.item_left_chat_file_type_link, parent, false);
                viewHolder = new ReceiveHolder(view,viewType,listenter);
                break;
            //RIGHT
            case MessageType.RIGHT_CHAT_FILE_TYPE_TEXT:
                view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.item_right_chat_file_type_text, parent, false);
                viewHolder = new SendHolder(view,viewType,listenter);
                break;
            case MessageType.RIGHT_CHAT_FILE_TYPE_FILE:
                view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.item_right_chat_file_type_file, parent, false);
                viewHolder = new SendHolder(view,viewType,listenter);
                break;
            case MessageType.RIGHT_CHAT_FILE_TYPE_IMAGE:
                view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.item_right_chat_file_type_image, parent, false);
                viewHolder = new SendHolder(view,viewType,listenter);
                break;
            case MessageType.RIGHT_CHAT_FILE_TYPE_VOICE:
                view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.item_right_chat_file_type_voice, parent, false);
                viewHolder = new SendHolder(view,viewType,listenter);
                break;
            case MessageType.RIGHT_CHAT_FILE_TYPE_CONTACT:
                view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.item_right_chat_file_type_contact, parent, false);
                viewHolder = new SendHolder(view,viewType,listenter);
                break;
            case MessageType.RIGHT_CHAT_FILE_TYPE_LINK:
                view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.item_right_chat_file_type_link, parent, false);
                viewHolder = new SendHolder(view,viewType,listenter);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.itemView.setTag(position);
        holder.setData(messageEntityList.get(position));
    }

    @Override
    public int getItemCount() {
        return messageEntityList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return messageEntityList.get(position).getType();
    }

    public void add(MessageEntity messageEntity) {
        if (messageEntityList == null) {
            messageEntityList = new ArrayList<>();
        }

        this.messageEntityList.add(0,messageEntity);
        notifyItemInserted(0);
    }
}
