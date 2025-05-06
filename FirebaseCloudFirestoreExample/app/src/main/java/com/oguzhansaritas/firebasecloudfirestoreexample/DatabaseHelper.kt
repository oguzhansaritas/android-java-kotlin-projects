package com.baristuzemen.firebasecloudfirestoreexample


import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class DatabaseHelper {

    private lateinit var database: FirebaseFirestore

    fun allEmployees(adapter: UserAdapter?) {

        val docRef = database.collection("users")

        docRef.addSnapshotListener { snapshots, e ->

            if (e != null) {
                Log.w("DatabaseHelper", "Listen failed.", e)
                return@addSnapshotListener
            }

            adapter?.clear()
            var counter: Int = 1;

            for (myUser in snapshots!!) {
                val user = User(myUser.id, myUser.getString("name"), myUser?.getString("address"), Integer.toString(counter++))
                adapter?.add(user)
            }
        }
    }

    fun open() {
        database = FirebaseFirestore.getInstance()
    }

    fun add(name: String, address: String) {
        val data = hashMapOf(
            "name" to name,
            "address" to address
        )

        // create the document with the user id above as the document name
        database.collection("users").add(data)
    }

    fun update(_id: String, name: String, address: String) {
        val myRef = database.collection("users").document(_id)
        myRef.update("name", name)
        myRef.update("address", address)
    }

    fun delete(_id: String) {
        database.collection("users").document(_id).delete()
    }
}
