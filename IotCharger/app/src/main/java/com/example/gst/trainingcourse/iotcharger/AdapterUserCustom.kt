package com.example.gst.trainingcourse.iotcharger

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AdapterUserCustom(val dataList : ArrayList<Device>): RecyclerView.Adapter<AdapterUserCustom.ViewHolderCustom>() {

    private lateinit var database : DatabaseReference

    class ViewHolderCustom(itemView : View) : RecyclerView.ViewHolder(itemView){
        val tvUserNameFirebase : TextView = itemView.findViewById(R.id.tvUserNameFirebase)
        val tvNameStatFirebase : TextView = itemView.findViewById(R.id.tvUserNameFirebase)

        val btnConnect : Button = itemView.findViewById(R.id.btnConnect)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCustom {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolderCustom, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}