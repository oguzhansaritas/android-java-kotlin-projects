package com.baristuzemen.firebasetictactoe

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_choose_player.*

import java.util.ArrayList

class ChoosePlayerActivity : AppCompatActivity() {

    internal var currentOpponent: User? = null
    internal var loggedInUser: User? = null

    private var mFirebaseDatabase: DatabaseReference? = null
    private var mFirebaseInstance: FirebaseDatabase? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_player)

        val arrayOfUsers = ArrayList<User>()
        val adapter = UserAdapter(this, arrayOfUsers)

        val listView = findViewById<View>(R.id.myListView) as ListView
        listView.adapter = adapter

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            currentOpponent = listView.adapter.getItem(position) as User
            etInviteEmal.setText(currentOpponent!!.email)
        }

        mFirebaseInstance = FirebaseDatabase.getInstance()

        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance!!.getReference("users")

        val currentUserLoggedIn = FirebaseAuth.getInstance().currentUser

        val allUsers = mFirebaseDatabase!!.orderByChild("name")

        allUsers.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val user = dataSnapshot.getValue(User::class.java)

                Log.v(TAG, "User data:  " + user!!.myID + ", " + user.name + ", " + user.email)

                // get the current user from the database
                if (currentUserLoggedIn!!.email == user.email) {
                    loggedInUser = user

                    // go  to the game screen if a game is in progress
                    if (user.currentlyPlaying) {
                        startActivity(Intent(this@ChoosePlayerActivity, GameActivity::class.java))
                        finish()
                    }
                } else if (!user.currentlyPlaying && user.opponentID.isEmpty()) {
                    adapter.add(user)
                }// if the other user is not currently playing and they do not have a
                // current request, then they are a valid opponent to choose
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)

                // get the current user from the database
                if (currentUserLoggedIn!!.email != user!!.email)
                    adapter.remove(user)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException())
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.key!!)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.key!!)
                val user = dataSnapshot.getValue(User::class.java)
                val userID = dataSnapshot.key

                // if the other user is not currently playing and they do not have a
                // current request, then they are a valid opponent to choose
                // get the current user from the database
                if (currentUserLoggedIn!!.email != user!!.email) {
                    if (user.currentlyPlaying || !user.opponentID.isEmpty()) {
                        adapter.remove(user)
                    }
                } else {

                    // update your object
                    loggedInUser = user

                    if (user.request == true) {
                        showAcceptOrDenyInviteDialog()
                        user.request = false
                        mFirebaseDatabase!!.child(userID!!).setValue(user)
                    } else if (user.accepted == "true") {
                        // set values back to initial state and show button
                        progressBar!!.visibility = View.GONE
                        buInvite.isEnabled = true

                        mFirebaseDatabase!!.child(loggedInUser!!.myID).child("accepted").setValue("none")

                        // show dialog and go to game screen
                        showAcceptOrDenyStatusDialog(true)
                    } else if (user.accepted == "false") {

                        // set values back to initial state and show button
                        progressBar!!.visibility = View.GONE
                        mFirebaseDatabase!!.child(loggedInUser!!.myID).child("opponentID").setValue("")
                        mFirebaseDatabase!!.child(loggedInUser!!.myID).child("opponentEmail").setValue("")
                        mFirebaseDatabase!!.child(loggedInUser!!.myID).child("accepted").setValue("none")

                        // show dialog
                        showAcceptOrDenyStatusDialog(false)
                        buInvite.isEnabled = true
                    }
                }
            }
        })
    }

    private fun showAcceptOrDenyStatusDialog(status: Boolean) {
        val alertDialog = AlertDialog.Builder(this)

        // Setting Dialog Title
        alertDialog.setTitle("Game Invite Status...")

        // Setting Dialog Message
        if (status)
            alertDialog.setMessage("Your game with " + loggedInUser!!.opponentEmail + " has been accepted")
        else
            alertDialog.setMessage("Your game with " + loggedInUser!!.opponentEmail + " has been denied")


        // Setting Positive "Yes" Btn
        alertDialog.setPositiveButton("OK"
        ) { _, _ ->
            // navigate to game screen
            if (status) {
                startActivity(Intent(this@ChoosePlayerActivity, GameActivity::class.java))
            }
        }

        // Showing Alert Dialog
        alertDialog.show()
    }

    private fun showAcceptOrDenyInviteDialog() {
        val alertDialog = AlertDialog.Builder(this)

        // Setting Dialog Title
        alertDialog.setTitle("Accept Game Invite...")

        // Setting Dialog Message
        alertDialog.setMessage("Would you like to play tic tac toe against " + loggedInUser!!.opponentEmail + "?")

        // Setting Positive "Yes" Btn
        alertDialog.setPositiveButton("YES"
        ) { _, _ ->
            // create game and go there
            val game = Game(loggedInUser!!.opponentID)

            mFirebaseDatabase!!.child(loggedInUser!!.opponentID).child("myGame").setValue(game)
            mFirebaseDatabase!!.child(loggedInUser!!.myID).child("myGame").setValue(game)

            // set game status for both players (currently playing)
            mFirebaseDatabase!!.child(loggedInUser!!.opponentID).child("currentlyPlaying").setValue(true)
            mFirebaseDatabase!!.child(loggedInUser!!.myID).child("currentlyPlaying").setValue(true)

            mFirebaseDatabase!!.child(loggedInUser!!.opponentID).child("accepted").setValue("true")

            // navigate to game screen
            startActivity(Intent(this@ChoosePlayerActivity, GameActivity::class.java))
        }

        // Setting Negative "NO" Btn
        alertDialog.setNegativeButton("NO"
        ) { dialog, _ ->
            mFirebaseDatabase!!.child(loggedInUser!!.myID).child("opponentID").setValue("")
            mFirebaseDatabase!!.child(loggedInUser!!.myID).child("opponentEmail").setValue("")
            mFirebaseDatabase!!.child(loggedInUser!!.opponentID).child("accepted").setValue("false")
            dialog.cancel()
        }

        // Showing Alert Dialog
        alertDialog.show()
    }

    fun onClickInvite(view: View) {
        if (currentOpponent != null) {
            // set opponent id for selected user to invite and let them know they have an invite in database
            mFirebaseDatabase!!.child(currentOpponent!!.myID).child("opponentID").setValue(loggedInUser!!.myID)
            mFirebaseDatabase!!.child(currentOpponent!!.myID).child("opponentEmail").setValue(loggedInUser!!.email)
            mFirebaseDatabase!!.child(currentOpponent!!.myID).child("request").setValue(true)

            // set opponent id for current logged in user in database
            mFirebaseDatabase!!.child(loggedInUser!!.myID).child("opponentID").setValue(currentOpponent!!.myID)
            mFirebaseDatabase!!.child(loggedInUser!!.myID).child("opponentEmail").setValue(currentOpponent!!.email)
            mFirebaseDatabase!!.child(loggedInUser!!.myID).child("accepted").setValue("pending")

            progressBar!!.visibility = View.VISIBLE
            buInvite.isEnabled = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        when (item.itemId) {
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()

                startActivity(Intent(this@ChoosePlayerActivity, RegisterActivity::class.java))
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    companion object {

        private val TAG = ChoosePlayerActivity::class.java.simpleName
    }
}
