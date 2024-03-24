package com.example.iitianbuddy.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.iitianbuddy.R
import com.example.iitianbuddy.adapters.HomeFragmentEventAdapter
import com.example.iitianbuddy.models.Event
import com.example.iitianbuddy.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class HomeFragment : Fragment() {


    private lateinit var database: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentHomeBinding
    private lateinit var eventList: ArrayList<Event>
    private lateinit var adapter: HomeFragmentEventAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        initializer();
        setCurrentUserData();
        getCurrentEvents()


        binding.classSchedule.setOnClickListener{
            loadFragment(ClassScheduleFragment())
        }

        binding.event.setOnClickListener {
            loadFragment(EventFragment())
        }



        return binding.root
    }

    private fun getCurrentEvents() {
        val colRef: CollectionReference = database.collection("Events").document("Current").collection("Collection")

        colRef.addSnapshotListener { value, error ->
            if (error!= null) {
                Toast.makeText(requireContext(), "Something went Wrong...", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            if (value == null){
                Toast.makeText(requireContext(), "There is no events...", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            eventList.clear()
            for(document: DocumentSnapshot in value.documents){
                val event: Event = document.toObject(Event::class.java)!!
                eventList.add(event)
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun loadFragment(fragment: Fragment){
        val fm: FragmentManager = requireActivity().supportFragmentManager
        fm.popBackStackImmediate()
        val ft: FragmentTransaction = fm.beginTransaction()
        ft.replace(R.id.container, fragment)
        ft.addToBackStack(null)
        ft.commit()
    }

    private fun setCurrentUserData() {
        val document: DocumentReference = database.collection("Students").document(auth.currentUser?.uid.toString())
        document.get().addOnCompleteListener {
            if(it.isSuccessful){
                val snapshot: DocumentSnapshot = it.result

                val imgLink: String = snapshot.getString("profileImg").toString()
                val fullName: String = snapshot.getString("fullName").toString()

                if(imgLink.isNotEmpty()){
                    Picasso.get().load(imgLink).into(binding.profileImg)
                }
                val name: String = fullName.split(" ")[0]
                binding.name.text = "Hi, " + name
            }
        }
    }

    private fun initializer() {
        database = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        eventList = ArrayList()
        adapter = HomeFragmentEventAdapter(requireContext(), eventList)
        binding.eventHolderRV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.eventHolderRV.adapter = adapter
    }
}