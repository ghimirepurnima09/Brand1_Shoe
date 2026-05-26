package com.example.brand_shoe.repo

import com.example.brand_shoe.Model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserRepoImpl : UserRepo {
    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance()
    val ref = database.getReference("users")

    override fun login(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Login Successfully")
                } else {
                    callback(false, it.exception?.message.toString())
                }
            }
    }

    override fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Register Successfully", "${auth.currentUser?.uid}")
                } else {
                    callback(false, "${it.exception?.message}", "")
                }
            }
    }

    // crud ko create
    override fun addUser(
        id: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(id).setValue(model).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "User added successfully")
            } else {
                callback(false, "${it.exception?.message}")
            }
        }
    }

    override fun deleteUsr(
        id: String,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(id).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "User deleted successfully")
            } else {
                callback(false, "${it.exception?.message}")
            }
        }
    }

    override fun editProfile(
        id: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(id).updateChildren(model.toMap()).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "User added successfully")
            } else {
                callback(false, "${it.exception?.message}")
            }
        }
    }

    override fun forgetPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Reset link sent to $email")
            } else {
                callback(false, "${it.exception?.message}")
            }
        }
    }

    override fun getUserId(
        id: String,
        callback: (Boolean, String, UserModel?) -> Unit
    ) {
        ref.child(id).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(UserModel::class.java)
                    if (user != null) {
                        callback(true, "user fetchd", user)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, "${error.message}", null)
            }
        })
    }

    override fun getAllUser(callback: (Boolean, String, List<UserModel?>) -> Unit) {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val allUsers = mutableListOf<UserModel?>()
                    for (user in snapshot.children) {
                        val data = user.getValue(UserModel::class.java)
                        if (data != null) {
                            allUsers.add(data)
                        }
                    }
                    callback(true, "Users fetched", allUsers)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                callback(false, p0.message, emptyList())
            }
        })
    }

    override fun logout(callback: (Boolean, String) -> Unit) {
        try {
            auth.signOut()
            callback(true, "Logout successful")
        } catch (e: Exception) {
            callback(false, e.toString())
        }
    }
}