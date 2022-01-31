package com.example.gst.trainingcourse.iotcharger

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavArgs
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.gst.trainingcourse.iotcharger.databinding.FragmentConnectFillBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ConnectFillFragment : Fragment() {

    private val args : ConnectFillFragmentArgs by navArgs()

    private lateinit var binding: FragmentConnectFillBinding
    private lateinit var database: DatabaseReference
    private lateinit var dataList : ArrayList<Device>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConnectFillBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nameReceived = args.name
        binding.tvDeviceNameFirebase.text = nameReceived

        binding.btnConnectCharge.setOnClickListener {
            val insertedCode = binding.edtInsertCode.text.toString()

            if( insertedCode.isEmpty()){
                Toast.makeText(context,"Please insert the Code",Toast.LENGTH_SHORT).show()
            }else{
                database = FirebaseDatabase.getInstance().getReference("Device")
                database.child(nameReceived).child("clientPass").setValue(insertedCode)

                binding.edtInsertCode.text.clear()
            }
        }

        binding.btnCancelConnect.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_connectFillFragment_to_userFragment)
        }
    }
}