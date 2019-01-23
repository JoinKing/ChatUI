package com.king.chatui.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by king
 * @date 2019.1.17
 */

public class BaseViewHolder<M> extends RecyclerView.ViewHolder {

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public void setData(M data) {

    }
}
