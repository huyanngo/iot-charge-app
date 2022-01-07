package com.example.gst.trainingcourse.iotcharger

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddDeviceDialogFragment(val dataList : ArrayList<Device>) : DialogFragment() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView: View = inflater.inflate(R.layout.fragment_add_device_dialog, container, false)

        val btnAddToDtb = rootView.findViewById<Button>(R.id.btnAddDeviceToDtb)
        val btnCancel = rootView.findViewById<Button>(R.id.btnCancel2)
        val edtDeviceName = rootView.findViewById<EditText>(R.id.edtNumberDevice)

        database = FirebaseDatabase.getInstance().getReference("Device")

        btnAddToDtb.setOnClickListener {
            val name = edtDeviceName.text.toString()
            val flag : Int = checkExist(name)

            if (flag == 1){
                pushDeviceToDtb(name)
                dismiss()
            }else{
                Toast.makeText(activity,"Device already exist",Toast.LENGTH_SHORT).show()
                edtDeviceName.text.clear()
            }
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
        return rootView
    }

    private fun pushDeviceToDtb(name: String) {
        val device = Device(name,"off","null","null")
        database = FirebaseDatabase.getInstance().getReference("Device")
        database.child(name).setValue(device)
    }

    private fun checkExist(name: String): Int {
        var flag = 1
        for (device in dataList){
            if (device.name == name){
                flag = 0
                break
            }
        }
        return flag
    }
}