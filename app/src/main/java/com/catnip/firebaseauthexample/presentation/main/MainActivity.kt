package com.catnip.firebaseauthexample.presentation.main

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isEmpty
import androidx.core.view.isVisible
import coil.load
import coil.transform.CircleCropTransformation
import com.catnip.firebaseauthexample.R
import com.catnip.firebaseauthexample.data.model.User
import com.catnip.firebaseauthexample.data.network.firebase.auth.FirebaseAuthDataSourceImpl
import com.catnip.firebaseauthexample.data.repository.UserRepositoryImpl
import com.catnip.firebaseauthexample.databinding.ActivityLoginBinding
import com.catnip.firebaseauthexample.databinding.ActivityMainBinding
import com.catnip.firebaseauthexample.presentation.login.LoginActivity
import com.catnip.firebaseauthexample.presentation.login.LoginViewModel
import com.catnip.firebaseauthexample.utils.GenericViewModelFactory
import com.catnip.firebaseauthexample.utils.proceedWhen
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val viewModel: MainViewModel by viewModels {
        GenericViewModelFactory.create(createViewModel())
    }

    //todo : create media picker result

    private fun changePhotoProfile(uri: Uri) {
        //todo : change photo profile here
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupForm()
        showUserData()
        setClickListeners()
        observeData()
    }

    private fun setClickListeners() {
        //todo : set click listener
        binding.tvChangePwd.setOnClickListener {
            requestChangePassword()
        }
        binding.tvLogout.setOnClickListener {
            doLogout()
        }
        binding.btnChangeProfile.setOnClickListener {
            changeProfileData()
        }
    }

    private fun requestChangePassword() {
        //todo : request change password to viewmodel and show dialog
        viewModel.createChangePwdRequest()
        AlertDialog.Builder(this)
            .setMessage(
                "Change password request sent to your email" +
                        "${viewModel.getCurrentUser()?.email}"
            )
            .setPositiveButton("Okay") { _, _ ->
                // do nothing
            }.create().show()
    }

    private fun doLogout() {
        //todo :  show dialog, if yes proceed logout
        AlertDialog.Builder(this)
            .setMessage(
                "Do you want to logout ?"
            )
            .setPositiveButton("Yes") { _, _ ->
                viewModel.doLogout()
                navigateToLogin()
            }.setNegativeButton("No") { _, _ ->

            }.create().show()

    }

    private fun navigateToLogin() {
        //todo :  navigate to login
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }

    private fun observeData() {
        //todo : observe result change photo and detail
        viewModel.changeProfileResult.observe(this) {
            it.proceedWhen(
                doOnSuccess = {
                    binding.pbLoading.isVisible = false
                    binding.btnChangeProfile.isVisible = true
                    binding.btnChangeProfile.isEnabled = true
                    showUserData()
                },
                doOnLoading = {
                    binding.pbLoading.isVisible = true
                    binding.btnChangeProfile.isVisible = false
                },
                doOnError = {
                    binding.pbLoading.isVisible = false
                    binding.btnChangeProfile.isVisible = true
                    binding.btnChangeProfile.isEnabled = true
                    Toast.makeText(
                        this,
                        "Change Profile Failed : ${it.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }

    }

    private fun changeProfileData() {
        //todo :  change fullname data
        if (checkNameValidation()) {
            val name = binding.layoutForm.etName.text.toString().trim()
            viewModel.changeProfile(name)
        }
    }

    private fun checkNameValidation(): Boolean {
        //todo :  check if name is valid
        return if (binding.layoutForm.etName.text?.isEmpty() == true) {
            binding.layoutForm.tilName.isErrorEnabled = true
            binding.layoutForm.tilName.error = getString(R.string.text_error_name_cannot_empty)
            false
        } else {
            binding.layoutForm.tilName.isErrorEnabled = false
            true
        }
    }

    private fun setupForm() {
        //todo : setup form that required in this page
        binding.layoutForm.tilName.isVisible = true
        binding.layoutForm.tilEmail.isVisible = true

    }

    private fun showUserData() {
        //todo : show user data to the views
        binding.layoutForm.etName.setText(viewModel.getCurrentUser()?.fullName)
        binding.layoutForm.etEmail.setText(viewModel.getCurrentUser()?.email)
        binding.layoutForm.tilEmail.isEnabled = false

    }

    private fun createViewModel(): MainViewModel {
        val firebaseAuth = FirebaseAuth.getInstance()
        val dataSource = FirebaseAuthDataSourceImpl(firebaseAuth)
        val repo = UserRepositoryImpl(dataSource)
        return MainViewModel(repo)
    }
}