package com.myproject.myproject

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabItem
import android.support.v4.app.FragmentPagerAdapter
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.StorageReference
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener

import android.widget.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


//import android.support.test.espresso.web.model.Atoms.getTitle
//import sun.text.normalizer.UTF16.append




class ProfileActivity : AppCompatActivity() , View.OnClickListener  {


    var bt_news: Button? = null
    var bt_post: Button? = null
    var bt_profile: Button? = null
    var bt_logout: Button? = null
    var tvName: TextView? = null
    var tvEmail: TextView? = null
    var GirdView: GridView? = null
    var img: CircleImageView? = null

    var mAuth: FirebaseAuth? = null
    var mDatabase: FirebaseDatabase? = null
    var mStorage: StorageReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        bt_news = findViewById(R.id.news)
        bt_post = findViewById(R.id.came)
        bt_profile = findViewById(R.id.profiles)
//        bt_logout = findViewById(R.id.bt_logout)
        tvName = findViewById(R.id.tv_name)
        tvEmail = findViewById(R.id.tv_email)
        GirdView = findViewById(R.id.Gridview)


//        GirdView?.setAdapter(new CustomAdapter(this);

        img = findViewById(R.id.imgProfile)
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()

        bt_news?.setOnClickListener(this)
        bt_post?.setOnClickListener(this)
        bt_profile?.setOnClickListener(this)
        bt_logout?.setOnClickListener(this)

        var user = mAuth?.currentUser?.uid

        val Ref2 = FirebaseDatabase.getInstance().reference.child("posts")
        var mPost : ArrayList<String>
        var numOfpost : Int
        val _this = this
        val postListener2 = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                mPost = getPost(user!!,dataSnapshot)
                numOfpost = mPost.count()
                GirdView?.adapter = CustomAdapter(_this, mPost,numOfpost)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }

        Ref2.addValueEventListener(postListener2)



        val Ref = FirebaseDatabase.getInstance().reference.child("users").child(user)



        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
//                val post = dataSnapshot.getValue<Post>(Post::class.java)
                // ...

                val value = dataSnapshot.value as Map<String, Any>
                val name = value.getValue("name")
                val urlStr = value.getValue("imgURL")
                val email = value.getValue("email")

                Picasso.get().load(urlStr.toString()).into(img)
                tvEmail?.text = email.toString()
                tvName?.text = name.toString()


            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
//                Log.w(FragmentActivity.TAG, "loadPost:onCancelled",onCancelled databaseError.toException())
                // ...
            }
        }

        Ref.addValueEventListener(postListener)


    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.news ->  startActivity(Intent(this, Home::class.java))
            R.id.came -> startActivity(Intent(this, PostActivity::class.java))
            R.id.profiles -> startActivity(Intent(this, ProfileActivity::class.java))
//            R.id.bt_logout -> signOut()
        }
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

    fun getPost(uid: String,dataSnapshot: DataSnapshot): ArrayList<String> {


        var recipeList = ArrayList<String>()


                for (data in dataSnapshot.children) {
                    val child = data.value

                    val value = child as Map<String, Any>

                    val urlStr = value.getValue("imgURL").toString()
                    val id = value.getValue("id").toString()
                    Log.d("string",id +  " = " + uid)
//                    Log.d("string",id)
                    if(id == uid) {
                        recipeList.add(urlStr)
                    }
                }



        return recipeList
    }


}


