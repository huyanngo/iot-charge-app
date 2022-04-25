package com.example.gst.trainingcourse.iotcharger.dialogfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.gst.trainingcourse.iotcharger.model.Device
import com.example.gst.trainingcourse.iotcharger.R
import com.example.gst.trainingcourse.iotcharger.constant.CONSTANT
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddDeviceDialogFragment(private val dataList : ArrayList<Device>) : DialogFragment() {

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
            if (name.isNotEmpty()) {
                val flag: Int = checkExist(name)

                if (flag == 1) {
                    pushDeviceToDtb(name)
                    dismiss()
                } else {
                    edtDeviceName.text.clear()
                    edtDeviceName.error = CONSTANT.ERROR_DEVICE_NAME
                    Toast.makeText(activity, "Device already exist", Toast.LENGTH_SHORT).show()
                }
            }else{
                edtDeviceName.error = CONSTANT.ERROR_DEVICE_NAME
                Toast.makeText(activity, "Please insert Device ID", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
        return rootView
    }

    private fun pushDeviceToDtb(name: String) {
        val device = Device(name,0,"null","null","null")
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