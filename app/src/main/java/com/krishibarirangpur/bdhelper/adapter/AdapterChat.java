package com.krishibarirangpur.bdhelper.adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.model.ModelChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.HolderViewChat> {

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    Context context;
    ArrayList<ModelChat> chatList;
    private final String imageUrl;
    FirebaseUser firebaseUser;

    public AdapterChat(Context context, ArrayList<ModelChat> chatList, String imageUrl) {
        this.context = context;
        this.chatList = chatList;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public HolderViewChat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //Inflate Layout
        if (viewType==MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right,parent, false);
            return new HolderViewChat(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left,parent, false);
            return new HolderViewChat(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull HolderViewChat holder, int position) {
        //get model data
        ModelChat modelChat = chatList.get(position);

        String message = modelChat.getMessage();
        String timestamp = modelChat.getTimestamp();
        String isSeen = modelChat.getIsSeen();

        //convert Timestamp to dd/mm/yyyy  hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String messageTime = DateFormat.format("dd-MMM-yyyy hh:mm aa", calendar).toString();

        //set data
        messageTime = messageTime.replace("AM", "am");
        messageTime = messageTime.replace("PM", "pm");
        holder.messageTv.setText(message);
        holder.timeTv.setText(messageTime);

        try{
            Picasso.get().load(imageUrl).into(holder.profileIv);
        } catch (Exception e) {
        }

        //set seen/delivered status of message
        if (position== chatList.size() -1){
            if (isSeen.equals("true")){
                holder.isSeenTv.setText("Seen");
            }
            else {
                holder.isSeenTv.setText("Delivered");
            }
        }
        else {
            holder.isSeenTv.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }


    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }
    }

    public static class HolderViewChat extends RecyclerView.ViewHolder{
        public ImageView profileIv;
        public TextView messageTv, timeTv, isSeenTv;

        public HolderViewChat(@NonNull View itemView) {
            super(itemView);

            profileIv = itemView.findViewById(R.id.profileIv);
            messageTv = itemView.findViewById(R.id.messageTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            isSeenTv = itemView.findViewById(R.id.isSeenTv);
        }
    }
}
