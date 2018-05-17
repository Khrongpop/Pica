package com.myproject.myproject

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import com.google.firebase.auth.FirebaseAuth
import android.content.DialogInterface
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream

import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class PostActivity : AppCompatActivity(), View.OnClickListener {

    private var bt_news: Button? = null
    private var bt_post: Button? = null
    private var bt_profile: Button? = null

    private var etDetail: EditText? = null
    private var btnPost: Button? = null
    private var imgbtn: ImageButton? = null

    private var mAuth: FirebaseAuth? = null
    private var filepath: StorageReference? = null
    private var mStorage: StorageReference? = null
    private var imgData : Uri? = null
    private var imageBitmap : Bitmap? = null

    private var mBulider : AlertDialog.Builder? = null
    private var mProgress : ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        bt_news = findViewById(R.id.news)
        bt_post = findViewById(R.id.came)
        bt_profile = findViewById(R.id.profiles)
        imgbtn = findViewById(R.id.imgbtn)
        btnPost = findViewById(R.id.postbtn)
        etDetail = findViewById(R.id.etDetail)
        mProgress = ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()
        mStorage = FirebaseStorage.getInstance().reference

        bt_news?.setOnClickListener(this)
        bt_post?.setOnClickListener(this)
        bt_profile?.setOnClickListener(this)
        btnPost?.setOnClickListener(this)
        imgbtn?.setOnClickListener(this)
//        hideSoftKeyboard(this)
        etDetail?.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.news ->  startActivity(Intent(this, Home::class.java))
            R.id.came -> startActivity(Intent(this, PostActivity::class.java))
            R.id.profiles -> startActivity(Intent(this, ProfileActivity::class.java))
            R.id.imgbtn -> imgPick()
            R.id.postbtn -> uploadIMG()
        }
    }


    private fun galleryIntent() {
        val pickPhoto = Intent(Intent.ACTION_PICK , android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI  )
        startActivityForResult(pickPhoto, 1)//one can be replaced with any action code
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent)
        when (requestCode) {
            0 -> if (resultCode == Activity.RESULT_OK) {
                val selectedImage = imageReturnedIntent.data


                val extras = imageReturnedIntent.extras
                imageBitmap = extras.get("data") as Bitmap

//                imgData = extras.get("data")
//                imageBitmap =  MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                imgbtn?.setImageBitmap(imageBitmap)


            }
            1 -> if (resultCode == Activity.RESULT_OK) {
                val selectedImage = imageReturnedIntent.data
                imgData = selectedImage
//                imageBitmap =  MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
//                imgbtn?.setImageBitmap(imageBitmap)
                imgbtn?.setImageURI(selectedImage)

            }

        }
    }

    private fun imgPick() {

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


    private fun cameraIntent() {
//        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        startActivityForResult(takePicture, 0)

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, 0)
        }

    }



    private fun post(imgURL: String ,id: String) {
        val detail = etDetail?.text.toString()
        mProgress?.dismiss()

        if ( imageBitmap != null) {

            val Ref = FirebaseDatabase.getInstance().reference
            val value = HashMap<String, Any>().apply {
                put("detail",detail)
                put("id",id)
                put("imgURL",imgURL)
            }
            val key = Ref.push().key
            Ref.child("posts").child(key).setValue(value)
            Toast.makeText(this, "Post success.", Toast.LENGTH_SHORT).show()
            etDetail?.text = null
             imgbtn?.setImageDrawable(getDrawable(R.drawable.ic_add))

        } else {
            Toast.makeText(this, "Post faild.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadIMG()  {

        var user = mAuth?.currentUser?.uid
        if (user != null) {

            mProgress?.setMessage("Post")
            mProgress?.show()
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            filepath = mStorage?.child("photo")?.child(user)?.child("images/pic_"+timeStamp.toString())
            val  baos = ByteArrayOutputStream()
            imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 30, baos)

            val  data = baos.toByteArray()
//                    byte[] data = baos.toByteArray()

                    //uploading the image
//                    val uploadTask2 = childRef2.putBytes(data);
            data.let { it1 ->
                filepath?.putBytes(it1)?.addOnFailureListener(OnFailureListener {
                    // Handle unsuccessful uploads
                })?.addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    val downloadUrl = taskSnapshot.downloadUrl.toString()
                    post(downloadUrl,user)

                })
            }


        }
    }

    fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_profile,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        signOut()
        return true
    }
    private  fun signOut() {

        mAuth?.signOut()
        val MainAct = Intent( this , MainActivity::class.java)
        startActivity(MainAct)
    }





}
