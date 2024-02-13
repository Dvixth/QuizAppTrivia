package com.example.quizapp

import android.annotation.SuppressLint
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LeaderBoard : AppCompatActivity() {

    //variable pour le classement
    var rank:Int =1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leader_board)
        val tableLayout: TableLayout = findViewById(R.id.tableLayout)
        val typeface = ResourcesCompat.getFont(this, R.font.bukhari_script)


        //recuperation des donnees du joueur et l'affichage dans un tableau
        @SuppressLint("SetTextI18n")
        fun recupererInformationPlayer() {
            try{
                //la  premiere ligne du tableau qui contient les titres
                val Titlerow = TableRow(this@LeaderBoard)
                Titlerow.setPadding(8, 8, 8, 8)

                val titleRank = TextView(this@LeaderBoard)
                titleRank.text = "Rank"
                titleRank.setTypeface(typeface, Typeface.BOLD)
                titleRank.textSize=18f

                val titleUser = TextView(this@LeaderBoard)
                titleUser.text = "Username"
                titleUser.setTypeface(typeface)
                titleUser.textSize=18f

                val titleTemps = TextView(this@LeaderBoard)
                titleTemps.text = "Time"
                titleTemps.setTypeface(typeface)
                titleTemps.textSize=18f

                val titleScore = TextView(this@LeaderBoard)
                titleScore.text = "Score"
                titleScore.setTypeface(typeface)
                titleScore.textSize=18f

                val marginLayoutParams = TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    25f
                )

                val marginLayoutParams2 = TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    20f
                )

                val marginLayoutParams3 = TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    15f
                )

                Titlerow.addView(titleRank, marginLayoutParams2)
                Titlerow.addView(titleUser, marginLayoutParams)
                Titlerow.addView(titleTemps, marginLayoutParams3)
                Titlerow.addView(titleScore, marginLayoutParams3)

                tableLayout.addView(Titlerow)
            }catch(e: Exception){
                Log.e("LeaderBoard", "Error: ${e.message}")
            }

            val playerScores = mutableListOf<Player>()
            //recureation des donnees de la BDD
            val playerRef = FirebaseDatabase.getInstance().getReference("game_players")
            playerRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //parcours de la liste de joueurs dans la BDD
                    for (playerSnapshot in snapshot.children) {
                        if (playerSnapshot.exists()) {
                            val username = playerSnapshot.child("username").getValue(String::class.java) ?: ""
                            val time = playerSnapshot.child("time").getValue(Long::class.java) ?: 0
                            val score = playerSnapshot.child("score").getValue(Int::class.java) ?: 0
                            val playerScore= Player(username,500, score, time)
                            playerScores.add(playerScore)
                        }
                    }
                    //le tri de la liste selon le temps et le score
                    playerScores.sortWith(compareBy<Player> { -it.score }.thenBy { it.time })

                    //affichage dans le tableau
                    for(player in playerScores){
                        val row = TableRow(this@LeaderBoard)
                        val layoutParams = TableRow.LayoutParams(
                            TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                        )
                        layoutParams.setMargins(0, 0, 0, 16)
                        row.layoutParams = layoutParams
                        row.setPadding(8, 8, 8, 8)

                        val rankF = TextView(this@LeaderBoard)
                        rankF.text = "$rank"
                        rankF.setTypeface(typeface, Typeface.BOLD)
                        rankF.textSize=18f

                        val user = TextView(this@LeaderBoard)
                        user.text = "${player.username}"
                        user.setTypeface(typeface)
                        user.textSize=18f

                        val temps = TextView(this@LeaderBoard)
                        temps.text = "${player.time}"
                        temps.setTypeface(typeface)
                        temps.textSize=18f

                        val scoreF = TextView(this@LeaderBoard)
                        scoreF.text = "${player.score}"
                        scoreF.setTypeface(typeface)
                        scoreF.textSize=18f

                        val marginLayoutParams = TableRow.LayoutParams(
                            0,
                            TableRow.LayoutParams.WRAP_CONTENT,
                            25f
                        )

                        val marginLayoutParams2 = TableRow.LayoutParams(
                            0,
                            TableRow.LayoutParams.WRAP_CONTENT,
                            20f
                        )

                        val marginLayoutParams3 = TableRow.LayoutParams(
                            0,
                            TableRow.LayoutParams.WRAP_CONTENT,
                            15f
                        )


                        row.addView(rankF,marginLayoutParams2)
                        row.addView(user, marginLayoutParams)
                        row.addView(temps, marginLayoutParams3)
                        row.addView(scoreF, marginLayoutParams3)

                        tableLayout.addView(row)
                        //incrementation du classement
                        rank++                    }

                }
                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        }
        //appel de la fonction pour l'affichage
        recupererInformationPlayer()
    }
}