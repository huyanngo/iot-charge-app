package com.example.gst.trainingcourse.iotcharger.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.gst.trainingcourse.iotcharger.R
import com.example.gst.trainingcourse.iotcharger.model.Device
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.math.BigInteger
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*


class AdapterAdminCustom(
    private val dataList: ArrayList<Device>,
    private val parentFragment: Fragment
) :
    RecyclerView.Adapter<AdapterAdminCustom.ViewHolderCustom>() {

    private lateinit var database: DatabaseReference

    class ViewHolderCustom(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNameFireBase: TextView = itemView.findViewById(R.id.tvNameFirebase)
        val tvStatusFireBase: TextView = itemView.findViewById(R.id.tvStatFirebase)
        val tvServerCodeFireBase: TextView = itemView.findViewById(R.id.tvSeverCodeFirebase)
        val tvClientCodeFireBase: TextView = itemView.findViewById(R.id.tvClientCodeFirebase)

        val btnGenerate: Button = itemView.findViewById(R.id.btnGenerate)
        val btnTurnOn: Button = itemView.findViewById(R.id.btnOn)
        val btnTurnOff: Button = itemView.findViewById(R.id.btnOff)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCustom {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_admin_list_device, parent, false)
        return ViewHolderCustom(view)
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolderCustom, position: Int) {
        holder.tvNameFireBase.text = dataList[position].name.toString()

        if (Integer.parseInt(dataList[position].status.toString()) == 0) {
            holder.tvStatusFireBase.text = "OFF"
        } else {
            holder.tvStatusFireBase.text = "ON"
        }

        holder.tvServerCodeFireBase.text = dataList[position].serverPass.toString()
        holder.tvClientCodeFireBase.text = dataList[position].clientPass.toString()

        /**
         * When pressed the btnGenerate create a random number with 4 digits and push to database and turn off the charger
         * Set all element to null and set dateOn to now
         */
        holder.btnGenerate.setOnClickListener {

            database = FirebaseDatabase.getInstance().getReference("Device")

            val randomNumber = (0..9999).random()
            var tmp: String = randomNumber.toString()

            while (true) {
                if (tmp.length != 4) {
                    val zero = "0"
                    tmp = "$zero$tmp"
                } else {
                    break
                }
            }

            database.child(dataList[position].name.toString()).child("serverPass").setValue(encryptPassword(tmp))
            database.child(dataList[position].name.toString()).child("clientPass").setValue("null")
            database.child(dataList[position].name.toString()).child("status").setValue(0)

            //The password only appear once and can not be retrieved again
            Toast.makeText(
                parentFragment.context,
                "Device ${dataList[position].name} password is: $tmp",
                Toast.LENGTH_LONG
            ).show()

            /**
             * Update time DateOn
             */
            val calendar = Calendar.getInstance();
            val simpleDateFormat = SimpleDateFormat("HH:mm:ss")
            val date = simpleDateFormat.format(calendar.time)

            database.child(dataList[position].name.toString()).child("dateOn").setValue(date.toString())
            updateAdapter()
        }

        /**
         * Check status from database
         */
        if (dataList[position].status == 1) {
            holder.btnTurnOn.visibility = View.INVISIBLE
            holder.btnTurnOff.visibility = View.VISIBLE
        } else {
            holder.btnTurnOn.visibility = View.VISIBLE
            holder.btnTurnOff.visibility = View.INVISIBLE
        }



        /**
         * When pressed the btnTurnOn, checked if status is ON yet, then pushed to database
         */
        holder.btnTurnOn.setOnClickListener {
            if (dataList[position].serverPass == dataList[position].clientPass && dataList[position].serverPass != "null") {
                database =
                    FirebaseDatabase.getInstance().getReference("Device")               //Initialize

                database.child(dataList[position].name.toString()).child("status").setValue(1)
                holder.btnTurnOn.visibility = View.INVISIBLE
                holder.btnTurnOff.visibility = View.VISIBLE

                /**
                 * Update time DateOn
                 */
                val calendar = Calendar.getInstance();
                val simpleDateFormat = SimpleDateFormat("HH:mm:ss")
                val date = simpleDateFormat.format(calendar.time)

                database.child(dataList[position].name.toString()).child("dateOn")
                    .setValue(date.toString())

                updateAdapter()
            } else {
                Toast.makeText(
                    parentFragment.context,
                    "Unable to Turn On device ${dataList[position].name}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }



        /**
         * When pressed the btnTurnOff, pushed status OFF to database
         */
        holder.btnTurnOff.setOnClickListener {
            database =
                FirebaseDatabase.getInstance().getReference("Device")                   //Initialize

            database.child(dataList[position].name.toString()).child("status").setValue(0)
            database.child(dataList[position].name.toString()).child("serverPass").setValue("null")
            database.child(dataList[position].name.toString()).child("clientPass").setValue("null")
            database.child(dataList[position].name.toString()).child("dateOn").setValue("null")
            holder.btnTurnOff.visibility = View.INVISIBLE
            holder.btnTurnOn.visibility = View.VISIBLE

            updateAdapter()
        }
    }

    fun turnOnCharger(){

    }

    override fun getItemCount(): Int = dataList.size

    fun updateData(newDatalist: ArrayList<Device>) {
        dataList.clear()
        dataList.addAll(newDatalist)
        notifyDataSetChanged()
    }

    private fun updateAdapter() {
        notifyDataSetChanged()
    }


    /**
     * Encrypt the input password and push to the database
     */
    @SuppressLint("GetInstance")
    fun encryptPassword(password : String) : String{
        var hashValue = ""
        try {
            // Static getInstance method is called with hashing MD5
            val mMessageDigest = MessageDigest.getInstance("MD5")

            //Update method simply appends the input byte password array to the current tempArray of the MessageDigestSpi abstract class.
            //-->Updates the digest using the specified array of bytes, starting at the specified offset.
            mMessageDigest.update(password.toByteArray(),0,password.length)

            //BigInteger convert Byte array into signum representation
            //Then convert message to hex value
            hashValue = BigInteger(1, mMessageDigest.digest()).toString(16)

            println(hashValue)
        }catch (e: Exception){

        }

        return hashValue
    }
}