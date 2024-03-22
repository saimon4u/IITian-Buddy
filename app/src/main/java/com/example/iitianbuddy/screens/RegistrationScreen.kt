package com.example.iitianbuddy.screens

import android.app.ActivityOptions
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Pair
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.iitianbuddy.databinding.ActivityRegistrationScreenBinding
import com.example.iitianbuddy.models.Student
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date

class RegistrationScreen : AppCompatActivity() {


    private lateinit var binding: ActivityRegistrationScreenBinding
    private val emailPattern: String = "^bsse[0-9]+@iit\\.du\\.ac\\.bd"
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializer()
        setBatchSpinner()

        binding.redirectToLoginScreen.setOnClickListener {
            val intent: Intent = Intent(this@RegistrationScreen, LoginScreen::class.java)
            val pairs: Array<Pair<View, String>?> = arrayOfNulls(8)

            pairs[0] = Pair<View, String>(binding.logo, "logo")
            pairs[1] = Pair<View, String>(binding.greetings, "text")
            pairs[2] = Pair<View, String>(binding.toggleLayout, "toggle")
            pairs[3] = Pair<View, String>(binding.email, "email")
            pairs[4] = Pair<View, String>(binding.password, "password")
            pairs[5] = Pair<View, String>(binding.registerAccountBtn, "btn")
            pairs[6] = Pair<View, String>(binding.options, "option")
            pairs[7] = Pair<View, String>(binding.optionsLayout, "option_image")

            val options = ActivityOptions.makeSceneTransitionAnimation(this@RegistrationScreen, *pairs)
            startActivity(intent, options.toBundle())
            finish()
        }

        binding.registerAccountBtn.setOnClickListener {
            registerAccountWithEmail()
        }
    }

    private fun registerAccountWithEmail(){
        val name: String = binding.name.editText?.text.toString()
        val email: String = binding.email.editText?.text.toString()
        val password: String = binding.password.editText?.text.toString()
        val confirmPassword: String = binding.confirmPassword.editText?.text.toString()
        val batch: String = binding.spinner.selectedItem.toString()

        if(TextUtils.isEmpty(email)){
            binding.email.error = "Email is required"
        }
        else if(TextUtils.isEmpty(password)){
            binding.password.error = "Password is required"
        }
        else if(TextUtils.isEmpty(name)){
            binding.name.error = "Name is required"
        }
        else if(!email.matches(emailPattern.toRegex())){
            binding.email.error = "Invalid BSSE email"
        }
        else if(password.length < 8){
            binding.password.error = "Password must be at least 8 characters"
        }
        else if(!password.equals(confirmPassword)){
            binding.confirmPassword.error = "Passwords do not match"
        }
        else{
            binding.loading.visibility = View.VISIBLE
            disableViews()

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if(it.isSuccessful){
                    val formatter: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
                    val date: Date = Date()
                    val id: String? = it.result.user?.uid
                    val student: Student = Student(fullName = name, email = email, password = password, userId = id.toString(), batch = batch, joinedDate = formatter.format(date) )
                    database.collection("Students").document(auth.currentUser?.uid.toString()).set(student).addOnCompleteListener {
                        result ->
                        if(result.isSuccessful){
                            val intent: Intent = Intent(this@RegistrationScreen, MainScreen::class.java)
                            val pref: SharedPreferences = getSharedPreferences("login", MODE_PRIVATE)
                            val editor: SharedPreferences.Editor = pref.edit()
                            editor.putBoolean("flag", true)
                            editor.apply()
                            binding.loading.visibility = View.GONE
                            startActivity(intent)
                            finish()
                        }
                    }
                }
                else{
                    Toast.makeText(this, "Something went wrong...", Toast.LENGTH_SHORT).show()
                    binding.loading.visibility = View.GONE
                    enableViews()
                }
            }
        }
    }

    private fun disableViews(){
        binding.name.isEnabled = false
        binding.email.isEnabled = false
        binding.password.isEnabled = false
        binding.confirmPassword.isEnabled = false
        binding.spinner.isEnabled = false
        binding.registerAccountBtn.isEnabled = false
        binding.redirectToLoginScreen.isEnabled = false
        binding.toggleLayout.isEnabled = false
    }

    private fun enableViews(){
        binding.name.isEnabled = true
        binding.email.isEnabled = true
        binding.password.isEnabled = true
        binding.confirmPassword.isEnabled = true
        binding.spinner.isEnabled = true
        binding.registerAccountBtn.isEnabled = true
        binding.redirectToLoginScreen.isEnabled = true
        binding.toggleLayout.isEnabled = true
    }

    private fun initializer(){
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
    }

    private fun setBatchSpinner(){
        val list: Array<String> = arrayOf("BSSE1", "BSSE2", "BSSE3", "BSSE4", "BSSE5", "BSSE6", "BSSE7", "BSSE8", "BSSE9", "BSSE10", "BSSE11", "BSSE12", "BSSE13", "BSSE14", "BSSE15", "BSSE16", "BSSE17", "BSSE18", "BSSE19", "BSSE20")
        val adapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, list)
        binding.spinner.adapter = adapter
    }
}