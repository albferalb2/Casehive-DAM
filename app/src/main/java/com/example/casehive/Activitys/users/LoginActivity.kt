package com.example.casehive.Activitys.users

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.casehive.Activitys.MainActivity
import com.example.casehive.R
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEdit: EditText
    private lateinit var passwordEdit: EditText
    private lateinit var loginBtn: Button
    private lateinit var registerBtn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        emailEdit = findViewById(R.id.emailEditText)
        passwordEdit = findViewById(R.id.passwordEditText)
        loginBtn = findViewById(R.id.loginButton)
        registerBtn = findViewById(R.id.goToRegisterButton)

        loginBtn.setOnClickListener {
            val email = emailEdit.text.toString()
            val password = passwordEdit.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Usuario no encontrado. Redirigiendo a registro...", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, RegisterActivity::class.java)
                        intent.putExtra("email", email)
                        intent.putExtra("password", password)
                        startActivity(intent)
                    }
            } else {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        registerBtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun setLocale(language:String){
        val local= Locale(language)
        Locale.setDefault(local)
        val config=resources.configuration
        config.setLocale(local)
        createConfigurationContext(config)
        resources.updateConfiguration(config,resources.displayMetrics)
        recreate()

    }
}
