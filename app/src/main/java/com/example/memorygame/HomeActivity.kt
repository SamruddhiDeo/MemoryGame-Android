package com.example.memorygame

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import java.util.*
import kotlin.collections.ArrayList

class HomeActivity : AppCompatActivity() {
    // Initializations
    private lateinit var moves: TextView
    private lateinit var time: TextView
    private lateinit var highestMoves: TextView
    private lateinit var highestTime: TextView
    private lateinit var pauseBtn: ImageView
    private lateinit var btn1: AppCompatButton
    private lateinit var btn2: AppCompatButton
    private lateinit var btn3: AppCompatButton
    private lateinit var btn4: AppCompatButton
    private lateinit var btn5: AppCompatButton
    private lateinit var btn6: AppCompatButton
    private lateinit var btn7: AppCompatButton
    private lateinit var btn8: AppCompatButton
    private lateinit var btn9: AppCompatButton
    private lateinit var btn10: AppCompatButton
    private lateinit var btn11: AppCompatButton
    private lateinit var btn12: AppCompatButton
    private lateinit var openBtn1: AppCompatButton
    private lateinit var openBtn2: AppCompatButton
    private val arrBtnImages = arrayOfNulls<String>(12)
    private val openCards = arrayOfNulls<String>(2)
    private val cardsDisableListeners = ArrayList<String>()
    private var stopCounting = false
    private var count = 0
    private var movesCount = 0
    private var seconds = 0
    private var minutes = 0
    private var noOfMatches = 0
    private var secondsToShow = 0
    private var openBtnId1 = 0
    private var openBtnId2 = 0
    private var highestMovesStored = 0
    private var highestTimeStored = 0
    private var isHighScoreBroke = false
    private var highestMinutesStored = 0
    private var highestSecondsStoredToShow = 0
    private var removeListener = false
    private lateinit var iMain: Intent
    private lateinit var iHome: Intent
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Declarations
        btn1 = findViewById(R.id.btn1)
        btn2 = findViewById(R.id.btn2)
        btn3 = findViewById(R.id.btn3)
        btn4 = findViewById(R.id.btn4)
        btn5 = findViewById(R.id.btn5)
        btn6 = findViewById(R.id.btn6)
        btn7 = findViewById(R.id.btn7)
        btn8 = findViewById(R.id.btn8)
        btn9 = findViewById(R.id.btn9)
        btn10 = findViewById(R.id.btn10)
        btn11 = findViewById(R.id.btn11)
        btn12 = findViewById(R.id.btn12)
        pauseBtn = findViewById(R.id.pauseBtn)
        moves = findViewById(R.id.moves)
        time = findViewById(R.id.time)
        highestMoves = findViewById(R.id.highestMoves)
        highestTime = findViewById(R.id.highestTime)

