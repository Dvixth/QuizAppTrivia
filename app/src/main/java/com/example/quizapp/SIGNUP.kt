package com.example.quizapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging

class SIGNUP : AppCompatActivity() {
    lateinit var email: EditText
    lateinit var pass: EditText
    lateinit var confirmpass: EditText
    lateinit var b1: Button
    lateinit var t1: TextView
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)
        b1 = findViewById(R.id.btnsignup)
        t1 = findViewById(R.id.signinbtn)
        email = findViewById(R.id.email)
        pass = findViewById(R.id.pass)
        confirmpass = findViewById(R.id.confirmpass)

        //si l'utilisateur clique sur ce bouton il sera redirigé vers l'activité de connexion
        t1.setOnClickListener() {
            val intent = Intent(this@SIGNUP, LOGIN::class.java)
            startActivity(intent)
        }
        auth = FirebaseAuth.getInstance()


        b1.setOnClickListener() {
            val userEmail = email.text.toString()
            val userPass = pass.text.toString()
            val userConfirmPass = confirmpass.text.toString()
            //verification si les champs sont vides
            if (userEmail.isNotEmpty() && userPass.isNotEmpty() && userConfirmPass.isNotEmpty()) {
                //verification si les mots de passes correspondent
                if (userPass.toString() == userConfirmPass.toString()) {
                    // Création de l'utilisateur avec Firebase Authentication
                    auth.createUserWithEmailAndPassword(userEmail.toString(), userPass.toString())
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                //redirction vers l'activite de connexion
                                val intent = Intent(this, LOGIN::class.java)
                                startActivity(intent)
                                Toast.makeText(this, "Compte crée avec succès", Toast.LENGTH_SHORT).show()
                                // Extraction du nom d'utilisateur à partir de l'adresse e-mail
                                val username = userEmail.substringBefore('@')
                                //enregistrement du joueur dans BDD
                                val database= FirebaseDatabase.getInstance().getReference("game_players")
                                val player = Player(username)
                                val playerID =FirebaseAuth.getInstance().currentUser!!.uid
                                database.child(playerID).setValue(player)
                                //Récupération et sauvegarde du jeton FCM pour les notifications
                                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val fcmToken = task.result
                                        saveFCMTokenToDatabase(fcmToken)
                                    }
                                }
                            } else {
                                //affichage d'un message en cas d'echec de création
                                Toast.makeText(this, "Erreur de création ! réssayer ! ", Toast.LENGTH_SHORT).show()
                            }
                        }
                }else{
                    //affichage de message si les mots de passes ne correspondent
                    Toast.makeText(this, "Les mots de passe ne se correspondent pas ! ", Toast.LENGTH_SHORT).show()
                }
            }else{
                //message pour les champs vides
                Toast.makeText(this, "Aucun Champs vide n'est permis remplissez tout !", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // Sauvegarde du jeton FCM dans la base de données Firebase
    private fun saveFCMTokenToDatabase(fcmToken: String?) {
        if (fcmToken != null) {
            val currentUserID = FirebaseAuth.getInstance().currentUser?.uid
            val databaseReference = FirebaseDatabase.getInstance().getReference("game_players")
            currentUserID?.let {
                databaseReference.child(it).child("fcmToken").setValue(fcmToken)
            }
        }
    }

}