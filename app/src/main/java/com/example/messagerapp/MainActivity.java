package com.example.messagerapp;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messagerapp.messages.MessagesAdapter;
import com.example.messagerapp.messages.MessagesList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private final List<MessagesList> messagesLists = new ArrayList<>();
    private String phone;
    private String name;
    private String email;
    private int unseenMessages = 0;
    private String lastMessage = "";
    private String chatKey = "";
    private RecyclerView messagesRecyclerView;
    private MessagesAdapter messagesAdapter;
    private boolean dataSet = false;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://messengerapp-153c2-default-rtdb.firebaseio.com");
    private Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final CircleImageView userProfileImg = findViewById(R.id.userProfileImg);

        messagesRecyclerView = findViewById(R.id.Rvi_messages);

        // получить данные из активности Registration Activity.class
        phone = getIntent().getStringExtra("phone");
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");

        messagesRecyclerView.setHasFixedSize(true);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // устанавливаем адаптер к ресайклервью
        messagesAdapter = new MessagesAdapter(messagesLists, MainActivity.this);
        messagesRecyclerView.setAdapter(messagesAdapter);


        progressDialog = new Dialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setContentView(new ProgressBar(this));
        progressDialog.show();


        // Получаем картинку профиля из базы данных Firebase
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final String profileImgUrl = snapshot.child("users").child(phone).child("profile_pic").getValue(String.class);

                if (profileImgUrl != null && !profileImgUrl.isEmpty()) {
                    // устанавливаем картинку профиля в circle image View
                    Picasso.get().load(profileImgUrl).into(userProfileImg);
                }

                progressDialog.dismiss();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MainActivity", "Database Error: " + error.getMessage());

                // Закрываем ProgressDialog в случае ошибки
                progressDialog.dismiss();
            }
        });
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                unseenMessages = 0;
                messagesLists.clear();
                lastMessage = "";
                chatKey = "";

                for(DataSnapshot dataSnapshot : snapshot.child("users").getChildren()){

                    final String getPhone = dataSnapshot.getKey();

                    dataSet = false;

                    if(!getPhone.equals(phone)){

                        final String getName = dataSnapshot.child("name").getValue(String.class);
                        final String getProfileImg = dataSnapshot.child("profile_img").getValue(String.class);


                        databaseReference.child("chat").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                int getChatCounts = (int)snapshot.getChildrenCount();

                                if(getChatCounts > 0) {

                                    for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {

                                        final String getKey = dataSnapshot1.getKey();
                                        chatKey = getKey;

                                        if (dataSnapshot1.hasChild("user_1") && dataSnapshot1.hasChild("user_2") && dataSnapshot1.hasChild("message")) {

                                            final String getUserOne = dataSnapshot1.child("user_1").getValue(String.class);
                                            final String getUserTwo = dataSnapshot1.child("user_2").getValue(String.class);

                                            if((getUserOne.equals(getPhone) && getUserTwo.equals(phone)) || (getUserOne.equals(phone) && getUserTwo.equals(getPhone))) {

                                                for (DataSnapshot chatDataSnapshot : dataSnapshot1.child("messages").getChildren()) {

                                                    final long getMessageKey = Long.parseLong(chatDataSnapshot.getKey());
                                                    final long getLastSeenMessage = Long.parseLong(MemoryData.geLastMsgTs(MainActivity.this, getKey));

                                                    lastMessage = chatDataSnapshot.child("msg").getValue(String.class);
                                                    if (getMessageKey > getLastSeenMessage) {
                                                        unseenMessages++;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                if (!dataSet){

                                    dataSet = true;
                                    MessagesList messagesList = new MessagesList(getName, getPhone, lastMessage, getProfileImg, unseenMessages, chatKey);
                                    messagesLists.add(messagesList);
                                    messagesAdapter.updateData(messagesLists);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
