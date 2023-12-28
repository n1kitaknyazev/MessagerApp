package com.example.messagerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.example.messagerapp.databinding.ActivityRegistrationBinding;
import com.google.firebase.database.ValueEventListener;

public class RegistrationActivity extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://messengerapp-153c2-default-rtdb.firebaseio.com/");
    private ActivityRegistrationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final EditText name = binding.edtRegName;
        final EditText phone = binding.edtRegPhn;
        final EditText email = binding.edtRegEmail;
        final AppCompatButton signUpBtn = binding.btnRegSignUp;

        final Dialog progressDialog = new Dialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setContentView(new ProgressBar(this));

        //проверяем  выполнен ли уже вход пользователем
        if (!MemoryData.getData(this).isEmpty()) {

            Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
            intent.putExtra("phone", MemoryData.getData(this));
            intent.putExtra("name", MemoryData.getName(this));
            intent.putExtra("email", "");
            startActivity(intent);
            finish();
        }

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.show();

                final String nameTxt = name.getText().toString();
                final String phoneTxt = phone.getText().toString();
                final String emailTxt = email.getText().toString();

                if (nameTxt.isEmpty() || phoneTxt.isEmpty() || emailTxt.isEmpty()) {
                    Toast.makeText(RegistrationActivity.this, "All Fields Required!!!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            progressDialog.dismiss();

                            if (snapshot.child("users").hasChild(phoneTxt)) {
                                Toast.makeText(RegistrationActivity.this, "Номер телефона уже зарегистрирован ", Toast.LENGTH_SHORT).show();
                            } else {
                                databaseReference.child("users").child(phoneTxt).child("email").setValue(emailTxt);
                                databaseReference.child("users").child(phoneTxt).child("name").setValue(nameTxt);
                                databaseReference.child("users").child(phoneTxt).child("profile_img").setValue("");

                                //сохраняем номер телефона в память
                                MemoryData.saveData(phoneTxt, RegistrationActivity.this);

                                // Сохраняем имя в память
                                MemoryData.saveName(nameTxt, RegistrationActivity.this);
                                Toast.makeText(RegistrationActivity.this, "Выполнено успешно", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                                intent.putExtra("phone", phoneTxt);
                                intent.putExtra("name", nameTxt);
                                intent.putExtra("email", emailTxt);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });
    }
}
