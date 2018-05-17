package com.myproject.myproject

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import android.view.View
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener



class Home : AppCompatActivity() , View.OnClickListener{



    var bt_news: Button? = null
    var bt_post: Button? = null
    var bt_profile: Button? = null


    var mAuth: FirebaseAuth? = null

    private lateinit var listView: ListView
//    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


//        textView = findViewById(R.id.textView)
        bt_news = findViewById(R.id.news)
        bt_post = findViewById(R.id.came)
        bt_profile = findViewById(R.id.profiles)

        mAuth = FirebaseAuth.getInstance()


        bt_news?.setOnClickListener(this)
        bt_post?.setOnClickListener(this)
        bt_profile?.setOnClickListener(this)



        val currentUser = mAuth?.currentUser

        listView = findViewById(R.id.recipe_list_view)


        val Ref = FirebaseDatabase.getInstance().reference.child("posts")

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

//
//                for (data in dataSnapshot.children) {
//
//                    val child = data.value
//                    Log.d("string",child.toString())
//                }

                val recipeList = Recipe.getPost(dataSnapshot)

//                textView.text =recipeList.toString()

                val adapter = RecipeAdapter(this@Home, recipeList)
                listView.adapter = adapter

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }

        Ref.addValueEventListener(postListener)



//        val context = this
//        listView.setOnItemClickListener { _, _, position, _ ->
//            val selectedRecipe = recipeList[position]
//
//            val detailIntent = RecipeDetailActivity.newIntent(context, selectedRecipe)
//
//            startActivity(detailIntent)
//        }


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

    override fun onClick(view: View?) {

        when (view?.id) {
            R.id.news ->  startActivity(Intent(this, Home::class.java))
            R.id.came -> startActivity(Intent(this, PostActivity::class.java))
            R.id.profiles -> startActivity(Intent(this, ProfileActivity::class.java))

        }
    }





}
