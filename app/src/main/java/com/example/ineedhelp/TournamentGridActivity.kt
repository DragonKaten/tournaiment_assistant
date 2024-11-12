package com.example.ineedhelp

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ineedhelp.helpers.FightJsonHelper
import com.example.ineedhelp.model.FightGroup

class TournamentGridActivity : AppCompatActivity() {
    private lateinit var backPressedCallback: OnBackPressedCallback
    private var tournamentName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tournament_grid)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_tournament_grid)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //тітле ес наме турнемант
        tournamentName = intent.getStringExtra("tournament_name")
        if (tournamentName != null) {
            title = tournamentName
        }

        loadFightDataAndUpdateUI()

        val saveButton = findViewById<Button>(R.id.save_button)
        saveButton.setOnClickListener {
            val jsonHelper = FightJsonHelper(this)
            val fileName = jsonHelper.saveBufferToUniqueFile(tournamentName)
            //Toast.makeText(this, "Tournament saved as $fileName", Toast.LENGTH_SHORT).show()
        }

        // Ініціалізація обробника для кнопки "Назад"
        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val jsonHelper = FightJsonHelper(this@TournamentGridActivity)
                jsonHelper.clearBufferFile()
                //Toast.makeText(this@TournamentGridActivity, R.string.buffer_cleared_message, Toast.LENGTH_SHORT).show()
                finish() // Закриває активність і виконує стандартний перехід назад
            }
        }
        onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    override fun onResume() {
        super.onResume()
        loadFightDataAndUpdateUI()
    }

    private fun loadFightDataAndUpdateUI() {
        val container = findViewById<LinearLayout>(R.id.linearLayout_container_tournament_grid)
        container.removeAllViews()

        val jsonHelper = FightJsonHelper(this)
        val fightGroups = jsonHelper.getFightsFromBuffer()

        // Отримуємо кольори фону рядків для таблиці залежно від теми
        val evenRowBackgroundColor = if (isDarkTheme()) {
            resources.getColor(R.color.tableRowBackgroundDarkEven, null) // темна тема
        } else {
            resources.getColor(R.color.white, null) // світла тема
        }

        val oddRowBackgroundColor = if (isDarkTheme()) {
            resources.getColor(R.color.tableRowBackgroundDarkOdd, null) // темна тема
        } else {
            resources.getColor(R.color.light_grey, null) // світла тема
        }

        if (fightGroups != null) {
            for ((groupIndex, fightGroup) in fightGroups.withIndex()) {
                val tableLayout = TableLayout(this).apply {
                    layoutParams = TableLayout.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT
                    )
                    isStretchAllColumns = true
                }

                val groupTitle = TextView(this).apply {
                    text = getString(R.string.group_fights_title, groupIndex + 1)
                    textSize = 18f
                    setPadding(10, 20, 10, 20)
                }
                container.addView(groupTitle)

                for ((fightIndex, fight) in fightGroup.fights.withIndex()) {
                    val row = TableRow(this).apply {
                        setPadding(10, 10, 10, 10)
                        // Чередуємо кольори фону
                        setBackgroundColor(
                            if (fightIndex % 2 == 0) {
                                evenRowBackgroundColor
                            } else {
                                oddRowBackgroundColor
                            }
                        )
                    }

                    val fightNumber = TextView(this).apply {
                        text = "${fightIndex + 1}."
                        layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.1f)
                        gravity = Gravity.CENTER
                    }
                    row.addView(fightNumber)

                    val fighter1 = TextView(this).apply {
                        text = fight.fighter1Name
                        textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                        layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.4f)
                        //setTextColor(resources.getColor(R.color.black))
                    }
                    row.addView(fighter1)

                    val separator1 = TextView(this).apply {
                        text = "-"
                        textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                    }
                    row.addView(separator1)

                    val fighter2 = TextView(this).apply {
                        text = fight.fighter2Name
                        textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                        layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.4f)
                        //setTextColor(resources.getColor(R.color.black))
                    }
                    row.addView(fighter2)

                    val score1 = TextView(this).apply {
                        text = fight.fighter1Score.toString()
                        textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                        layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.1f)
                    }
                    row.addView(score1)

                    val separator2 = TextView(this).apply {
                        text = ":"
                        textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                    }
                    row.addView(separator2)

                    val score2 = TextView(this).apply {
                        text = fight.fighter2Score.toString()
                        textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                        layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.1f)
                    }
                    row.addView(score2)

                    tableLayout.addView(row)
                }

                container.addView(tableLayout)

                val btnDisplayMode = Button(this).apply {
                    text = getString(R.string.display_mode_button_text, groupIndex + 1)
                }
                container.addView(btnDisplayMode)
                val selectedGroupId = groupIndex
                btnDisplayMode.setOnClickListener {
                    val intent = Intent(this, TournamentDisplayModeActivity::class.java)
                    intent.putExtra("tournament_name", tournamentName)
                    intent.putExtra("GROUP_ID", selectedGroupId)
                    startActivity(intent)
                }
            }
        } else {
            Toast.makeText(this, R.string.error_loading_fights_data, Toast.LENGTH_SHORT).show()
        }
    }
    // Функція для визначення поточної теми
    private fun isDarkTheme(): Boolean {
        return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }
}