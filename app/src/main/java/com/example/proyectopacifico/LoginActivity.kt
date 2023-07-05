package com.example.proyectopacifico

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.proyectopacifico.core.Constants
import com.example.proyectopacifico.data.localdb.AppDatabase
import com.example.proyectopacifico.databinding.ActivityLoginBinding
import com.example.proyectopacifico.domain.user.UserRepoImpl
import com.example.proyectopacifico.presentation.UserViewModel
import com.example.proyectopacifico.presentation.UserViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.example.proyectopacifico.core.Result
import com.example.proyectopacifico.data.models.entities.UserEntity


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModelUsers by viewModels<UserViewModel> {
        UserViewModelFactory(
            UserRepoImpl(
                AppDatabase.getDatabase(applicationContext).UserDao()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.supportActionBar?.hide()


        clicks()


    }

    private fun clicks() {
        binding.btnLogin.setOnClickListener { validateData() }

    }

    private fun validateData() {
        val results = arrayOf(validateNickName(), validatePassword())
        if (false in results) {
            return
        }
        sendUser()
    }

    private fun validateNickName(): Boolean {
        return if (binding.edtNickName.text.toString().isNullOrEmpty()) {
            binding.idLayoutLoginName.error = "Este campo es obligatorio"
            false
        } else {
            binding.idLayoutLoginName.error = null
            true
        }
    }

    private fun validatePassword(): Boolean {
        return if (binding.edtPassword.text.toString().isNullOrEmpty()) {
            binding.txtILPassword.error = "Este campo es obligatorio"
            false
        } else {
            binding.txtILPassword.error = null
            true
        }
    }

    private fun sendUser() {
        lifecycleScope.launchWhenStarted {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelUsers.getOneUser(binding.edtNickName.text.toString()).collect {
                    when (it) {
                        is Result.Loading -> {}
                        is Result.Success -> {
                            if (it.data != null) {
                                if (it.data.password == binding.edtPassword.text.toString()) {
                                    saveUserData(it.data)
                                    val i = Intent(this@LoginActivity, MainActivity::class.java)
                                    startActivity(i)
                                    finish()
                                } else {
                                    Snackbar.make(
                                        binding.root,
                                        "La contraseña no coincide",
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }
                            }else{
                                Snackbar.make(
                                    binding.root,
                                    "No existe ese usuario",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }

                        }
                        is Result.Failure -> {
                            Log.e("sendUser: ", it.exception.message.toString())
                            Snackbar.make(
                                binding.root,
                                "Error al comunicarse con la bd",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun saveUserData(usersEntity: UserEntity) {
        val shared = getSharedPreferences(Constants.SHARED_USER, Context.MODE_PRIVATE)
        shared.edit().apply {
            putInt("idUser", usersEntity.id_user)
            putInt("profileId", usersEntity.profile_id)
            putString("userNick", usersEntity.user_nick)
            putString("userNames", usersEntity.usernames)
            putString("userLastNames", usersEntity.user_last_names)
            putString("phone", usersEntity.phone_number)
            putString("email", usersEntity.email)
            putString("identification", usersEntity.identification)
        }.apply()
    }

}