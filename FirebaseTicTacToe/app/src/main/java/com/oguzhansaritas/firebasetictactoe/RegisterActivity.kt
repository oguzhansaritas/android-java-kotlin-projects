package com.baristuzemen.firebasetictactoe

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var mFirebaseDatabase: DatabaseReference? = null
    private var mFirebaseDatabaseInstance: FirebaseDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        //Get Firebase instances
        mAuth = FirebaseAuth.getInstance()
        mFirebaseDatabaseInstance = FirebaseDatabase.getInstance()

        // if already logged in go to choose player screen
        if (mAuth!!.currentUser != null) {
            startActivity(Intent(this, ChoosePlayerActivity::class.java))
            finish()
        }
    }

    fun onRegisterClicked(view: View) {

        if (TextUtils.isEmpty(email.text.toString())) {
            Toast.makeText(applicationContext, "Enter email address!", Toast.LENGTH_SHORT).show()
            return
        }

        if (TextUtils.isEmpty(password.text.toString())) {
            Toast.makeText(applicationContext, "Enter password!", Toast.LENGTH_SHORT).show()
            return
        }

        if (TextUtils.isEmpty(username.text.toString())) {
            Toast.makeText(applicationContext, "Enter username!", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.text.toString().length < 6) {
            Toast.makeText(applicationContext, "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar!!.visibility = View.VISIBLE
        //create user
        mAuth!!.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener(this@RegisterActivity) { task ->
                Toast.makeText(this@RegisterActivity, "createUserWithEmail:onComplete:" + task.isSuccessful, Toast.LENGTH_SHORT).show()
                progressBar!!.visibility = View.GONE
                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful) {
                    Toast.makeText(this@RegisterActivity, "Authentication failed." + task.exception!!,
                        Toast.LENGTH_SHORT).show()
                    Log.e("MYTag", task.exception!!.toString())
                } else {

                    // get reference to 'users' node
                    mFirebaseDatabase = mFirebaseDatabaseInstance!!.getReference("users")

                    val user = FirebaseAuth.getInstance().currentUser
                    val myUser = User(username.text.toString(), user?.email!!, user.uid)

                    mFirebaseDatabase!!.child(user.uid).setValue(myUser)

                    startActivity(Intent(this@RegisterActivity, ChoosePlayerActivity::class.java))
                    finish()
                }
            }
    }

    fun onLoginClicked(view: View) {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}
