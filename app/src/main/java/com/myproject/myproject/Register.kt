package com.myproject.myproject

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.media.Image
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.InputMethodManager
//import android.view.inputmethod.InputMethodManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlin.collections.HashMap
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.android.gms.tasks.OnSuccessListener

import android.widget.*
import com.google.android.gms.tasks.OnFailureListener
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


class Register : AppCompatActivity() {
    private var etName: EditText? = null
    private var etEmail: EditText? = null
    private var etPass: EditText? = null
    private var btnLog: Button? = null
    private var imgbtn: ImageButton? = null
    private var mAuth: FirebaseAuth? = null

    private var mDatabase: DatabaseReference? = null
    private var mStorage: StorageReference? = null
    private var filepath: StorageReference? = null

    private var imgData : Uri? = null
    private var imageBitmap : Bitmap? = null
    private var mProgress : ProgressDialog? = null

    private var mBulider : AlertDialog.Builder? = null
    private var back: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
//        hideSoftKeyboard(this)
        etName = findViewById(R.id.et_name)
        etEmail = findViewById(R.id.et_email)
        etPass = findViewById(R.id.et_pass)
        btnLog = findViewById(R.id.bt_log)
        imgbtn = findViewById(R.id.imgBt)
        back = findViewById(R.id.back_log)
        mAuth = FirebaseAuth.getInstance()
        mStorage = FirebaseStorage.getInstance().reference

        mProgress = ProgressDialog(this)


        imgbtn?.setOnClickListener {
            mBulider = AlertDialog.Builder(this)
            mBulider?.setTitle("Add Photo!")
            val items = arrayOf<CharSequence>("Take Photo", "Choose from Library", "Cancel")

            mBulider?.setItems(items, DialogInterface.OnClickListener { dialog, item ->

                when {
                    items[item] == "Take Photo" -> cameraIntent()
                    items[item] == "Choose from Library" -> galleryIntent()
                    items[item] == "Cancel" -> dialog.dismiss()
                }
            })
            mBulider?.show()

        }
        back?.setOnClickListener {
            val loginMain = Intent(this, MainActivity::class.java)
            startActivity(loginMain)
        }

        btnLog?.setOnClickListener {
            var email: String
            var password: String
            var name: String
            email = etEmail?.text.toString()
            password = etPass?.text.toString()
            name = etName?.text.toString()

            mProgress?.setMessage("Sign Up")
            mProgress?.show()

            if( email != "" && password != "") {
                mAuth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
//                                Log.d(FragmentActivity.TAG, "createUserWithEmail:success")


                            var user = mAuth?.currentUser?.uid
                            if (user != null) {

                                logIn(email,password)

                                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                                filepath = mStorage?.child("photo")?.child(user)?.child("images/pic_"+timeStamp.toString())
                                val  baos = ByteArrayOutputStream()

                                imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 30, baos)

                                val  data = baos.toByteArray()
                                data?.let { it1 ->
                                    filepath?.putBytes(it1)?.addOnFailureListener(OnFailureListener {
                                        // Handle unsuccessful uploads
                                    })?.addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                        val downloadUrl = taskSnapshot.downloadUrl.toString()
                                        createNewUser(user, name, email ,downloadUrl )
                                        mProgress?.dismiss()
                                        Toast.makeText(this, "Success.",
                                                Toast.LENGTH_SHORT).show()

                                    })
                                }


                            }

                    } else {
                        mProgress?.dismiss()
                        Toast.makeText(this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                    }

                    // ...
                }
            }


        }

        etName?.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        })

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



    private fun createNewUser(userId: String, name: String, email: String , imgURL: String) {

        val Ref = FirebaseDatabase.getInstance().reference
        val value = HashMap<String, Any>().apply {
            put("name",name)
            put("email",email)
            put("imgURL",imgURL)
        }

        Ref.child("users").child(userId).setValue(value)


    }

    private fun logIn(email: String , password:  String) {
        mAuth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
//                            Log.d(FragmentActivity.AG, "signInWithEmail:success")
//                            val user = mAuth.getCurrentUser()
//                            updateUI(user)
                val HomeMain = Intent(this, Home::class.java)
                startActivity(HomeMain)

            } else {
                // If sign in fails, display a message to the user.
//                            Log.w(FragmentActivity.TAG, "signInWithEmail:failure", task.exception)
//                            Toast.makeText(this@EmailPasswordActivity, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show()
//                            updateUI(null)
            }

            // ...
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent)
        when (requestCode) {
            0 -> if (resultCode == Activity.RESULT_OK) {

                val selectedImage = imageReturnedIntent.data
                imgbtn?.setImageURI(selectedImage)
                imgData = selectedImage

//                imgbtn?.setImageURI(selectedImage)
//                imgData = selectedImage
                val extras = imageReturnedIntent.extras
                imageBitmap = extras.get("data") as Bitmap

                imgbtn?.setImageBitmap(imageBitmap)

//                imageBitmap =  MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
//                imgbtn?.setImageBitmap(imageBitmap)
            }
            1 -> if (resultCode == Activity.RESULT_OK) {
                val selectedImage = imageReturnedIntent.data
                imgbtn?.setImageURI(selectedImage)
                imgData = selectedImage
//                imageBitmap =  MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
//                imgbtn?.setImageBitmap(imageBitmap)
            }
        }
    }

    private fun cameraIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, 0)
        }
    }

    private fun galleryIntent() {
        val pickPhoto = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPhoto, 1)//one can be replaced with any action code
    }

   fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }


}

