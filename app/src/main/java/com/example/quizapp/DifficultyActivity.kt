package com.example.quizapp


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class DifficultyActivity : AppCompatActivity() {


    //fonction pour le retour au menu
    private fun returnToMainActivity() {
        val intent1 = Intent(this, MainActivity::class.java)
        startActivity(intent1)
        finish()
    }

    //soustraire 100 limcoins a chaque partie et attribution 500 si le joueur n'a pas de solde suffisant
    fun retrievePlayerLimcoins(intent: Intent) {
        //acces au BDD
        val playerID = FirebaseAuth.getInstance().currentUser!!.uid
        val playerRef = FirebaseDatabase.getInstance().getReference("game_players").child(playerID)
        playerRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var hasLIM = snapshot.child("limcoins").getValue(Int::class.java)?: 0
                    if (hasLIM < 100) {
                        // le joueur a le choix soit accepter les limcoins offerts soit non avec la boite de dialogue
                        val alertDialogBuilder = AlertDialog.Builder(this@DifficultyActivity)
                        alertDialogBuilder.apply {
                            setTitle("Alert")
                            setMessage("Not enough coins but we are giving you another 500!!")
                            setPositiveButton("OK") { dialog, _ ->
                                hasLIM += 500
                                snapshot.ref.child("limcoins").setValue(hasLIM)
                                dialog.dismiss()
                                startActivity(intent)
                            }
                            setNegativeButton("Cancel") { dialog, _ ->
                                dialog.dismiss()
                                returnToMainActivity()
                            }
                            setCancelable(false)
                        }
                        val alertDialog = alertDialogBuilder.create()
                        alertDialog.show()
                    } else {
                        //s'il n'accepte pas il va revenir au menu
                        hasLIM -= 100
                        snapshot.ref.child("limcoins").setValue(hasLIM)
                        startActivity(intent)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_difficulty)
        //chaque boutton est attribué a un niveau du jeu
        //lorsque le joueur clique sur un boutton on verifie son solde et apres on demarre
        val button1 = findViewById<Button>(R.id.imageButton)
        val button2 = findViewById<Button>(R.id.imageButton2)
        val button3 = findViewById<Button>(R.id.imageButton3)

        button1.setOnClickListener {
            Log.d("ButtonClicked", "Button clicked")
            val intent = Intent(this, EasyClassiqueActivity::class.java)
            startActivity(intent)
            retrievePlayerLimcoins(intent)
        }

        button2.setOnClickListener {
            val intent = Intent(this, MediumClassiqueActivity::class.java)
            startActivity(intent)
            retrievePlayerLimcoins(intent)
        }
        button3.setOnClickListener {
            val intent = Intent(this, HardClassiqueActivity::class.java)
            startActivity(intent)
            retrievePlayerLimcoins(intent)
        }
    }



}