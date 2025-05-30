package com.example.jagajajan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jagajajan.R;
import com.example.jagajajan.model.ChatMessage;

import java.util.List;

import android.support.v7.widget.RecyclerView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private Context context;
    private List<ChatMessage> chatList;
    private int userId;

    public ChatAdapter(Context context, List<ChatMessage> chatList, int userId) {
        this.context = context;
        this.chatList = chatList;
        this.userId = userId;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage chat = chatList.get(position);
        if (chat.getIdLampiran() != 0) {
            return 2;
        }
        return (chat.getIdPengirim() == userId) ? 1 : 0;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 2) {
            view = LayoutInflater.from(context).inflate(R.layout.item_chat_acivment, parent, false);
        } else if (viewType == 1) {
            view = LayoutInflater.from(context).inflate(R.layout.item_chat_sent, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_chat_received, parent, false);
        }
        return new ChatViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        ChatMessage message = chatList.get(position);
        holder.textMessage.setText(message.getIsiPesan());
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;

        public ChatViewHolder(View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.text_message);
        }
    }
}
