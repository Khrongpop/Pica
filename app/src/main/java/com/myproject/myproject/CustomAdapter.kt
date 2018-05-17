package com.myproject.myproject

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView.ScaleType
import android.widget.ImageView
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.myproject.myproject.R.id.imageView
import android.widget.LinearLayout




/**
 * Created by khrongpop on 24/4/2018 AD.
 */
class CustomAdapter(context: ProfileActivity, mPost: ArrayList<String>, numOfpost: Int) :  BaseAdapter() {



    private var mContext: Context? = context
    private var mPost: ArrayList<String> = mPost
    private var numOfitem: Int = numOfpost

//    fun setCustomAdapter(context: Context) {
//        this.mContext = context
//    }

    override fun getCount(): Int {
        return numOfitem
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val imageView: ImageView
        if (convertView == null) {
            imageView = ImageView(mContext)
//            GridView.layoutParams
//            imageView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).setMargins()
            imageView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 250)
//            imageView.layoutParams =
            imageView.scaleType = ScaleType.CENTER_CROP
            imageView.setPadding(4, 4, 4, 4)



        } else {
            imageView = convertView as ImageView
        }
        Picasso.get().load(mPost[position]).into(imageView)
//        imageView.setImageResource(R.drawable.ic_favorites)
        return imageView
    }



}