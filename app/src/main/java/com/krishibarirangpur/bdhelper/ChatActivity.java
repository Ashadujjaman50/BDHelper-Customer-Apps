package com.krishibarirangpur.bdhelper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.krishibarirangpur.bdhelper.adapter.AdapterChat;
import com.krishibarirangpur.bdhelper.databinding.ActivityChatBinding;
import com.krishibarirangpur.bdhelper.model.ModelChat;
import com.krishibarirangpur.bdhelper.myUtils.BaseActivity;
import com.krishibarirangpur.bdhelper.myUtils.MyUtils;
import com.krishibarirangpur.bdhelper.myUtils.ThemeUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatActivity extends BaseActivity {

    private ActivityChatBinding binding;

    String receiverUid, currentUid, message;

    AdapterChat adapterChat;
    private ArrayList<ModelChat> chatList;

    //for check if user has seen message or not
    private ValueEventListener seenListener;
    private DatabaseReference userRefForSeen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // থিম আগে সেট কর
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);

        //init views
        Intent intent = getIntent();
        receiverUid = intent.getStringExtra("adminID");

        currentUid = FirebaseAuth.getInstance().getUid();


        binding.backBtn.setOnClickListener(v -> finishOnBack());



        //readMessage
        readMessage();

        //seenLast Message
        seenMessage();


        binding.sendBtn.setOnClickListener(v -> {
            message = binding.messageEt.getText().toString().trim();

            if (TextUtils.isEmpty(message)){
                binding.messageEt.setError("Input text message");
            }
            else {
                //send message
                sendMessage();
            }
            //reset edit text after sending message
            binding.messageEt.setText("");
        });


    }

    private void sendMessage() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        String timestamp = "" + System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", currentUid);
        hashMap.put("receiver", receiverUid);
        hashMap.put("message", message);
        hashMap.put("timestamp", timestamp);
        hashMap.put("isSeen", "false");
        hashMap.put("userType", "customer");

        // Chats node এ push করো
        ref.child("Chats").push().setValue(hashMap)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Chat", "Message sent!");
                    sendPushNotification(receiverUid, message);
                })
                .addOnFailureListener(e -> Log.e("Chat", "Error: " + e.getMessage()));

        // ChatList update করো (only once check)
        DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(currentUid)
                .child(receiverUid);
        chatRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    chatRef1.child("id").setValue(receiverUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(receiverUid)
                .child(currentUid);
        chatRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    chatRef2.child("id").setValue(currentUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendPushNotification(String receiverUid, String message) {
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(receiverUid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String token = documentSnapshot.getString("device_token");
                        if (token != null) {
                            Log.d("Token ", "token: "+token);
                            // TODO: Implement push notification logic here
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("FCM", "❌ Failed to fetch token", e));
    }


    private void readMessage() {
        chatList = new ArrayList<>();
        adapterChat = new AdapterChat(ChatActivity.this, chatList, MyUtils.admin_profile_photo_url);
        binding.chatRv.setAdapter(adapterChat);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats");
        ref.keepSynced(true);
        ref.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelChat chat = ds.getValue(ModelChat.class);

                    if (chat != null && chat.getReceiver() != null && chat.getSender() != null && (chat.getReceiver().equals(currentUid) && chat.getSender().equals(receiverUid) ||
                            chat.getReceiver().equals(receiverUid) && chat.getSender().equals(currentUid))){
                        chatList.add(chat);
                    }
                    //set adapter
                    adapterChat.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void seenMessage() {
        userRefForSeen =FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if (chat != null && chat.getReceiver() != null && chat.getSender() != null && chat.getReceiver().equals(currentUid) && chat.getSender().equals(receiverUid)){
                        HashMap<String, Object> hasSeenMap = new HashMap<>();
                        hasSeenMap.put("isSeen","true");
                        ds.getRef().updateChildren(hasSeenMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        userRefForSeen.removeEventListener(seenListener);
    }
}