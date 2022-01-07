package com.example.gst.trainingcourse.iotcharger

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class AdapterAdminCustom(val dataList : ArrayList<Device>) : RecyclerView.Adapter<AdapterAdminCustom.ViewHolderCustom>() {

    private lateinit var database : DatabaseReference

    class ViewHolderCustom(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNameFireBase : TextView = itemView.findViewById(R.id.tvNameFirebase)
        val tvStatusFireBase : TextView = itemView.findViewById(R.id.tvStatFirebase)
        val tvServerCodeFireBase : TextView = itemView.findViewById(R.id.tvSeverCodeFirebase)
        val tvClientCodeFireBase : TextView = itemView.findViewById(R.id.tvClientCodeFirebase)

        val btnGenerate : Button = itemView.findViewById(R.id.btnGenerate)
        val btnTurnOn : Button = itemView.findViewById(R.id.btnOn)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCustom {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.adapter_admin_list_device,parent, false)
        return ViewHolderCustom(view)
    }

    override fun onBindViewHolder(holder: ViewHolderCustom, position: Int) {
        holder.tvNameFireBase.text = dataList[position].name.toString()
        holder.tvStatusFireBase.text = dataList[position].status.toString()
        holder.tvServerCodeFireBase.text = dataList[position].serverPass.toString()
        holder.tvClientCodeFireBase.text = dataList[position].clientPass.toString()

        holder.btnGenerate.setOnClickListener {
            database = FirebaseDatabase.getInstance().getReference("Device")

            val randomNumber = (0..9999).random()
            var tmp : String = randomNumber.toString()

            while (true){
                if (tmp.length!=4){
                    val zero = "0"
                    tmp = "$zero$tmp"
                }else{
                    break
                }
            }

            database.child(dataList[position].name.toString()).child("serverPass").setValue(tmp)
            updateServerCode()
        }
    }

    override fun getItemCount(): Int = dataList.size

    fun updateData(newDatalist : ArrayList<Device>){
        dataList.clear()
        dataList.addAll(newDatalist)
        notifyDataSetChanged()
    }

    fun updateServerCode(){
        dataList.clear()
        notifyDataSetChanged()
    }
}