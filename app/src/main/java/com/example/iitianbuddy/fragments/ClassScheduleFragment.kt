package com.example.iitianbuddy.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.iitianbuddy.R
import com.example.iitianbuddy.adapters.ClassScheduleClassesAdapter
import com.example.iitianbuddy.databinding.FragmentClassScheduleBinding
import com.example.iitianbuddy.models.Classes
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Boolean
import java.util.Calendar
import kotlin.String
import kotlin.toString

class ClassScheduleFragment : Fragment() {


    private lateinit var binding: FragmentClassScheduleBinding
    private lateinit var database: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: ClassScheduleClassesAdapter
    private lateinit var batch: String
    private lateinit var previousDay: String
    private lateinit var currentDay: String
    private lateinit var classList: ArrayList<Classes>
    private val mainScope = CoroutineScope(Dispatchers.IO)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentClassScheduleBinding.inflate(inflater, container, false)


        mainScope.launch {
            initializer()
            getCurrentUserData()
            currentDay = getCurrentDay()
            selectDay(getDayTextView(currentDay))
            getCurrentClasses()
        }


//        initializer()
//        getCurrentUserData()
//        currentDay = getCurrentDay()
//        selectDay(getDayTextView(currentDay))
//        getCurrentClasses()


        binding.sun.setOnClickListener {
            previousDay = currentDay
            currentDay = "sun"
            selectDay(binding.sun)
            getCurrentClasses()
        }

        binding.mon.setOnClickListener {
            previousDay = currentDay
            currentDay = "mon"
            selectDay(binding.mon)
            getCurrentClasses()
        }

        binding.tue.setOnClickListener {
            previousDay = currentDay
            currentDay = "tue"
            selectDay(binding.tue)
            getCurrentClasses()
        }

        binding.wed.setOnClickListener {
            previousDay = currentDay
            currentDay = "wed"
            selectDay(binding.wed)
            getCurrentClasses()
        }

        binding.thu.setOnClickListener {
            previousDay = currentDay
            currentDay = "thu"
            selectDay(binding.thu)
            getCurrentClasses()
        }

        binding.extra.setOnClickListener {
            previousDay = currentDay
            currentDay = "extra"
            selectDay(binding.extra)
            getCurrentClasses()
        }

        binding.fab.setOnClickListener {
            addClassToDatabase()
        }


        return binding.root
    }

    private fun addClassToDatabase() {
        val dialog: Dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.edit_class_layout)
        val save: Button = dialog.findViewById(R.id.save)
        val cancel: Button = dialog.findViewById(R.id.cancel)
        val spinner: Spinner = dialog.findViewById(R.id.spinner)
        val courseName: TextInputLayout = dialog.findViewById(R.id.course_code)
        val instructorName: TextInputLayout = dialog.findViewById(R.id.instructor_name)
        val roomNumber: TextInputLayout = dialog.findViewById(R.id.room_number)
        val fromTime: TextInputLayout = dialog.findViewById(R.id.from_time)
        val toTime: TextInputLayout = dialog.findViewById(R.id.to_time)
        dialog.setCancelable(false)
        dialog.show()


        val items: Array<String> = arrayOf("sun", "mon", "tue", "wed", "thu", "extra")
        val adapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, items)
        spinner.adapter = adapter

        for(i: Int in items.indices){
            if(items[i] == currentDay){
                spinner.setSelection(i)
                break
            }
        }

        cancel.setOnClickListener {
            dialog.dismiss()
        }

        save.setOnClickListener {
            val cName: String = courseName.editText!!.text.toString()
            val iName: String = instructorName.editText!!.text.toString()
            val rNumber: String = roomNumber.editText!!.text.toString()
            val fTime: String = fromTime.editText!!.text.toString()
            val tTime: String = toTime.editText!!.text.toString()
            val day: String = spinner.selectedItem.toString()

            val model: Classes = Classes(cName, iName, rNumber, fTime, tTime, day, "Regular")

            val document: DocumentReference = database.collection("Classes").document(batch).collection(day).document(cName)
            document.set(model).addOnCompleteListener {
                if(it.isSuccessful){
                    Toast.makeText(requireContext(), "Class Added!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                else{
                    Toast.makeText(requireContext(), "Something went wrong...", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
        }
    }

    private fun getCurrentClasses() {
        val collectionRef: CollectionReference = database.collection("Classes").document(batch).collection(currentDay)

        collectionRef.addSnapshotListener { value, error ->
            if(error != null){
                Toast.makeText(requireContext(), "Something went wrong...", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            if(value == null){
                Toast.makeText(requireContext(), "There is no classes...", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            if(value.size() == 0){
                binding.message.visibility = View.VISIBLE
            }
            else{
                binding.message.visibility = View.GONE
            }
            classList.clear()
            for(snapshot: DocumentSnapshot in value.documents){
                val classModel: Classes = snapshot.toObject(Classes::class.java)!!
                classList.add(classModel)
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun selectDay(dayTextView: TextView) {
        if(previousDay.isNotEmpty()){
            unselectDay(getDayTextView(previousDay))
        }
        val drawable: Drawable = ContextCompat.getDrawable(requireContext(), R.drawable.day_select_shape)!!
        dayTextView.background = drawable
        dayTextView.setTextColor(Color.parseColor("#210440"))
    }

    private fun unselectDay(dayTextView: TextView) {
        dayTextView.background = null
        dayTextView.setTextColor(Color.parseColor("#FFFDB095"))
    }

    private fun getDayTextView(currentDay: String): TextView {
        return when (currentDay){
            "sun"-> binding.sun
            "mon"-> binding.mon
            "tue"-> binding.tue
            "wed"-> binding.wed
            "thu"-> binding.thu
            else -> binding.extra
        }
    }

    private fun getCurrentDay(): String {
        val calendar: Calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.DAY_OF_WEEK)){
            Calendar.MONDAY -> "mon"
            Calendar.TUESDAY -> "tue"
            Calendar.WEDNESDAY -> "wed"
            Calendar.THURSDAY -> "thu"
            Calendar.SUNDAY -> "sun"
            else -> "extra"
        }
    }

    private suspend fun getCurrentUserData() {
        val document: DocumentReference = database.collection("Students").document(auth.currentUser?.uid.toString())

        document.get().addOnCompleteListener {
            if(it.isSuccessful){
                val snapshot: DocumentSnapshot = it.result

                val imgLink: String = snapshot.getString("profileImg").toString()

                if(imgLink.isNotEmpty()){
                    Picasso.get().load(imgLink).into(binding.profileImg)
                }
                if(Boolean.TRUE == snapshot.getBoolean("cr")) {
                    binding.fab.visibility = View.VISIBLE
                    ClassScheduleClassesAdapter.cr = true
                }
                else{
                    ClassScheduleClassesAdapter.cr = false
                }

                batch = snapshot.getString("batch").toString()
                ClassScheduleClassesAdapter.batch = batch
            }
            else{
                Toast.makeText(requireContext(), "Something went wrong...", Toast.LENGTH_SHORT).show()
            }
        }.await()
    }


    private fun initializer() {
        database = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        classList = ArrayList()
        adapter = ClassScheduleClassesAdapter(requireContext(), classList)
        binding.clasHolderRV.layoutManager = LinearLayoutManager(requireContext())
        binding.clasHolderRV.adapter = adapter
        previousDay = ""
        batch = ""
    }
}