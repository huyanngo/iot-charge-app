package com.example.gst.trainingcourse.iotcharger.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.gst.trainingcourse.iotcharger.R
import com.example.gst.trainingcourse.iotcharger.model.Device
import com.example.gst.trainingcourse.iotcharger.adapter.AdapterAdminCustom
import com.example.gst.trainingcourse.iotcharger.databinding.FragmentAdminScreenBinding
import com.example.gst.trainingcourse.iotcharger.dialogfragment.AddAccountDialogFragment
import com.example.gst.trainingcourse.iotcharger.dialogfragment.AddDeviceDialogFragment
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AdminFragment : Fragment() {
    private lateinit var binding: FragmentAdminScreenBinding
    private lateinit var database: DatabaseReference
    private lateinit var dataList: ArrayList<Device>

    private var mHandler: Handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null
    private var delay = 3000

    private var timeLimitOn = 1                       //Time limit for each charger on (Minutes)
    private var timeLimitPass = 30                       //Time limit for each password available (Seconds)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataList = arrayListOf<Device>()
        retrieveDeviceList()
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


        /**
         * Implement Navigation for back button, or else the phone will lost current location when pressed
         */
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Navigation.findNavController(view)
                    .navigate(R.id.action_adminScreenFragment_to_loginFragment)
            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)

        binding.ctLogOut.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_adminScreenFragment_to_loginFragment)
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
        Log.d("NotifyUpdate", "Notify Update")
    }


    private fun addDevice() {
        val addDevice = AddDeviceDialogFragment(dataList)
        addDevice.show(activity?.supportFragmentManager!!, "asd")
    }

    private fun addAccount() {
        val addAccount = AddAccountDialogFragment()
        addAccount.show(activity?.supportFragmentManager!!, "asd")
    }


    private fun retrieveDeviceList() {
        Log.d("#retrieveDeviceList", "Retrieved")
        database = FirebaseDatabase.getInstance().getReference("Device")

        val newArrayList = ArrayList<Device>()

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("#retrieveListener", "RetrieveListener")

                if (snapshot.exists()) {
                    for (deviceSnapshot in snapshot.children) {

                        val device = deviceSnapshot.getValue(Device::class.java)
                        newArrayList.add(device as Device)
                    }
                }

                Log.d("update", "----------------------->  Update Database")

                dataList.clear()
                dataList.addAll(newArrayList)

                /**
                 * Add dataList to adapter
                 * update new data from newArrayList to dataList
                 */
                binding.recyclerView.adapter = AdapterAdminCustom(dataList, this@AdminFragment)
                AdapterAdminCustom(dataList, this@AdminFragment).updateData(newArrayList)
                newArrayList.clear()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    override fun onResume() {
        super.onResume()

        /**
         * Start Countdown method to execute code check connection
         */
        mHandler.postDelayed(Runnable {
            mHandler.postDelayed(runnable!!, delay.toLong())

            checkConnectionAndTime()

            Log.d("#resume", "${delay / 1000} second has passed")
        }.also { runnable = it }, delay.toLong())

        Log.d("fm_lifecycle", "#onResume")
    }


    @SuppressLint("SimpleDateFormat")
    private fun checkConnectionAndTime() {
        for (device in dataList) {
            /**
             * Check if there's a equal code from server and client
             */
            if (device.clientPass == device.serverPass && device.serverPass != "null" && device.status != 1) {
                Toast.makeText(
                    context,
                    "Device " + device.name + " has correct password",
                    Toast.LENGTH_SHORT
                ).show()
            }

            /**
             * Check the Firebase dateOn. If currentDate - dateOn > acceptableTime Turnoff the charger
             */
            val dateOnFirebase = device.dateOn
            if (dateOnFirebase != "null") {
                if (dateOnFirebase != null && dateOnFirebase.isNotEmpty()) {
                    //get time firebase
                    val dateOn: Date = convertStringToDate(dateOnFirebase)

                    //get current time
                    val calendar = Calendar.getInstance();
                    val simpleDateFormat = SimpleDateFormat("HH:mm:ss")
                    val currentTimeString = simpleDateFormat.format(calendar.time)
                    val currentTime = convertStringToDate(currentTimeString)

                    //Get difference between 2 date and get
                    val difference: Long = currentTime.time - dateOn.time
                    val seconds = difference / 1000
                    val minutes = seconds / 60

                    //If Charger on and time difference passed timeLimitOn, shut down the charger
                    if (minutes.toInt() > timeLimitOn - 1 && device.status == 1) {
                        database = FirebaseDatabase.getInstance()
                            .getReference("Device")        //Initialize

                        database.child(device.name.toString()).child("status").setValue(0)
                        database.child(device.name.toString()).child("serverPass").setValue("null")
                        database.child(device.name.toString()).child("clientPass").setValue("null")
                        database.child(device.name.toString()).child("dateOn").setValue("null")

                        Toast.makeText(
                            context,
                            "Device ${device.name} has been shutdown due to time exceed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    //If Charger off and time difference passed timeLimitPass, disable the ServerCode
                    else if (seconds.toInt() > timeLimitPass && device.status == 0) {

                        database = FirebaseDatabase.getInstance()
                            .getReference("Device")        //Initialize

                        database.child(device.name.toString()).child("status").setValue(0)
                        database.child(device.name.toString()).child("serverPass").setValue("null")
                        database.child(device.name.toString()).child("clientPass").setValue("null")
                        database.child(device.name.toString()).child("dateOn").setValue("null")

                        Toast.makeText(
                            context,
                            "Password for device ${device.name} has been disable due to time exceed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    /**
     * Convert String to Date
     */
    @SuppressLint("SimpleDateFormat")
    private fun convertStringToDate(time: String): Date {
        val dateFormat = SimpleDateFormat("HH:mm:ss")
        return dateFormat.parse(time)
    }

    /**
     * Convert Date to String
     */
    @SuppressLint("SimpleDateFormat")
    private fun convertDateToString(time: Date): String {
        val dateFormat = SimpleDateFormat("HH:mm:ss")
        return dateFormat.format(time)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("fm_lifecycle", "#onAttach")
    }

    override fun onStart() {
        super.onStart()
        Log.d("fm_lifecycle", "#onStart")
    }

    override fun onPause() {
        super.onPause()
        Log.d("fm_lifecycle", "#onPause")
    }

    override fun onStop() {
        /**
         * Stop Handler countdown
         */
        runnable?.let { mHandler.removeCallbacks(it) }

        super.onStop()
        Log.d("fm_lifecycle", "#onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("fm_lifecycle", "#onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("fm_lifecycle", "#onDestroy")
    }
}