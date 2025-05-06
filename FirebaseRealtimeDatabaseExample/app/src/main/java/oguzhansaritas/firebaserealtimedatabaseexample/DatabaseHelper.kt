package baristuzemen.firebaserealtimedatabaseexample

import android.util.Log
import com.google.firebase.database.*


class DatabaseHelper {


    private lateinit var database: DatabaseReference

    fun allEmployees(adapter: UserAdapter?) {
        var myReference: DatabaseReference = database.child("users")

        myReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                adapter?.clear()

                var counter : Int = 1;

                for (myUser in dataSnapshot.children) {
                    val user = myUser.getValue(User::class.java)

                    // Check for null
                    if (user == null) {
                        Log.e("DatabaseHelper", "User data is null!")
                        return
                    }

                    user._id = myUser.key!!
                    user.counter = Integer.toString(counter++)
                    adapter?.add(user)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.e("DatabaseHelper", "Failed to read user", error.toException())
            }
        })
    }

    fun open() {
        database = FirebaseDatabase.getInstance().reference
    }

    fun add(name: String, address: String) {
        val key = database.child("users").push().key
        database.child("users").child(key!!).child("name").setValue(name)
        database.child("users").child(key!!).child("address").setValue(address)
    }

    fun update(_id: String, name: String, address: String) {
        database.child("users").child(_id).child("name").setValue(name)
        database.child("users").child(_id).child("address").setValue(address)
    }

    fun delete(_id: String) {
        database.child("users").child(_id).removeValue()
    }
}