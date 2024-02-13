package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

//l'activite de connexion
class LOGIN : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var email: EditText
    lateinit var pass: EditText
    lateinit var b1: Button
    lateinit var t1: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        b1 = findViewById(R.id.buttonsignin)
        t1 = findViewById(R.id.signupbtn)
        email = findViewById(R.id.email)
        pass = findViewById(R.id.pass)

        //si l'utilisateur clique sur ce bouton il sera redige vers l'activité de creation de compte
        t1.setOnClickListener(){
            val intent = Intent(this@LOGIN,SIGNUP::class.java)
            startActivity(intent)
        }

        //verification de mot de passe et addresse mail si il existent au non avec le methodes de firebase
        auth = FirebaseAuth.getInstance()
        b1.setOnClickListener(){
            val userEmail = email.text.toString()
            val userPass = pass.text.toString()
            if (userEmail.isNotEmpty() && userPass.isNotEmpty()) {
                auth.signInWithEmailAndPassword(userEmail.toString(), userPass.toString())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            Toast.makeText(this, "Connecté avec succès !", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(this, "Echec de la connexion !", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            }else{
                Toast.makeText(this, "Aucun Champs vide n'est permis remplissez tout !", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
