package snage.up.laaco

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.zaglushkaproject.R
import kotlin.random.Random

class QuizGameActivity : AppCompatActivity() {

    private lateinit var questionText: TextView
    private lateinit var answerOptionsText: TextView
    private lateinit var answer1Button: Button
    private lateinit var answer2Button: Button
    private lateinit var answer3Button: Button
    private lateinit var answer4Button: Button
    private lateinit var scoreText: TextView
    private lateinit var livesText: TextView
    private lateinit var timerText: TextView
    private lateinit var restartButton: Button

    private val questions = QuestionList().questions
    private val usedIndexes: MutableList<Int> = mutableListOf()

    private var currentQuestionIndex = getRandomQuestionIndex()
    private lateinit var currentQuestion: Question
    private var score = 0
    private var lives = 3

    private lateinit var timer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.quiz_game_layout)

        questionText = findViewById(R.id.questionText)
        answerOptionsText = findViewById(R.id.answerOptionsText)
        answer1Button = findViewById(R.id.answer1Button)
        answer2Button = findViewById(R.id.answer2Button)
        answer3Button = findViewById(R.id.answer3Button)
        answer4Button = findViewById(R.id.answer4Button)
        scoreText = findViewById(R.id.scoreText)
        livesText = findViewById(R.id.livesText)
        timerText = findViewById(R.id.timerText)
        restartButton = findViewById(R.id.restartButton)

        restartButton.setOnClickListener {
            restartGame()
        }

        updateQuestion()

        answer1Button.setOnClickListener { checkAnswer(1) }
        answer2Button.setOnClickListener { checkAnswer(2) }
        answer3Button.setOnClickListener { checkAnswer(3) }
        answer4Button.setOnClickListener { checkAnswer(4) }

        startTimer()
    }

    private fun updateQuestion() {
        currentQuestion = questions[Random.nextInt(0, questions.size)]
        questionText.text = currentQuestion.question

        val answerOptions = "A. ${currentQuestion.answer1}\nB. ${currentQuestion.answer2}\nC. ${currentQuestion.answer3}\nD. ${currentQuestion.answer4}"
        answerOptionsText.text = answerOptions
    }

    private fun checkAnswer(selectedAnswer: Int) {
        timer.cancel()
        if (selectedAnswer == currentQuestion.correctAnswer) {
            score++
            updateScoreLive()
        } else {
            lives--
            updateScoreLive()
            if (lives == 0) {
                showResult()
                return
            }
        }

        currentQuestionIndex = getRandomQuestionIndex()
        if (currentQuestionIndex < questions.size) {
            updateQuestion()
            startTimer()
        } else {
            showResult()
        }
    }

    private fun showResult() {
        questionText.visibility = View.GONE
        answer1Button.visibility = View.GONE
        answer2Button.visibility = View.GONE
        answer3Button.visibility = View.GONE
        answer4Button.visibility = View.GONE
        restartButton.visibility = View.VISIBLE
        answerOptionsText.visibility = View.GONE
        timerText.visibility = View.GONE
        timer.cancel()

        updateScoreLive()
    }

    private fun startTimer() {
        timer = object : CountDownTimer(15000, 1500) {
            override fun onTick(millisUntilFinished: Long) {
                timerText.text = "Time left: ${millisUntilFinished / 1000}"
            }
            override fun onFinish() {
                checkAnswer(0)
            }
        }
        timer.start()
    }

    private fun restartGame() {
        currentQuestionIndex = 0
        score = 0
        lives = 3
        updateQuestion()

        timerText.visibility = View.VISIBLE
        questionText.visibility = View.VISIBLE
        answer1Button.visibility = View.VISIBLE
        answer2Button.visibility = View.VISIBLE
        answer3Button.visibility = View.VISIBLE
        answer4Button.visibility = View.VISIBLE
        restartButton.visibility = View.GONE
        answerOptionsText.visibility = View.VISIBLE
        updateScoreLive()
        startTimer()
    }

    private fun updateScoreLive() {
        scoreText.text = "Score: $score"
        livesText.text = "Lives: $lives"
    }
    fun getRandomQuestionIndex(): Int {
        val availableIndices = questions.indices.filter { it !in usedIndexes }

        if (availableIndices.isEmpty()) {
            usedIndexes.clear()
        }

        val randomIndex = availableIndices.random()
        usedIndexes.add(randomIndex)

        return randomIndex
    }
}