        // Intent to go to home
        iMain = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY)
        }
        iHome = Intent(intent).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY)
        }

        // To set highest score
        setHighestScore()

        // Set background images of cards randomly
        val images = ArrayList(Arrays.asList("img1", "img1", "img2", "img2", "img3", "img3", "img4", "img4", "img5", "img5", "img6", "img6"))
        var min = 0
        var max = 11
        var random: Int
        for (i in arrBtnImages.indices) {
            random = (Math.random() * ((max - min) + 1)).toInt()
            arrBtnImages[i] = images[random]
            images.removeAt(random)
            max--
        }

        // Function for pause button
        pauseBtn.setOnClickListener {
            pauseBtn.setBackgroundResource(R.drawable.baseline_play_circle_24)
            stopCounting = true
            openPauseDialog()
        }
    }

    // Overriding on back pressed method
    override fun onBackPressed() {
        openPauseDialog()
    }

    // Function for card click
    fun btnClick(v: View) {
        if (count == 0) {
            startTimer()  // Count 0 means clicked first time
        }
        if (!removeListener && !cardsDisableListeners.contains(v.tag))

        {
            val btn = v as AppCompatButton
            // Change background
            btn.text = ""
            val btnId = v.tag as String
            val idIndexSubstring = btnId.substring(3).toInt()
            val idIndex = idIndexSubstring - 1
            val resId = resources.getIdentifier(arrBtnImages[idIndex], "drawable", packageName)
            btn.setBackground(resources.getDrawable(resId, null))

            // Check match
            count++
            if (count % 2 != 0) {
                openCards[0] = btnId
            } else {
                if (openCards[0] == btnId) {
                    count--
                } else {
                    openCards[1] = btnId
                    removeListener = true
                    incrementMoves()
                    checkCardMatch()
                }
            }
        }
    }

    // Function to check card match or not
    private fun checkCardMatch() {
        if (arrBtnImages[openCards[0]!!.substring(3).toInt() - 1] != arrBtnImages[openCards[1]!!.substring(3).toInt() - 1]) {
            // To set background back to pink if not matched
            openBtnId1 = resources.getIdentifier(openCards[0], "id", packageName)
            openBtnId2 = resources.getIdentifier(openCards[1], "id", packageName)
            openBtn1 = findViewById(openBtnId1)
            openBtn2 = findViewById(openBtnId2)
            Handler().postDelayed({
                openBtn1.setBackgroundResource(R.drawable.custom_btn)
                openBtn2.setBackgroundResource(R.drawable.custom_btn)
                removeListener = false
            }, 1300)
        } else {
            cardsDisableListeners.add(openCards[0]!!)
            cardsDisableListeners.add(openCards[1]!!)
            noOfMatches++   // If match
            removeListener = false
        }

        // Check win or not
        if (noOfMatches == 6) {
            removeListener = true
            isHighScoreBroke = updateHighestScore()
            stopCounting = true
            openWinDialog()
        }
    }

    // Function to increment moves count
    private fun incrementMoves() {
        if (!stopCounting) {
            movesCount++
            moves.text = "Moves : $movesCount"
        }
    }

    // Function for time increasing
    private fun startTimer() {
        Handler().postDelayed({
            if (!stopCounting) {
                seconds++
            }
            updateTimer()
            startTimer()
        }, 1000)
    }

    private fun updateTimer() {
        if (seconds >= 60) {
            minutes = seconds / 60
            secondsToShow = seconds % 60
            time.text = if ((seconds > minutes * 60) && (seconds < minutes * 60 + 10) || (seconds % 60 == 0)) {
                "Time : $minutes:0$secondsToShow"
            } else {
                "Time : $minutes:$secondsToShow"
            }
        } else {
            time.text = if (seconds < 10) {
                "Time : $minutes:0$seconds"
            } else {
                "Time : $minutes:$seconds"
            }
        }
    }

    private fun openWinDialog() {
        val winDialog = Dialog(this)
        winDialog.setContentView(R.layout.custom_win_dialog)
        winDialog.setCanceledOnTouchOutside(false)

        val window: Window? = winDialog.window
        window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))

        val winDialogMoves: TextView = winDialog.findViewById(R.id.winDialogMoves)
        val winDialogTime: TextView = winDialog.findViewById(R.id.winDialogTime)
        val winDialogRestart: ImageView = winDialog.findViewById(R.id.winDialogRestart)
        val winDialogHome: ImageView = winDialog.findViewById(R.id.winDialogHome)
        val newHighScore: TextView = winDialog.findViewById(R.id.newHighScore)

        newHighScore.visibility = if (isHighScoreBroke) View.VISIBLE else View.INVISIBLE

        winDialogMoves.text = moves.text
        winDialogTime.text = time.text

        winDialogRestart.setOnClickListener {
            winDialog.dismiss()
            startActivity(iHome)
            restartGame()
        }

        winDialogHome.setOnClickListener {
            startActivity(iMain)
            restartGame()
        }

        winDialog.show()
    }

    private fun openPauseDialog() {
        val pauseDialog = Dialog(this)
        pauseDialog.setContentView(R.layout.custom_pause_dialog)
        pauseDialog.setCanceledOnTouchOutside(false)

        val window: Window? = pauseDialog.window
        window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))

        val resumeBtn: AppCompatButton = pauseDialog.findViewById(R.id.resumeBtn)
        val restartBtn: AppCompatButton = pauseDialog.findViewById(R.id.restartBtn)
        val quitBtn: AppCompatButton = pauseDialog.findViewById(R.id.quitBtn)

        resumeBtn.setOnClickListener {
            pauseDialog.dismiss()
            pauseBtn.setBackgroundResource(R.drawable.baseline_pause_circle_24)
            stopCounting = false
        }

        restartBtn.setOnClickListener {
            startActivity(iHome)
            restartGame()
        }

        quitBtn.setOnClickListener {
            startActivity(iMain)
            restartGame()
        }

        pauseDialog.show()
    }

    private fun restartGame() {
        isHighScoreBroke = false
        count = 0
        noOfMatches = 0
        seconds = 0
        minutes = 0
        movesCount = 0
        removeListener = false
        cardsDisableListeners.clear()
    }

    private fun setHighestScore() {
        highestSecondsStoredToShow = 0
        preferences = getSharedPreferences("HighestScore", MODE_PRIVATE)

        highestMovesStored = preferences.getInt("highest moves", 0)
        highestTimeStored = preferences.getInt("highest time", 0)
        highestMinutesStored = highestTimeStored / 60
        highestSecondsStoredToShow = highestTimeStored % 60
        highestMoves.text = "Moves : $highestMovesStored"
        highestTime.text = if ((highestTimeStored > highestMinutesStored * 60) && (highestTimeStored < highestMinutesStored * 60 + 10) || highestTimeStored % 60 == 0) {
            "Time : $highestMinutesStored:0$highestSecondsStoredToShow"
        } else {
            "Time : $highestMinutesStored:$highestSecondsStoredToShow"
        }
    }

    private fun updateHighestScore(): Boolean {
        preferences = getSharedPreferences("HighestScore", MODE_PRIVATE)
        return if ((movesCount <= highestMovesStored) || (seconds <= highestTimeStored) || ((highestTimeStored == 0) && (highestMovesStored == 0))) {
            val editor: SharedPreferences.Editor = preferences.edit()
            editor.putInt("highest moves", movesCount)
            editor.putInt("highest time", seconds)
            editor.apply()
            true
        } else {
            false
        }
    }
}
