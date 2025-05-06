package com.baristuzemen.bullrentexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserProfile extends AppCompatActivity {

    EditText fullName, email, phoneNo, password;
    TextView fullNameLabel, usernameLabel;

    String _USERNAME, _NAME, _EMAIL, _PHONENO, _PASSWORD;

    DatabaseReference reference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_profile);


        reference = FirebaseDatabase.getInstance().getReference("users");



        fullName = findViewById(R.id.full_name_profile);
        email = findViewById(R.id.email_profile);
        phoneNo = findViewById(R.id.phone_no_profile);
        password = findViewById(R.id.password_profile);



        fullNameLabel = findViewById(R.id.fullname_field);
        usernameLabel = findViewById(R.id.username_field);






        showAllUserData();



    }


    private void showAllUserData() {

        Intent intent = getIntent();


        _USERNAME = intent.getStringExtra("username");
        _NAME = intent.getStringExtra("name");
        _EMAIL = intent.getStringExtra("email");
        _PHONENO = intent.getStringExtra("phoneNo");
        _PASSWORD = intent.getStringExtra("password");



        fullNameLabel.setText(_NAME);
        usernameLabel.setText(_USERNAME);
        fullName.setText(_NAME);
        email.setText(_EMAIL);
        phoneNo.setText(_PHONENO);
        password.setText(_PASSWORD);


    }





    public void update(View view) {
        if (isNameChanged() || isPasswordChanged()){
            Toast.makeText(this, "Data has been Updated!", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(this, "Data is same and can not be Updated!", Toast.LENGTH_SHORT).show();
    }

    private boolean isPasswordChanged(){
        if(!_PASSWORD.equals(password.getText().toString()))
        {
            reference.child(_USERNAME).child("password").setValue(password.getText().toString());
            _PASSWORD=password.getText().toString();
            return true;
        }else{
            return false;
        }
    }
    private boolean isNameChanged(){
        if(!_NAME.equals(fullName.getText().toString())){
            reference.child(_USERNAME).child("name").setValue(fullName.getText().toString());
            _NAME=fullName.getText().toString();
            return true;
        }else{
            return false;
        }
    }


}