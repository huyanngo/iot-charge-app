package com.example.gst.trainingcourse.iotcharger.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.Navigation
import com.example.gst.trainingcourse.iotcharger.adapter.AdapterUserCustom
import com.example.gst.trainingcourse.iotcharger.`object`.Device
import com.example.gst.trainingcourse.iotcharger.R
import com.example.gst.trainingcourse.iotcharger.adapter.AdapterAdminCustom
import com.example.gst.trainingcourse.iotcharger.databinding.FragmentUserScreenBinding
import com.google.firebase.database.*

class UserFragment : Fragment() {
    private lateinit var binding: FragmentUserScreenBinding
    private lateinit var database : DatabaseReference
    private lateinit var dataList : ArrayList<Device>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataList = arrayListOf<Device>()
        retrieveDeviceList()
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

        Log.d("fm_lifecycle","#onViewCre")

        /**
         * Implement Navigation for back button, or else the phone will lost current location when pressed
         */
        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                Navigation.findNavController(view).navigate(R.id.action_userScreenFragment_to_loginFragment)
            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)

        binding.ivReturn.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_userScreenFragment_to_loginFragment)
        }
    }

    /**
     * --------> Get dataList from the database
     */
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
                AdapterAdminCustom(dataList, this@UserFragment).updateData(newArrayList)
                newArrayList.clear()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        Log.d("sizeAdapter", dataList.size.toString())

    }

    override fun onResume() {
        Log.d("fm_lifecycle","#onResume")

        super.onResume()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("fm_lifecycle","#onAttach")
    }

    override fun onStart() {
        super.onStart()
        Log.d("fm_lifecycle","#onStart")
    }

    override fun onPause() {
        super.onPause()
        Log.d("fm_lifecycle","#onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("fm_lifecycle","#onStop")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("fm_lifecycle","#onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("fm_lifecycle","#onDestroy")
    }
}