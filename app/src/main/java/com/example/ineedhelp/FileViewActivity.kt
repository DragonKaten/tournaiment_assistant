package com.example.ineedhelp

import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ineedhelp.helpers.FightJsonHelper
import java.io.File

class FileViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_file_view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val fileName = intent.getStringExtra("fileName") ?: return
        title = fileName

        val deleteButton = findViewById<Button>(R.id.delete_button)
        deleteButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(R.string.delete_file)
                .setMessage(R.string.delete_confirmation_message)
                .setPositiveButton(R.string.delete) { _, _ ->
                    val file = File(filesDir, fileName)
                    if (file.exists() && file.delete()) {
                        Toast.makeText(this, R.string.file_deleted, Toast.LENGTH_SHORT).show()
                        finish() // Повертаємось у HistoryFragment
                    } else {
                        Toast.makeText(this, R.string.error_deleting_file, Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton(R.string.cancel, null) // Кнопка для скасування
                .show()
        }

        val openTournamentButton = findViewById<Button>(R.id.open_tournament_button)
        openTournamentButton.setOnClickListener{
            val jsonHelper = FightJsonHelper(this)
            val tournamentName = jsonHelper.extractSanitizedFileName(fileName)
            jsonHelper.copyUniqueFileToBuffer(fileName)
            val intent = Intent(this@FileViewActivity, TournamentGridActivity::class.java)
            intent.putExtra("tournament_name", tournamentName) // Додаємо назву турніру
            startActivity(intent)
        }

        loadFightDataAndUpdateUI(fileName)
    }
    private fun loadFightDataAndUpdateUI(fileName: String) {
        val container = findViewById<LinearLayout>(R.id.linearLayout_container_tournament_grid)
        val emptyMessageTextView = findViewById<TextView>(R.id.emptyMessageTextView)
        container.removeAllViews()

        val jsonHelper = FightJsonHelper(this)
        val fightGroups = jsonHelper.readFromFile(fileName)  // Використовуємо обраний файл

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
            }
        } else {
            //Toast.makeText(this, R.string.error_loading_fights_data, Toast.LENGTH_SHORT).show()
            container.visibility = View.GONE
            emptyMessageTextView.visibility = View.VISIBLE
            //emptyMessageTextView.text = getString(R.string.error_loading_fights_data)
        }
    }
    private fun isDarkTheme(): Boolean {
        return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }
}