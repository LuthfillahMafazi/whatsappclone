package com.zii.whatsappclone.main

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.zii.whatsappclone.R
import com.zii.whatsappclone.model.User
import com.zii.whatsappclone.utils.DATA_IMAGES
import com.zii.whatsappclone.utils.DATA_USERS
import com.zii.whatsappclone.utils.REQUEST_CODE_PHOTO
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.activity_signup.progress_layout

class SignupActivity : AppCompatActivity() {

    private val firebaseDb = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseAuthListener = FirebaseAuth.AuthStateListener {
        val user = firebaseAuth.currentUser?.uid
        if (user != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        setTextChangedListener(edt_email, til_email)
        setTextChangedListener(edt_password, til_password)
        setTextChangedListener(edt_name, til_name)
        setTextChangedListener(edt_phone, til_phone)
        progress_layout.setOnTouchListener { v, event -> true }

        btn_signup.setOnClickListener {
            onSignup()
        }

        txt_login.setOnClickListener {
            onLogin()
        }
    }

    private fun setTextChangedListener(edt: EditText, til: TextInputLayout) {
        edt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                til.isErrorEnabled = false
            }
        })
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(firebaseAuthListener)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(firebaseAuthListener)
    }

    fun onSignup() {
        var proceed = true
        if (edt_name.text.isNullOrEmpty()) {
            til_name.error = "Required Name"
            til_name.isErrorEnabled = true
            proceed = false
        }
        if (edt_phone.text.isNullOrEmpty()) {
            til_phone.error = "Required Phone Number"
            til_phone.isErrorEnabled = true
            proceed = false
        }
        if (edt_email.text.isNullOrEmpty()) {
            til_email.error = "Required Password"
            til_email.isErrorEnabled = true
            proceed = false
        }
        if (edt_password.text.isNullOrEmpty()) {
            til_password.error = "Required Password"
            til_password.isErrorEnabled = true
            proceed = false
        }

        if (proceed) {
            progress_layout.visibility = View.VISIBLE
            firebaseAuth.createUserWithEmailAndPassword(
                edt_email.text.toString(),
                edt_password.text.toString()
            )
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        progress_layout.visibility = View.GONE
                        Toast.makeText(
                            this,
                            "SignUp error: ${task.exception?.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (firebaseAuth.uid != null) {
                        val email = edt_email.text.toString()
                        val phone = edt_phone.text.toString()
                        val name = edt_name.text.toString()
                        val pass = edt_password.text.toString()
                        val user = User(
                            email,
                            phone,
                            name,
                            pass,
                            "Hello i'm new",
                            "",
                            ""
                        )
                        firebaseDb.collection(DATA_USERS)
                            .document(firebaseAuth.uid!!).set(user)
                    }
                    progress_layout.visibility = View.GONE
                }

                .addOnFailureListener {
                    progress_layout.visibility = View.GONE
                    it.printStackTrace()
                }
        }
    }

    private fun onLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

}
