package com.baristuzemen.firebasetictactoe

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

    }

    public override fun onStart() {
        super.onStart()

        // if user logged in, go to sign-in screen
        if (mAuth!!.currentUser != null) {
            startActivity(Intent(this, ChoosePlayerActivity::class.java))
            finish()
        }
    }


    override fun onResume() {
        super.onResume()
        progressBar.visibility = View.GONE
    }

    fun loginButtonClicked(view: View) {
        if (TextUtils.isEmpty(email.text.toString())) {
            Toast.makeText(applicationContext, "Enter email address!", Toast.LENGTH_SHORT).show()
            return
        }

        if (TextUtils.isEmpty(password.text.toString())) {
            Toast.makeText(applicationContext, "Enter password!", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.visibility = View.VISIBLE

        //authenticate user
        mAuth!!.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener(this@LoginActivity) { task ->
                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                progressBar.visibility = View.GONE
                if (!task.isSuccessful) {
                    // there was an error
                    if (password.text.toString().length < 6) {
                        password.error = getString(R.string.minimum_password)
                    } else {
                        Toast.makeText(this@LoginActivity, getString(R.string.auth_failed), Toast.LENGTH_LONG).show()
                    }
                } else {
                    val intent = Intent(this@LoginActivity, ChoosePlayerActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
    }
}

