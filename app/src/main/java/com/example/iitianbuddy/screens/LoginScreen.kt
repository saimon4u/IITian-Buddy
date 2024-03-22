package com.example.iitianbuddy.screens

import android.app.ActivityOptions
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Pair
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.iitianbuddy.databinding.ActivityLoginScreenBinding
import com.google.firebase.auth.FirebaseAuth

class LoginScreen : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginScreenBinding
    private val emailPattern: String = "^bsse[0-9]+@iit\\.du\\.ac\\.bd"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializer()

        binding.redirectToRegistrationScreen.setOnClickListener {
            val intent: Intent = Intent(this@LoginScreen, RegistrationScreen::class.java)
            val pairs: Array<Pair<View, String>?> = arrayOfNulls(8)

            pairs[0] = Pair<View, String>(binding.logo, "logo")
            pairs[1] = Pair<View, String>(binding.greetings, "text")
            pairs[2] = Pair<View, String>(binding.pageToggle, "toggle")
            pairs[3] = Pair<View, String>(binding.email, "email")
            pairs[4] = Pair<View, String>(binding.password, "password")
            pairs[5] = Pair<View, String>(binding.loginBtn, "btn")
            pairs[6] = Pair<View, String>(binding.options, "option")
            pairs[7] = Pair<View, String>(binding.optionImage, "option_image")

            val options1 = ActivityOptions.makeSceneTransitionAnimation(this@LoginScreen, *pairs)
            startActivity(intent, options1.toBundle())
            finish()
        }


        binding.loginBtn.setOnClickListener {
            val loginEmail: String = binding.email.editText?.text.toString()
            val loginPassword: String = binding.password.editText?.text.toString()

            if(TextUtils.isEmpty(loginEmail)){
                binding.email.error = "Email cannot be empty"
            }
            else if(TextUtils.isEmpty(loginPassword)){
                binding.password.error = "Password cannot be empty"
            }
            else if (!loginEmail.matches(emailPattern.toRegex())) {
                binding.email.error = "Enter a valid type of BSSE email"
            }
            else{
                binding.loading.visibility = View.VISIBLE
                disableViews()
                auth.signInWithEmailAndPassword(loginEmail, loginPassword).addOnCompleteListener {
                    if(it.isSuccessful){
                        val intent: Intent = Intent(this@LoginScreen, MainScreen::class.java)
                        val pref: SharedPreferences = getSharedPreferences("login", MODE_PRIVATE)
                        val editor: SharedPreferences.Editor = pref.edit()
                        editor.putBoolean("flag", true)
                        editor.apply()
                        binding.loading.visibility = View.GONE
                        startActivity(intent)
                        finish()
                    }
                    else{
                        Toast.makeText(this, "Something went wrong ...", Toast.LENGTH_SHORT).show()
                        binding.loading.visibility = View.GONE
                        enableViews()
                    }
                }
            }
        }
    }

    private fun disableViews(){
        binding.loginBtn.isEnabled = false
        binding.email.isEnabled = false
        binding.password.isEnabled = false
        binding.pageToggle.isEnabled = false
    }

    private fun enableViews(){
        binding.loginBtn.isEnabled = true
        binding.email.isEnabled = true
        binding.password.isEnabled = true
        binding.pageToggle.isEnabled = true
    }

    private fun initializer(){
        auth = FirebaseAuth.getInstance()
    }
}