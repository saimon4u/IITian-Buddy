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
//    private lateinit var binding: FragmentHomeBinding
    private lateinit var eventList: ArrayList<Event>
    private lateinit var adapter: HomeFragmentEventAdapter

    private var _binding: FragmentHomeBinding? = null

    // with the backing property of the kotlin
    // we extract
    // the non null value of the _binding
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        initializer();
        setCurrentUserData();
        getCurrentEvents()


        binding.profileImg.setOnClickListener {
            setCurrentEvents()
        }

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

    private fun setCurrentEvents() {
        val eventCollection: CollectionReference = database.collection("Events").document("Current").collection("Collection")
        val model = Event(title = "IIT Indoor Games", imgLink = "https://firebasestorage.googleapis.com/v0/b/iitian-buddy-caddb.appspot.com/o/Events%2FFB_IMG_1710957745854.jpg?alt=media&token=fe58184b-d3f6-4262-9fad-7c9905e9e0fe")
        val model1 = Event(title = "IIT Family Tour", imgLink = "https://firebasestorage.googleapis.com/v0/b/iitian-buddy-caddb.appspot.com/o/Events%2FFB_IMG_1710957785861.jpg?alt=media&token=2a1875d6-02d8-42c6-9c91-069e3b4c4eb7")
        val model2 = Event(title = "Flutter Frenzy", imgLink = "https://firebasestorage.googleapis.com/v0/b/iitian-buddy-caddb.appspot.com/o/Events%2FFB_IMG_1710957904751.jpg?alt=media&token=66be78a7-2044-4ab2-a51b-91c71219f64d")
        val model3 = Event(title = "ITVerse 2023", imgLink = "https://firebasestorage.googleapis.com/v0/b/iitian-buddy-caddb.appspot.com/o/Events%2FFB_IMG_1710957935823.jpg?alt=media&token=35432676-1435-43fb-b603-816882816ad9")

        eventCollection.add(model)
        eventCollection.add(model1)
        eventCollection.add(model2)
        eventCollection.add(model3)
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
                binding.name.text = name
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