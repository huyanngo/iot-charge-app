package com.example.gst.trainingcourse.iotcharger.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
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
import com.example.gst.trainingcourse.iotcharger.MapsActivity
import com.example.gst.trainingcourse.iotcharger.R
import com.example.gst.trainingcourse.iotcharger.constant.CONSTANT
import com.example.gst.trainingcourse.iotcharger.databinding.FragmentLoginBinding
import com.example.gst.trainingcourse.iotcharger.model.Account
import com.google.firebase.database.*
import java.math.BigInteger
import java.security.MessageDigest

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var database: DatabaseReference
    private var accountList: ArrayList<Account> = arrayListOf<Account>()


    private var mHandler: Handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null
    private var delay = 2000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val checkbox = binding.cbAdmin

        /**
         * When user pressed the back button, kill the app
         */
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val builder = AlertDialog.Builder(activity!!)
                builder.setTitle("Exit IoTCharge")
                builder.setMessage("Do you want to exit the app?")
                builder.setPositiveButton("Yes") { dialog, which ->
                    System.exit(0)
                }

                builder.setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }

                val alert = builder.create()
                alert.show()
            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)

        checkbox.setOnClickListener {
            if (checkbox.isChecked) {
                binding.edtAccount.visibility = View.VISIBLE
                binding.edtPassword.visibility = View.VISIBLE
                binding.btnLogin.text = CONSTANT.LOGIN_ADMIN

            } else {
                binding.edtAccount.visibility = View.INVISIBLE
                binding.edtPassword.visibility = View.INVISIBLE
                binding.btnLogin.text = CONSTANT.LOGIN_CLIENT
            }
        }

        /**
         * Check if the inserted account exist in the Firebase database
         */
        binding.btnLogin.setOnClickListener {
            if (checkbox.isChecked) {
                var valid = 0
                for (account in accountList) {
                    val acc = binding.edtAccount.text.toString()
                    val pass = binding.edtPassword.text.toString()
                    val passHash = encryptPassword(pass)

                    if (acc == account.account.toString() && passHash == account.password.toString()) {
                        Navigation.findNavController(view)
                            .navigate(R.id.action_loginFragment_to_adminScreenFragment)
                        valid = 1
                    }
                }
                if (valid == 0) {
                    binding.edtAccount.text.clear()
                    binding.edtPassword.text.clear()
                    binding.edtAccount.error = CONSTANT.ERROR_ACCOUNT
                    binding.edtPassword.error = CONSTANT.ERROR_PASSWORD
                }
            } else {
                Navigation.findNavController(view)
                    .navigate(R.id.action_loginFragment_to_userScreenFragment)
            }
        }

        //Go to MapActivity onclick. Kill current Activity
        binding.btnMap.setOnClickListener {
            val intent = Intent(context, MapsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            activity?.finish()
        }
    }

    private fun retrieveData() {
        database = FirebaseDatabase.getInstance().getReference("Account")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    for (accountSnapshot in snapshot.children) {

                        val account = accountSnapshot.getValue(Account::class.java)
                        accountList.add(account as Account)

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onResume() {
        super.onResume()
        Log.d("LoginCheckResume", "Resume")
        binding.cbAdmin.isChecked = false

        /**
         * Start Countdown method to execute code check if accountList is empty
         */
        mHandler.postDelayed(Runnable {
            mHandler.postDelayed(runnable!!, delay.toLong())
            if (accountList.isEmpty()) {

                retrieveData()
                Toast.makeText(context, CONSTANT.LOADING, Toast.LENGTH_SHORT).show()

            } else {

                Toast.makeText(context, CONSTANT.STAND_BY, Toast.LENGTH_SHORT)
                    .show()
                binding.btnLogin.isEnabled = true
                binding.cbAdmin.isEnabled = true
                binding.btnMap.isEnabled = true
                /**
                 * Stop Handler countdown
                 */
                runnable?.let { mHandler.removeCallbacks(it) }
            }

            Log.d("#handlerCheck", "${delay / 1000} second has passed")

        }.also { runnable = it }, delay.toLong())
    }

    override fun onStop() {
        /**
         * Stop Handler countdown
         */
        runnable?.let { mHandler.removeCallbacks(it) }

        super.onStop()
    }

    /**
     * Encrypt the input password and push to the database
     */
    @SuppressLint("GetInstance")
    fun encryptPassword(password: String): String {
        var hashValue = ""
        try {
            val mMessageDigest = MessageDigest.getInstance("MD5")

            mMessageDigest.update(password.toByteArray(), 0, password.length)

            hashValue = BigInteger(1, mMessageDigest.digest()).toString(16)

            println(hashValue)
        } catch (e: Exception) {

        }

        return hashValue
    }
}