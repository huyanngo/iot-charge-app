package com.example.gst.trainingcourse.iotcharger

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView


class AdapterUserCustom(
    private val dataList: ArrayList<Device>,
    private val parentFragment: Fragment
) :
    RecyclerView.Adapter<AdapterUserCustom.ViewHolderCustom>() {

    class ViewHolderCustom(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUserNameFirebase: TextView = itemView.findViewById(R.id.tvUserNameFirebase)
        val tvUserStatFirebase: TextView = itemView.findViewById(R.id.tvUserStatFirebase)

        val btnConnect: Button = itemView.findViewById(R.id.btnConnect)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCustom {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_user_list_device, parent, false)
        return ViewHolderCustom(view)
    }

    override fun onBindViewHolder(holder: ViewHolderCustom, position: Int) {

        holder.tvUserNameFirebase.text = dataList[position].name.toString()
        holder.tvUserStatFirebase.text = dataList[position].status.toString()

        val device: Device = dataList[position]
        holder.btnConnect.setOnClickListener {
            val devName = device.name
            if (devName != null) {
                val action: NavDirections =
                    UserFragmentDirections.actionUserFragmentToConnectFillFragment(devName)
                parentFragment.findNavController().navigate(action)
            } else {
                Log.d("asd", "Empty???")
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun updateData(newDataList: ArrayList<Device>) {
        dataList.clear()
        dataList.addAll(newDataList)
        notifyDataSetChanged()
    }
}