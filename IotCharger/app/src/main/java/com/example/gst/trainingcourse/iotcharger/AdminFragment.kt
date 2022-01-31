package com.example.gst.trainingcourse.iotcharger

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.gst.trainingcourse.iotcharger.databinding.FragmentAdminScreenBinding
import com.google.firebase.database.*

class AdminFragment : Fragment() {
    private lateinit var binding: FragmentAdminScreenBinding
    private lateinit var database : DatabaseReference
    private lateinit var dataList : ArrayList<Device>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataList = arrayListOf<Device>()
        retrieveDeviceList()

        binding.ctLogOut.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_adminScreenFragment_to_loginFragment)
        }

        binding.ctAddAccount.setOnClickListener {
            addAccount()
            notifyUpdate()
        }

        binding.ctAddDevice.setOnClickListener {
            addDevice()
            notifyUpdate()
        }

        Log.d("size", dataList.size.toString())
    }

    private fun notifyUpdate() {
        retrieveDeviceList()
    }

    private fun addDevice() {
        val addDevice = AddDeviceDialogFragment(dataList)
        addDevice.show(activity?.supportFragmentManager!!,"asd")
    }

    private fun addAccount() {
        val addAccount = AddAccountDialogFragment()
        addAccount.show(activity?.supportFragmentManager!!,"asd")
    }


    private fun retrieveDeviceList() {
        database = FirebaseDatabase.getInstance().getReference("Device")

        val newArrayList = ArrayList<Device>()
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){
                    for (deviceSnapshot in snapshot.children){

                        val device = deviceSnapshot.getValue(Device::class.java)
                        newArrayList.add(device as Device)
                    }
                }

                dataList.clear()
                dataList.addAll(newArrayList)

                Log.d("sizeA",dataList.size.toString())
                Log.d("sizeA",newArrayList.size.toString())

                binding.recyclerView.adapter = AdapterAdminCustom(dataList)
                AdapterAdminCustom(dataList).updateData(newArrayList)
                newArrayList.clear()

                Log.d("sizeAdapter", dataList.size.toString())

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


}