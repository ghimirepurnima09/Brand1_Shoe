package com.example.brand_shoe.repo

import com.example.brand_shoe.Model.UserModel

interface UserRepo {
    fun login(email: String, password: String, callback: (Boolean, String) -> Unit)

    fun register(email: String, password: String, callback: (Boolean, String, String) -> Unit)

    fun editProfile(id: String, model: UserModel, callback: (Boolean, String) -> Unit)

    fun addUser(id: String, model: UserModel, callback: (Boolean, String) -> Unit)

    fun deleteUsr(id: String, callback: (Boolean, String) -> Unit)

    fun forgetPassword(email: String, callback: (Boolean, String) -> Unit)

    fun getUserId(id: String, callback: (Boolean, String, UserModel?) -> Unit)

    fun getAllUser(callback: (Boolean, String, List<UserModel?>) -> Unit)

    fun logout(callback: (Boolean, String) -> Unit)
}