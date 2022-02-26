package com.example.gst.trainingcourse.iotcharger.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.gst.trainingcourse.iotcharger.R
import com.example.gst.trainingcourse.iotcharger.`object`.Account
import com.example.gst.trainingcourse.iotcharger.databinding.FragmentLoginBinding
import com.google.firebase.database.*

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var database: DatabaseReference
    private var accountList: ArrayList<Account> = arrayListOf<Account>()


    private var mHandler: Handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null
    private var delay = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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

        checkbox.setOnClickListener {
            if (checkbox.isChecked) {
                binding.edtAccount.visibility = View.VISIBLE
                binding.edtPassword.visibility = View.VISIBLE
                binding.btnLogin.text = getString(R.string.login_as_admin)

            } else {
                binding.edtAccount.visibility = View.INVISIBLE
                binding.edtPassword.visibility = View.INVISIBLE
                binding.btnLogin.text = getString(R.string.login_as_client)
            }
        }

        /**
         * Check if the inserted account exist in the Firebase database
         */
        binding.btnLogin.setOnClickListener {
            if (checkbox.isChecked) {
                var valid = 0
                Log.d("list", accountList.size.toString())
                for (account in accountList) {
                    val acc = binding.edtAccount.text.toString()
                    val pass = binding.edtPassword.text.toString()

                    if (acc == account.account.toString() && pass == account.password.toString()) {
                        Navigation.findNavController(view)
                            .navigate(R.id.action_loginFragment_to_adminScreenFragment)
                        valid = 1
                    }
                }
                if (valid == 0) {
                    Toast.makeText(activity, "Invalid", Toast.LENGTH_SHORT).show()
                }
                binding.edtAccount.text.clear()
                binding.edtPassword.text.clear()
            } else {
                Navigation.findNavController(view)
                    .navigate(R.id.action_loginFragment_to_userScreenFragment)
            }
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
                Toast.makeText(context,"Loading...",Toast.LENGTH_SHORT).show()

            } else {

                Toast.makeText(context,"Stand by. Please choose your mode",Toast.LENGTH_SHORT).show()
                binding.btnLogin.isEnabled = true
                binding.cbAdmin.isEnabled = true
                /**
                 * Stop Handler countdown
                 */
                runnable?.let { mHandler.removeCallbacks(it) }
                Log.d("#handlerCheck", "Found list")
            }

            Log.d("#handlerCheck", "${delay / 1000} second has passed")

        }.also { runnable = it }, delay.toLong())
    }

    override fun onStop() {
        super.onStop()
        Log.d("LoginCheckStop", "Stop")
    }
}