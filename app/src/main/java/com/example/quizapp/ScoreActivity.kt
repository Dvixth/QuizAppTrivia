package com.example.quizapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class ScoreActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)
        val t1: TextView = findViewById(R.id.timeTaken)
        val t2: TextView = findViewById(R.id.score)
        val b1: Button = findViewById(R.id.menu)
        val b2: Button = findViewById(R.id.leaderboard)

        //recuperation du score et temps de l'intent de chaque activité
        val TEMPS=intent.getLongExtra("TEMPS",0)
        val SCORE=intent.getIntExtra("SCORE",0)

        val min=(TEMPS/1000)/60
        val sec=(TEMPS/1000)%60

        val time = String.format("%02d:%02d", min, sec)
        t1.text="Time Taken: $time"
        t2.text="Your Score: $SCORE"

        //bouttons pour soit retourner au menu soit voir la table de scores a l'aide des boutons

        b1.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        b2.setOnClickListener{
            val intent = Intent(this, LeaderBoard::class.java)
            startActivity(intent)
        }
    }
}