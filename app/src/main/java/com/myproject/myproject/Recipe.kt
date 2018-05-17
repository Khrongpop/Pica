package com.myproject.myproject

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by khrongpop on 15/4/2018 AD.
 */


class Recipe(
        val detail: String,
        val id: String,
        val imgURL: String)
//        val instructionUrl: String,
//        val label: String)
{

    companion object {

        fun getRecipesFromFile(filename: String, context: Context): ArrayList<Recipe> {
            val recipeList = ArrayList<Recipe>()

            try {
                // Load data
                val jsonString = loadJsonFromAsset("recipes.json", context)
                val json = JSONObject(jsonString)
                val recipes = json.getJSONArray("recipes")

                // Get Recipe objects from data
//                (0 until recipes.length()).mapTo(recipeList) {
//                    Recipe(recipes.getJSONObject(it).getString("title"),
//                            recipes.getJSONObject(it).getString("description"),
//                            recipes.getJSONObject(it).getString("image"),
//                            recipes.getJSONObject(it).getString("url"),
//                            recipes.getJSONObject(it).getString("dietLabel"))
//                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return recipeList
        }

        fun getPost(dataSnapshot: DataSnapshot): ArrayList<Recipe> {

            var recipeList = ArrayList<Recipe>()

            for (data in dataSnapshot.children) {
                val child = data.value
//                Log.d("string",child.toString())

                val value = child as Map<String, Any>
                val detail = value.getValue("detail").toString()
                val urlStr = value.getValue("imgURL").toString()
                val id = value.getValue("id").toString()

                recipeList.add(Recipe(detail,id,urlStr))

            }



            return recipeList
        }



        private fun loadJsonFromAsset(filename: String, context: Context): String? {
            var json: String? = null

            try {
                val inputStream = context.assets.open(filename)
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                inputStream.close()
                json = String(buffer, Charsets.UTF_8)
            } catch (ex: java.io.IOException) {
                ex.printStackTrace()
                return null
            }

            return json
        }
    }
}