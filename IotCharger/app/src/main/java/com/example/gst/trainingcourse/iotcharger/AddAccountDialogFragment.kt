package com.example.gst.trainingcourse.iotcharger

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.database.*

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
                Toast.makeText(activity,"Please insert Account and Password",Toast.LENGTH_SHORT).show()
            }else{
                val flag = checkExist(account)

                if (flag == 1){
                    pushToDatabase(account,password)      //Push data to database
                    dismiss()
                }else{
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

}