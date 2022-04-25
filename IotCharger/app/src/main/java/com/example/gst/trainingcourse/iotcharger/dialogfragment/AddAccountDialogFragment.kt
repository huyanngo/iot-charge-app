package com.example.gst.trainingcourse.iotcharger.dialogfragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.gst.trainingcourse.iotcharger.model.Account
import com.example.gst.trainingcourse.iotcharger.R
import com.example.gst.trainingcourse.iotcharger.constant.CONSTANT
import com.google.firebase.database.*
import java.math.BigInteger
import java.security.MessageDigest

class AddAccountDialogFragment : DialogFragment() {

    private lateinit var database: DatabaseReference
    private lateinit var accountList : ArrayList<Account>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.fragment_add_account_dialog, container, false)

        val btnAddToDatabase = rootView.findViewById<Button>(R.id.btnAddAccToDtb)
        val btnCancel = rootView.findViewById<Button>(R.id.btnCancel)
        val edtAddAccount = rootView.findViewById<EditText>(R.id.edtAddAccount)
        val edtAddPassword = rootView.findViewById<EditText>(R.id.edtAddPassword)

        database = FirebaseDatabase.getInstance().getReference("Account")

        accountList = ArrayList<Account>()

        retrieveData()                     //Get account list from database

        btnAddToDatabase.setOnClickListener {
            val account = edtAddAccount.text.toString()
            val password = edtAddPassword.text.toString()

            if (account.isEmpty() || password.isEmpty()){
                edtAddAccount.error = CONSTANT.ERROR_ACCOUNT
                edtAddPassword.error = CONSTANT.ERROR_PASSWORD
                Toast.makeText(activity,"Please insert Account and Password",Toast.LENGTH_SHORT).show()
            }else{
                val flag = checkExist(account)


                if (flag == 1){
                    Toast.makeText(activity,"Account: $account \nPassword: $password",Toast.LENGTH_SHORT).show()

                    val passHash = encryptPassword(password)

                    pushToDatabase(account,passHash)      //Push data to database
                    dismiss()
                }else{
                    edtAddAccount.error = CONSTANT.ERROR_ACCOUNT
                    Toast.makeText(activity,"Account already exist",Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
        return rootView
    }

    private fun checkExist(account: String): Int {
        var flag = 1
        for (acc in accountList){
            if (acc.account == account){
                flag = 0
                break
            }
        }
        return flag
    }

    private fun pushToDatabase(account: String, password: String) {
        val admin = Account(account,password)

        database = FirebaseDatabase.getInstance().getReference("Account")
        database.child(account).setValue(admin)
    }

    private fun retrieveData(){
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){
                    Log.d("asd","exist found")
                    for (accountSnapshot in snapshot.children){

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