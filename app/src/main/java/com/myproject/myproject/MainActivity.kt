package com.myproject.myproject

import android.app.Activity
import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.content.Intent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
//import android.app.Activity
//import android.view.inputmethod.InputMethodManager


class MainActivity : AppCompatActivity() {


    var etEmail: EditText? = null
    var etPass: EditText? = null
    var btnLog: Button? = null
    var btnRes: Button? = null

    var tv: TextView? = null

    var mAuth: FirebaseAuth? = null

    var mProgress: ProgressDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        hideSoftKeyboard(this)



        etEmail = findViewById(R.id.et_email)
        etPass = findViewById(R.id.et_pass)
        btnLog = findViewById(R.id.bt_log)
        btnRes = findViewById(R.id.bt_regis)
        mAuth = FirebaseAuth.getInstance()
        mProgress = ProgressDialog(this)

        btnLog?.setOnClickListener {
            var email: String
            var password: String
            email = etEmail?.text.toString()
            password = etPass?.text.toString()

            mProgress?.setMessage("Sign In")
            mProgress?.show()

            mAuth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(FragmentActivity.AG, "signInWithEmail:success")
//                            val user = mAuth.getCurrentUser()
//                            updateUI(user)
                            Toast.makeText(this, "Authentication success.",
                                    Toast.LENGTH_SHORT).show()
                            mProgress?.dismiss()
                            toHome()
                        } else {
                            // If sign in fails, display a message to the user.
//                            Log.w(FragmentActivity.TAG, "signInWithEmail:failure", task.exception)
                            Toast.makeText(this, "Authentication failed." + task.exception,
                                    Toast.LENGTH_SHORT).show()
//                            updateUI(null)
                            mProgress?.dismiss()
                        }

                        // ...
                    }

        }

        btnRes?.setOnClickListener{
            val regMain = Intent( this , Register::class.java)
            startActivity(regMain)
        }

        etEmail?.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        })

        etPass?.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        })



    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth?.getCurrentUser()
        if (currentUser != null) {
            toHome()
        }
    }

    private fun toHome() {
        val HomeMain = Intent(this, Home::class.java)
        startActivity(HomeMain)
    }

    fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}
