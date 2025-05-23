package com.baristuzemen.bullrentexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {


    TextInputLayout regName, regUsername, regEmail, regPhoneNo, regPassword;
    Button regBtn, regToLoginBtn;

    FirebaseDatabase rootNode;
    DatabaseReference reference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        regName = findViewById(R.id.reg_name);
        regUsername = findViewById(R.id.reg_username);
        regEmail = findViewById(R.id.reg_email);
        regPhoneNo = findViewById(R.id.reg_phoneNo);
        regPassword = findViewById(R.id.reg_password);
        regBtn = findViewById(R.id.reg_btn);
        regToLoginBtn = findViewById(R.id.reg_login_btn);

    }


    private Boolean validateName(){

        String val = regName.getEditText().getText().toString();

        if (val.isEmpty()){
            regName.setError("Field cannot be empty");
            return false;
        }
        else {
            regName.setError(null);
            regName.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean validateUsername(){

        String val = regUsername.getEditText().getText().toString();
        String noWhiteSpace = "\\A\\w{4,20}\\z";
        if (val.isEmpty()){
            regUsername.setError("Field cannot be empty");
            return false;
        }
        else if (val.length()>=15){
            regUsername.setError("Username too long");
            return false;
        }
        else if (!val.matches(noWhiteSpace)){

            regUsername.setError("White Spaces are not allowed");
            return false;
        }
        else {
            regUsername.setError(null);
            regUsername.setErrorEnabled(false);
            return true;
        }


    }
    private Boolean validateEmail(){

        String val = regEmail.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (val.isEmpty()){
            regEmail.setError("Field cannot be empty");
            return false;
        }

        else if (!val.matches(emailPattern)){
            regEmail.setError("Invalid Email");
            return false;

        }
        else {
            regEmail.setError(null);
            return true;
        }



    }


    private Boolean validatePhoneNo(){

        String val = regPhoneNo.getEditText().getText().toString();

        if (val.isEmpty()){
            regPhoneNo.setError("Field cannot be empty");
            return false;
        }
        else {
            regPhoneNo.setError(null);
            return true;
        }



    }
    private Boolean validatePassword(){

        String val = regPassword.getEditText().getText().toString();
        String passwordVal = //"^" +
                "(?=.*[a-zA-Z])" +
                        //"(?=.*[@#$%^&+=])" +
                        //"(?=\\s+$)" +
                        ".{4,}" +
                        "$";

        if (val.isEmpty()){
            regPassword.setError("Field cannot be empty");
            return false;
        }
        else if (!val.matches(passwordVal)){
            regPassword.setError("Password is too weak");
            return false;
        }
        else {
            regPassword.setError(null);
            return true;
        }
    }


    public void registerUser(View view){

        if (!validateName() | !validateUsername() | !validateEmail() | !validatePhoneNo() | !validatePassword()){

            return;

        }


        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("users");

        //Get all the values
        String name = regName.getEditText().getText().toString();
        String username = regUsername.getEditText().getText().toString();
        String email = regEmail.getEditText().getText().toString();
        String phoneNo = regPhoneNo.getEditText().getText().toString();
        String password = regPassword.getEditText().getText().toString();


        /*Intent intent = new Intent(getApplicationContext(), VerifyPhoneNo.class);
        intent.putExtra("phoneNo", phoneNo);
        startActivity(intent);
        */




        UserHelperClass helperClass = new UserHelperClass(name, username, email, phoneNo, password);

        //reference.child(phoneNo).setValue(helperClass);

        reference.child(username).setValue(helperClass);

        Toast.makeText(this, "Your Account has been created succesfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();





    }
}