package com.example.quizapp

data class Player(
    val username: String? = null,
    var limcoins : Int = 500,
    var score: Int=0,
    var time: Long=0,
    var token: String? = null
)