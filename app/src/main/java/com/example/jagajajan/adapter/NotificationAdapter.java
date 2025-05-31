package com.example.jagajajan.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jagajajan.ChatActivity;
import com.example.jagajajan.R;
import com.example.jagajajan.model.NotificationItem;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private Context context;
    private List<NotificationItem> notificationList;

    public NotificationAdapter(Context context, List<NotificationItem> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // You'll need to create a layout file for each notification item (e.g., item_notification.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationItem item = notificationList.get(position);
        holder.tvNotificationTitle.setText(item.getTitle());
        holder.tvNotificationMessage.setText(item.getMessage());
        holder.tvNotificationTimestamp.setText(item.getTimestamp()); // You might want to format this

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("id", String.valueOf(item.getPengirimId()));
            intent.putExtra("nama", item.getTitle());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView tvNotificationTitle, tvNotificationMessage, tvNotificationTimestamp;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNotificationTitle = itemView.findViewById(R.id.tv_notification_title); // ID from item_notification.xml
            tvNotificationMessage = itemView.findViewById(R.id.tv_notification_message); // ID from item_notification.xml
            tvNotificationTimestamp = itemView.findViewById(R.id.tv_notification_timestamp); // ID from item_notification.xml
        }

    }
}