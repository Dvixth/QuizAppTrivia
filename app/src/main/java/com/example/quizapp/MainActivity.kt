package com.example.quizapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //selon le bouton appuiyé l'utilisateur peut soit accéder le mode classique, soit le mode défi ou consulter le tableau de classement
        val button1 = findViewById<Button>(R.id.button)
        val button2 = findViewById<Button>(R.id.button2)
        val button3 = findViewById<Button>(R.id.button3)

        button1.setOnClickListener {
            val intent = Intent(this@MainActivity,DifficultyActivity::class.java)
            startActivity(intent)
        }

        button2.setOnClickListener{
            val text = "Prochainement ! "
            val duration = Toast.LENGTH_SHORT

            val toast = Toast.makeText(this, text, duration) // in Activity
            toast.show()
        }

        button3.setOnClickListener {
            val intent = Intent(this, LeaderBoard::class.java)
            startActivity(intent)
        }
    }
    }
