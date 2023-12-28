package com.example.messagerapp.messages;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.messagerapp.R;
import com.example.messagerapp.chat.Chat;
import com.example.messagerapp.databinding.MessagesAdapterLayoutBinding;
import com.example.messagerapp.messages.MessagesList;
import com.squareup.picasso.Picasso;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder> {

    private List<MessagesList> messagesLists;
    private final Context context;

    public MessagesAdapter(List<MessagesList> messagesLists, Context context) {
        this.messagesLists = messagesLists;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MessagesAdapterLayoutBinding binding = MessagesAdapterLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MessagesList list2 = messagesLists.get(position);

        if (!list2.getProfileImg().isEmpty()) {
            Picasso.get().load(list2.getProfileImg()).into(holder.binding.profileImg);
        }

        holder.binding.name.setText(list2.getName());
        holder.binding.lastmsg.setText(list2.getLastMessage());

        if (list2.getUnseenMessages() == 0) {
            holder.binding.unseenMsgs.setVisibility(View.GONE);
            holder.binding.lastmsg.setTextColor(Color.parseColor("#959595"));
        } else {
            holder.binding.unseenMsgs.setVisibility(View.VISIBLE);
            holder.binding.unseenMsgs.setText(String.valueOf(list2.getUnseenMessages()));
            holder.binding.lastmsg.setTextColor(context.getResources().getColor(R.color.theme_color_8));
        }

        holder.binding.rootlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Chat.class);
                intent.putExtra("phone", list2.getPhone());
                intent.putExtra("name", list2.getName());
                intent.putExtra("profile_img", list2.getProfileImg());
                intent.putExtra("chat_key", list2.getChatKey());
                context.startActivity(intent);
            }
        });
    }

    public void updateData(List<MessagesList> messagesLists) {
        this.messagesLists = messagesLists;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return messagesLists.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final MessagesAdapterLayoutBinding binding;

        public MyViewHolder(@NonNull MessagesAdapterLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

