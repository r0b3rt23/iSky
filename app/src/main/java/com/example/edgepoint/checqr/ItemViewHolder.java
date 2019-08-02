package com.example.edgepoint.checqr;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
public class ItemViewHolder extends RecyclerView.ViewHolder{
    public TextView itemContent;
    public ItemViewHolder(View itemView) {
        super(itemView);
        itemContent = (TextView)itemView.findViewById(R.id.item_content);
    }
}
