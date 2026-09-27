package com.example.kodingapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class ChatAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<ChatMessage> chatList;

    public ChatAdapter(Context context, ArrayList<ChatMessage> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @Override
    public int getCount() { 
        return chatList.size(); 
    }

    @Override
    public Object getItem(int position) { 
        return chatList.get(position); 
    }

    @Override
    public long getItemId(int position) { 
        return position; 
    }

    // Pattern ViewHolder untuk performa ListView yang optimal
    private static class ViewHolder {
        View layoutLeft;
        View layoutRight;
        TextView tvSenderLeft;
        TextView tvMessageLeft;
        ImageView imgAvatarLeft;
        TextView tvMessageRight;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);
            holder = new ViewHolder();
            holder.layoutLeft = convertView.findViewById(R.id.layoutLeft);
            holder.layoutRight = convertView.findViewById(R.id.layoutRight);
            holder.tvSenderLeft = (TextView) convertView.findViewById(R.id.tvSenderLeft);
            holder.tvMessageLeft = (TextView) convertView.findViewById(R.id.tvMessageLeft);
            holder.imgAvatarLeft = (ImageView) convertView.findViewById(R.id.imgAvatarLeft);
            holder.tvMessageRight = (TextView) convertView.findViewById(R.id.tvMessageRight);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ChatMessage message = chatList.get(position);

        if (message.isMe()) {
            holder.layoutLeft.setVisibility(View.GONE);
            holder.layoutRight.setVisibility(View.VISIBLE);
            holder.tvMessageRight.setText(message.getMessage());
        } else {
            holder.layoutLeft.setVisibility(View.VISIBLE);
            holder.layoutRight.setVisibility(View.GONE);

            holder.tvSenderLeft.setText(message.getSender());
            holder.tvMessageLeft.setText(message.getMessage());

            // Render Foto profil dari teks Base64
            if (message.getAvatarBase64() != null && !message.getAvatarBase64().isEmpty()) {
                try {
                    byte[] decodedString = Base64.decode(message.getAvatarBase64(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    if (decodedByte != null) {
                        holder.imgAvatarLeft.setImageBitmap(decodedByte);
                    } else {
                        holder.imgAvatarLeft.setImageResource(android.R.drawable.sym_def_app_icon);
                    }
                } catch (Exception e) {
                    holder.imgAvatarLeft.setImageResource(android.R.drawable.sym_def_app_icon);
                }
            } else {
                holder.imgAvatarLeft.setImageResource(android.R.drawable.sym_def_app_icon);
            }
        }
        return convertView;
    }
}