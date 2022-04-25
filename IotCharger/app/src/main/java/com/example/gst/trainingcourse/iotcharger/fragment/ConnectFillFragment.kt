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
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.gst.trainingcourse.iotcharger.R
import com.example.gst.trainingcourse.iotcharger.constant.CONSTANT
import com.example.gst.trainingcourse.iotcharger.databinding.FragmentConnectFillBinding
import com.google.firebase.database.*
import java.math.BigInteger
import java.security.MessageDigest

class ConnectFillFragment : Fragment() {

    /**
     * Safe Args to transfer data from fragment to another
     */
    private val args : ConnectFillFragmentArgs by navArgs()

    private lateinit var binding: FragmentConnectFillBinding
    private lateinit var database: DatabaseReference

    var deviceStatus : Long = 0

    /**
     * Using Handler to set countdown method
     */
    private var mHandler : Handler = Handler(Looper.getMainLooper())
    private var runnable : Runnable? = null
    private var delay = 1000

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


        /**
         * Implement Navigation for back button, or else the phone will lost current location when pressed
         */
        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (deviceStatus.toInt() == 0) {
                    Navigation.findNavController(view)
                        .navigate(R.id.action_connectFillFragment_to_userFragment)
                }else{
                    Toast.makeText(context,"Don't kill the App", Toast.LENGTH_SHORT).show()
                }
            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)

        countDownCheck(nameReceived)

        /**
         * Push the inserted code to the database
         */
        binding.btnConnectCharge.setOnClickListener {
            val insertedCode = binding.edtInsertCode.text.toString()

            if( insertedCode.isEmpty()){
                binding.edtInsertCode.error = CONSTANT.ERROR_DEVICE_PASSWORD
                Toast.makeText(context,"Please insert the Code",Toast.LENGTH_SHORT).show()
            }else{
                database = FirebaseDatabase.getInstance().getReference("Device")
                database.child(nameReceived).child("clientPass").setValue(encryptPassword(insertedCode))

                binding.edtInsertCode.text.clear()
            }
        }

        binding.btnCancelConnect.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_connectFillFragment_to_userFragment)
        }
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

                frameBackgroundConnect.visibility = View.VISIBLE
                tvConnectedSign.setText(R.string.connected)
                tvConnectedSign.setTextColor(ContextCompat.getColor(context!!,R.color.green_light))
                edtInsertCode.isEnabled = false
                btnConnectCharge.isEnabled = false
                btnCancelConnect.isEnabled = false

            }
        }else{
            binding.apply {

                frameBackgroundConnect.visibility = View.VISIBLE
                tvConnectedSign.setText(R.string.disconnected)
                tvConnectedSign.setTextColor(ContextCompat.getColor(context!!,R.color.red_light))
                edtInsertCode.isEnabled = true
                btnConnectCharge.isEnabled = true
                btnCancelConnect.isEnabled = true

            }
        }
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onStop() {
        /**
         * Stop Handler countdown
         */
        runnable?.let { mHandler.removeCallbacks(it) }
        Log.d("#stopHandler","Stop countdown")

        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    /**
     * Encrypt the input password and push to the database
     */
    @SuppressLint("GetInstance")
    fun encryptPassword(password : String) : String{
        var hashValue = ""
        try {
            val mMessageDigest = MessageDigest.getInstance("MD5")

            mMessageDigest.update(password.toByteArray(),0,password.length)

            hashValue = BigInteger(1, mMessageDigest.digest()).toString(16)

            println(hashValue)
        }catch (e: Exception){

        }

        return hashValue
    }
}