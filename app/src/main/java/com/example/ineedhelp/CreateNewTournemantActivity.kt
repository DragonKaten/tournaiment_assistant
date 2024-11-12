package com.example.ineedhelp

import android.content.Intent
import androidx.appcompat.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ineedhelp.helpers.FightJsonHelper
import com.example.ineedhelp.helpers.TournamentManager

class CreateNewTournemantActivity : AppCompatActivity() {
    private lateinit var tournamentManager: TournamentManager
    private lateinit var backPressedCallback: OnBackPressedCallback
    private var tournamentName: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_new_tournemant)

        //паддінг
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_create_new_tournament)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Ініціалізація TournamentManager
        tournamentManager = TournamentManager(FightJsonHelper(this))

        //тітле ес наме турнемант
        tournamentName = intent.getStringExtra("tournament_name")
        if (tournamentName != null) {
            title = tournamentName
        }

        val etNumberOfFighters = findViewById<EditText>(R.id.editTextNumber_enterNumOfFighters)
        val btnConfirm = findViewById<Button>(R.id.buttonConfirm_numOfFighters)
        val llTablesContainer = findViewById<LinearLayout>(R.id.llTablesContainer_namesOfFighters)



        btnConfirm.setOnClickListener {
            llTablesContainer.removeAllViews()
            val numberOfFighters = etNumberOfFighters.text.toString().toIntOrNull()

            if (numberOfFighters != null) {
                if (numberOfFighters < 4) {
                    etNumberOfFighters.text.clear()
                    Toast.makeText(this, R.string.number_too_low_message, Toast.LENGTH_SHORT).show()
                } else if (numberOfFighters >= 10) {
                    val parts = divideFighters(numberOfFighters)

                    AlertDialog.Builder(this)
                        .setTitle(R.string.divide_fighters_title)
                        .setMessage(getString(R.string.divide_fighters_message, parts.size))
                        .setPositiveButton(R.string.yes) { _, _ ->
                            generateFighterInputFields(parts, llTablesContainer)
                        }
                        .setNegativeButton(R.string.no) { _, _ ->
                            generateFighterInputFields(listOf(numberOfFighters), llTablesContainer) }
                        .show()
                } else {
                    generateFighterInputFields(listOf(numberOfFighters), llTablesContainer)
                }
            } else {
                Toast.makeText(this, R.string.invalid_number_message, Toast.LENGTH_SHORT).show()
            }
        }





        // Ініціалізація обробника для кнопки "Назад"
        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val jsonHelper = FightJsonHelper(this@CreateNewTournemantActivity)
                jsonHelper.clearBufferFile()
                Toast.makeText(this@CreateNewTournemantActivity, R.string.buffer_cleared_message, Toast.LENGTH_SHORT).show()
                finish() // Закриває активність і виконує стандартний перехід назад
            }
        }
        onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }




    //ділити файтерів якщо юзер погодився
    private fun divideFighters(num: Int): List<Int> {
        val parts = mutableListOf<Int>()

        // Для випадку з 10 бійцями - спеціальне правило
        if (num == 10) {
            parts.add(5)
            parts.add(5)
            return parts
        }

        // Кількість груп - поділ на максимальну кількість бійців у групах (наприклад, 10)
        val numGroups = (num + 9) / 10 // Рахуємо, скільки потрібно груп (округляємо вверх)
        val baseGroupSize = num / numGroups // Середня кількість бійців у групі
        val remainder = num % numGroups // Залишок, що буде розподілено між кількома групами

        // Додаємо до груп базову кількість бійців
        for (i in 0 until numGroups) {
            if (i < remainder) {
                // Додаємо +1 до перших "remainder" груп, щоб компенсувати залишок
                parts.add(baseGroupSize + 1)
            } else {
                parts.add(baseGroupSize)
            }
        }

        return parts
    }

    //генерує текстові поля і 2 кнопки
    private fun generateFighterInputFields(parts: List<Int>, container: LinearLayout) {
        val fighterNames = mutableListOf<MutableList<String>>() // Список для імен всіх груп
        val editTexts = mutableListOf<EditText>() // Список для зберігання всіх EditText

        for ((index, part) in parts.withIndex()) {
            val groupLabel = TextView(this).apply {
                text = getString(R.string.fighters_group, index + 1, part)
                textSize = 18f
                setPadding(0, 16, 0, 8)
            }
            container.addView(groupLabel)

            val groupNames = mutableListOf<String>() // Список для імен однієї групи


            for (i in 1..part) {
                val fighterNameInput = EditText(this).apply {
                    hint = getString(R.string.fighter_hint, i)
                    inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                    setPadding(0, 8, 0, 8)
                }
                container.addView(fighterNameInput)
                editTexts.add(fighterNameInput)
            }
            fighterNames.add(groupNames) // Додаємо імена групи до загального списку
        }


        val btnGenerate = Button(this).apply {
            text = getString(R.string.btn_generate)
            setOnClickListener {
                // Перевірка на заповненість усіх полів
                var allFieldsFilled = true // Прапорець для перевірки
                val uniqueNames = mutableSetOf<String>() // Множина для зберігання унікальних імен

                for ((index, group) in fighterNames.withIndex()) {
                    for (i in 0 until parts[index]) {
                        val editTextIndex = editTexts.take(parts.take(index).sum()).size + i
                        val name = editTexts[editTextIndex].text.toString()

                        // Якщо поле порожнє, встановлюємо прапорець в false
                        if (name.isEmpty()) {
                            allFieldsFilled = false
                            break
                        }

                        // Перевірка на унікальність
                        if (!uniqueNames.add(name)) {
                            Toast.makeText(this@CreateNewTournemantActivity, R.string.unique_names_required, Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }

                        // Якщо поле заповнене, оновлюємо або додаємо ім'я
                        if (group.size >= i + 1) {
                            group[i] = name
                        } else {
                            group.add(name)
                        }
                    }
                    if (!allFieldsFilled) break // Виходимо з зовнішнього циклу, якщо знайшли порожнє поле
                }

                // Якщо не всі поля заповнені, показуємо повідомлення і не продовжуємо
                if (!allFieldsFilled) {
                    Toast.makeText(this@CreateNewTournemantActivity, R.string.all_fields_required, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Заповнення імен бійців
                val fighterGroups = fighterNames.map { ArrayList(it) }

                // Генерація боїв та запис у буферний файл
                val fights = tournamentManager.generateRoundRobinFights(fighterGroups)
                tournamentManager.saveToBuffer(fights)


                // Передаємо імена в нову активність через Intent
                val intent = Intent(this@CreateNewTournemantActivity, TournamentGridActivity::class.java)
                intent.putExtra("fighters_names", ArrayList(fighterNames.map { ArrayList(it) }))
                intent.putExtra("tournament_name", tournamentName) // Додаємо назву турніру
                startActivity(intent)
            }
        }


        val btnCancel = Button(this).apply {
            text = getString(R.string.cancel)
            setOnClickListener {
                val jsonHelper = FightJsonHelper(this@CreateNewTournemantActivity)
                jsonHelper.clearBufferFile()
                finish() // Закриває активність
            }
        }

        container.addView(btnGenerate)
        container.addView(btnCancel)
    }
}