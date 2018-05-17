package com.myproject.myproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

/**
 * Created by khrongpop on 16/4/2018 AD.
 */

class RecipeAdapter(private val context: Context,
                    private val dataSource: ArrayList<Recipe>) : BaseAdapter() {

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private class ViewHolder {

        lateinit var titleTextView: TextView
        lateinit var detailTextView: TextView
        lateinit var thumbnailImageView: CircleImageView
        lateinit var ItemImageView: ImageView
    }




    //1
    override fun getCount(): Int {
        return dataSource.size
    }

    //2
    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    //3
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    //4
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get view for row item
//        val rowView = inflater.inflate(R.layout.list_item_recipe, parent, false)
        // Get title element


        val view: View
        val holder: ViewHolder

        // 1
        if (convertView == null) {

            // 2
            view = inflater.inflate(R.layout.list_item_recipe, parent, false)

            // 3
            holder = ViewHolder()
            holder.thumbnailImageView = view.findViewById(R.id.recipe_list_thum)
            holder.titleTextView = view.findViewById(R.id.recipe_list_name)
            holder.ItemImageView = view.findViewById(R.id.recipe_list_img)
            holder.detailTextView = view.findViewById(R.id.recipe_list_title)

            // 4
            view.tag = holder
        } else {
            // 5
            view = convertView
            holder = convertView.tag as ViewHolder
        }

        // 6
        val titleTextView = holder.titleTextView
        val ItemImageView = holder.ItemImageView
        val detailTextView = holder.detailTextView
        val thumbnailImageView = holder.thumbnailImageView

        val recipe = getItem(position) as Recipe


        detailTextView.text = recipe.detail
        Picasso.get().load(recipe.imgURL).into(ItemImageView)


        val Ref = FirebaseDatabase.getInstance().reference.child("users").child(recipe.id)

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val value = dataSnapshot.value as Map<String, Any>
                val name = value.getValue("name")
                val urlStr = value.getValue("imgURL")
                val email = value.getValue("email")

                Picasso.get().load(urlStr.toString()).into(thumbnailImageView)
                titleTextView.text = name.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        Ref.addValueEventListener(postListener)



        return view


    }


}


