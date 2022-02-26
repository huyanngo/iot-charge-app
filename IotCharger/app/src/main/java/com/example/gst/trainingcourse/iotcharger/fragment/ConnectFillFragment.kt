package com.example.gst.trainingcourse.iotcharger.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.gst.trainingcourse.iotcharger.R
import com.example.gst.trainingcourse.iotcharger.`object`.Device
import com.example.gst.trainingcourse.iotcharger.adapter.AdapterAdminCustom
import com.example.gst.trainingcourse.iotcharger.databinding.FragmentConnectFillBinding
import com.google.firebase.database.*

class ConnectFillFragment : Fragment() {

    /**
     * Safe Args to transfer data from fragment to another
     */
    private val args : ConnectFillFragmentArgs by navArgs()

    private lateinit var binding: FragmentConnectFillBinding
    private lateinit var database: DatabaseReference
    private lateinit var dataList : ArrayList<Device>

    var deviceStatus : Long = 0

    /**
     * Using Handler to set countdown method
     */
    private var mHandler : Handler = Handler(Looper.getMainLooper())
    private var runnable : Runnable? = null
    private var delay = 1000
    private var delayOff = 5000


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

        countDownCheck(nameReceived)

        /**
         * Push the inserted code to the database
         */
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

    override fun onResume() {
        super.onResume()

    }

    private fun countDownCheck(nameReceived : String){
        /**
         * Start Countdown method to execute code check connection
         */
        mHandler.postDelayed(Runnable {
            mHandler.postDelayed(runnable!!,delay.toLong())
            checkConnection(nameReceived)
            Log.d("#startHandler","${delay/1000} second has passed")
        }.also { runnable = it },delay.toLong())
    }

    /**
     * Check if status is ON yet
     */
    @SuppressLint("ResourceAsColor")
    private fun checkConnection(nameReceived : String){
        database = FirebaseDatabase.getInstance().getReference("Device")
        database.child(nameReceived).get().addOnSuccessListener {

            if ( it.exists()){
                deviceStatus = it.child("status").value as Long
            }else{
                Toast.makeText(context,"ERROR",Toast.LENGTH_SHORT).show()
            }

        }.addOnFailureListener {

            Toast.makeText(context,"ERROR",Toast.LENGTH_SHORT).show()

        }
        if (deviceStatus.toInt() == 1){
            binding.apply {

                tvConnectedSign.visibility = View.VISIBLE
                tvConnectedSign.setText(R.string.connected)
                tvConnectedSign.setTextColor(ContextCompat.getColor(context!!,R.color.green_light))
                edtInsertCode.isEnabled = false
                btnConnectCharge.isEnabled = false

            }
        }else{
            binding.apply {

                tvConnectedSign.visibility = View.VISIBLE
                tvConnectedSign.setText(R.string.disconnected)
                tvConnectedSign.setTextColor(ContextCompat.getColor(context!!,R.color.red_light))
                edtInsertCode.isEnabled = true
                btnConnectCharge.isEnabled = true

            }
        }
    }

    override fun onStop() {
        /**
         * Stop Handler countdown
         */
        runnable?.let { mHandler.removeCallbacks(it) }
        Log.d("#stopHandler","Stop countdown")

        super.onStop()
    }
}