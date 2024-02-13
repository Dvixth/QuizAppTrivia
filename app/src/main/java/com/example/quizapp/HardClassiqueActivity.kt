

// pour cette acivite c'est la meme demarche pour l'activite easy juste avec une
//imporation des questions de niveau difficile de l'API et un temps< temps de
//l'activités medium et easy


package com.example.quizapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import android.text.Html
import androidx.annotation.RequiresApi

class HardClassiqueActivity : AppCompatActivity() {
    // Définition des variables de l'interface
    private lateinit var timerTextView: TextView
    private lateinit var progressTextView: TextView
    private lateinit var questionTextView: TextView
    private lateinit var answerButton1: Button
    private lateinit var answerButton2: Button
    private lateinit var answerButton3: Button
    private lateinit var answerButton4: Button


    // Méthode pour décoder les entités HTML
    @RequiresApi(Build.VERSION_CODES.N)
    fun decodeHtmlEntities(text: String): String {
        return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY).toString()
    }
    // Variable pour stocker le nombre de limcoins
    var lim=0
    // Méthode pour récupérer le nombre de limcoins du joueur depuis Firebase
    fun retrievePlayerLimcoins() {
        val playerID = FirebaseAuth.getInstance().currentUser!!.uid
        val playerRef = FirebaseDatabase.getInstance().getReference("game_players").child(playerID)
        playerRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    lim = snapshot.child("limcoins").getValue(Int::class.java)?:0
                    val limCoinsTextView = findViewById<TextView>(R.id.LimCoins)
                    limCoinsTextView.text = "$lim"
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    //déclaration des variables concernant le Timer
    private lateinit var countDownTimer: CountDownTimer
    private var timeRemainingMillis: Long = 0
    private val totalTimeMillis: Long = 60000 // Temps initial pour chaque question en millisecondes
    private var isQuizInProgress = false

    // Liste de questions pour le quiz
    private var questions = ArrayList<Question>() //déclarations d'un tableau de questions
    private var currentQuestionIndex = 0

    // ViewModel pour le quiz
    private lateinit var quizViewModel: QuizViewModel

    // Interface de l'API trivia
    interface TriviaApiService { //Interface de l'API trivia
        @GET("api.php?amount=50&difficulty=hard&type=multiple")
        fun getQuestions(@Query("amount") amount: Int): Call<TriviaResponse>
    }

    // Réponse de l'API trivia
    data class TriviaResponse(
        val results: List<Question>
    )

    class QuizViewModel : ViewModel() {
        val timerValue = MutableLiveData<Long>()
    }

    // Initialisation de l'API trivia
    private val triviaApiService: TriviaApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://opentdb.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TriviaApiService::class.java)
    }

    //Les questions
    data class Question(
        val question: String,
        @SerializedName("correct_answer") val correctAnswer: String,
        @SerializedName("incorrect_answers") val incorrectAnswers: List<String>
    )


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        retrievePlayerLimcoins()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hard_classique)
        // Initialisation des éléments de l'interface utilisateur
        val limCoinsTextView = findViewById<TextView>(R.id.LimCoins)
        limCoinsTextView.text = "$lim"
        timerTextView = findViewById(R.id.timerTextView)
        progressTextView = findViewById(R.id.progressTextView)
        questionTextView = findViewById(R.id.questionTextView)
        answerButton1 = findViewById(R.id.answerButton1)
        answerButton2 = findViewById(R.id.answerButton2)
        answerButton3 = findViewById(R.id.answerButton3)
        answerButton4 = findViewById(R.id.answerButton4)
        timeRemainingMillis = totalTimeMillis

        // Initialisation du ViewModel pour le quiz
        quizViewModel = ViewModelProvider(this).get(QuizViewModel::class.java)
        InitialiserCompteArebours()

        RecupererQuestion()// Récupérer les questions de l'API
        setupAnswerButtonListeners()// Mettre en place les écouteurs pour les boutons de réponse
    }

    // Mettre en place les écouteurs pour les boutons de réponse
    @RequiresApi(Build.VERSION_CODES.N)
    private fun setupAnswerButtonListeners() {
        val answerButtonClickListener = View.OnClickListener { view ->
            if (questions.isNotEmpty()) {
                val selectedAnswer = (view as Button).text.toString()
                //recuperation de la réponse de la liste et la decoder
                val correctAnswer = decodeHtmlEntities(questions[currentQuestionIndex].correctAnswer)
                //ce message dans le log pour connaitre la réponse correcte
                Log.d("AnswerSelection", "Selected Answer: $selectedAnswer, Correct Answer: $correctAnswer")
                if (selectedAnswer == questions[currentQuestionIndex].correctAnswer) {
                    showNextQuestion()// Affiche la prochaine question si la réponse est correctes
                } else {//Dans le cas le joueur se trompe on retourne le score et le temps et on revient au menu
                    returnToMainActivity()
                    onDestroy()
                    Toast.makeText(this, "FALSE", Toast.LENGTH_SHORT).show()
                    val time=(totalTimeMillis-timeRemainingMillis)/1000
                    val score=currentQuestionIndex
                    updateFin(time,score)// mise à jour le temps et le score dans Firebase
                }
            }
        }

        // Association les écouteurs aux boutons de réponse
        answerButton1.setOnClickListener(answerButtonClickListener)
        answerButton2.setOnClickListener(answerButtonClickListener)
        answerButton3.setOnClickListener(answerButtonClickListener)
        answerButton4.setOnClickListener(answerButtonClickListener)
    }

    //Mise  jour des limcoins dans la base de données
    fun updateLim(){
        val playerID = FirebaseAuth.getInstance().currentUser!!.uid
        val playerRef = FirebaseDatabase.getInstance().getReference("game_players").child(playerID)
        playerRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val limCoinsTextView = findViewById<TextView>(R.id.LimCoins)
                    snapshot.ref.child("limcoins").setValue(lim)
                    limCoinsTextView.text = "$lim"
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }


    //Fonction qui met a jour le score(nmbre de questions repondus) et le temps dans la base de données
    fun updateFin(time: Long,score: Int){
        val playerID = FirebaseAuth.getInstance().currentUser!!.uid //Récuperation de l'ID de l'utilisateur
        val playerRef = FirebaseDatabase.getInstance().getReference("game_players").child(playerID) //L'instance du joueur dans la BDD
        playerRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.ref.child("score").setValue(score)
                    snapshot.ref.child("time").setValue(time)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    private fun showNextQuestion() {
        //  Affiche la prochaine question du quiz
        currentQuestionIndex++

        //dans le cas ou le joueur repond au 3 questions correctes on ajoute 30 a son limcoins
        if((currentQuestionIndex)%3==0){
            lim=lim+30
            updateLim()
        }
        // si le quiz est terminé
        if (currentQuestionIndex >= questions.size) {
            isQuizInProgress = false
            //on ajoute le bonus eton fait la mise ajour dans la base de donnes pour les limcoins le score et le temps
            lim=lim+200
            updateLim()
            val time=(totalTimeMillis-timeRemainingMillis)/1000
            val score=currentQuestionIndex
            updateFin(time,score)
            //Intent qui contient le score et le temps pour l'activité du score
            val intent = Intent(this@HardClassiqueActivity,ScoreActivity::class.java)
            intent.putExtra("TEMPS",totalTimeMillis-timeRemainingMillis)
            intent.putExtra("SCORE",currentQuestionIndex)
            startActivity(intent)
            finish()
        } else {
            // Affiche la prochaine question du quiz
            AfficherQues(questions[currentQuestionIndex])
            Log.d("CorrectAnswer", "Correct answer for question ${currentQuestionIndex + 1}: ${questions[currentQuestionIndex].correctAnswer}")
        }
    }


    //affichage des questions
    private fun AfficherQues(question: Question) {
        // Decodage du texte de la question recuperee et l'affecte à questionTextView
        val decodedQuestionText = HtmlCompat.fromHtml(question.question, HtmlCompat.FROM_HTML_MODE_LEGACY)
        questionTextView.text = decodedQuestionText

        //creation de la liste des questions aleatoires
        val shuffledAnswers = ArrayList(question.incorrectAnswers)
        shuffledAnswers.add(question.correctAnswer)
        shuffledAnswers.shuffle()

        //affichage des reponses aleatoires sur les bouttons correspondantes
        answerButton1.text = HtmlCompat.fromHtml(shuffledAnswers.getOrNull(0) ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY)
        answerButton2.text = HtmlCompat.fromHtml(shuffledAnswers.getOrNull(1) ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY)
        answerButton3.text = HtmlCompat.fromHtml(shuffledAnswers.getOrNull(2) ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY)
        answerButton4.text = HtmlCompat.fromHtml(shuffledAnswers.getOrNull(3) ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY)

        //attribution d'un tag au bouttons pour identifier les reponses
        answerButton1.tag = 0
        answerButton2.tag = 1
        answerButton3.tag = 2
        answerButton4.tag = 3

        // Affiche la progression du quiz
        progressTextView.text = getString(R.string.question_progress, currentQuestionIndex + 1, questions.size)
    }

    private fun RecupererQuestion() {
        isQuizInProgress = true
        //appel a l'API pour recuperer les questions
        triviaApiService.getQuestions(11).enqueue(object : Callback<TriviaResponse> {
            override fun onResponse(call: Call<TriviaResponse>, response: Response<TriviaResponse>) {
                //verification si la reponse de l'API est reussie
                if (response.isSuccessful) {
                    //recuperation des qestions
                    val fetchedQuestions = response.body()?.results
                    if (fetchedQuestions != null) {
                        traiterQuestions(fetchedQuestions)
                    }
                }
            }

            override fun onFailure(call: Call<TriviaResponse>, t: Throwable) {
            }
        })
    }

    //fonction pour traiter les questions recuperees par l'API
    private fun traiterQuestions(fetchedQuestions: List<Question>) {
        //verification si les questions sont presentes
        if (fetchedQuestions.isNotEmpty()) {
            //stockage des questions dans la liste
            questions = ArrayList(fetchedQuestions)
            //affichage de la question
            AfficherQues(questions[currentQuestionIndex])
            //enregistrement de la reponse dans un message log
            Log.d("CorrectAnswer", "Correct answer for question ${currentQuestionIndex + 1}: ${questions[currentQuestionIndex].correctAnswer}")
        }
    }

    //compte a rebours
    private fun InitialiserCompteArebours() {
        countDownTimer = object : CountDownTimer(timeRemainingMillis, 1000) {
            // a chaque tic mise a jour du temps restant et actualisation de l'affichage
            override fun onTick(millisUntilFinished: Long) {
                timeRemainingMillis = millisUntilFinished
                updateTimerText()
                // Mise a jor la valeur du timer dans quizViewModel s'il est initialisé
                if (::quizViewModel.isInitialized) {
                    quizViewModel.timerValue.value = millisUntilFinished
                }
            }

            override fun onFinish() {
                // Appel à returnToMainActivity uniquement si le quiz est en cours
                if (isQuizInProgress) {
                    returnToMainActivity()
                }
            }
        }
        // Démarre le compte à rebours
        countDownTimer.start()
    }

    //mise a jour du timer
    @SuppressLint("StringFormatInvalid")
    private fun updateTimerText() {
        timerTextView.text = getString(R.string.timer, timeRemainingMillis / 1000)
    }

    //retour a l'activite principale a travers un intent
    private fun returnToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun finish() {
        super.finish()
        countDownTimer.cancel()
    }
}


