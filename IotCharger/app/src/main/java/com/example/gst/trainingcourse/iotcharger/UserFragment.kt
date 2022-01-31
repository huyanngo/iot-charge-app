package com.example.gst.trainingcourse.iotcharger

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.gst.trainingcourse.iotcharger.databinding.FragmentUserScreenBinding
import com.google.firebase.database.*

class UserFragment : Fragment() {
    private lateinit var binding: FragmentUserScreenBinding
    private lateinit var database : DatabaseReference
    private lateinit var dataList : ArrayList<Device>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataList = arrayListOf<Device>()
        retrieveDeviceList()

        binding.ivReturn.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_userScreenFragment_to_loginFragment)
        }
    }

    private fun retrieveDeviceList() {
        database = FirebaseDatabase.getInstance().getReference("Device")

        val newArrayList = ArrayList<Device>()
        database.addValueEventListener(object : ValueEventListener {                                //Add all data from Firebase to dataList
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){
                    for (deviceSnapshot in snapshot.children){

                        val device = deviceSnapshot.getValue(Device::class.java)
                        newArrayList.add(device as Device)
                    }
                }

                dataList.clear()
                dataList.addAll(newArrayList)

                binding.recyclerView.adapter = AdapterUserCustom(dataList,this@UserFragment)    //Get data from dataList to adapter
                AdapterUserCustom(dataList,this@UserFragment).updateData(newArrayList)
                newArrayList.clear()

                Log.d("sizeAdapter", dataList.size.toString())

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}