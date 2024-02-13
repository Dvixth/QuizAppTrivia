package com.example.quizapp

data class Question(
    val question: String,
    val correctAnswer: String,
    val incorrectAnswers: List<String> = emptyList()
)
{
    // Calcul de l'index de la réponse correcte
    val correctAnswerIndex: Int
        get() = incorrectAnswers.size
}
