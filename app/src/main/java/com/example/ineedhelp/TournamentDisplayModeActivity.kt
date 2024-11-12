package com.example.ineedhelp

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ineedhelp.adapters.FightAdapter
import com.example.ineedhelp.helpers.FightJsonHelper
import com.example.ineedhelp.helpers.SpacingItemDecoration
import com.example.ineedhelp.model.FightGroup

class TournamentDisplayModeActivity : AppCompatActivity() {
    private lateinit var recyclerViewThreeFights: RecyclerView
    private lateinit var jsonHelper: FightJsonHelper
    private lateinit var buttonBack: Button
    private lateinit var buttonNext: Button
    private lateinit var editTextScore1: EditText
    private lateinit var editTextScore2: EditText
    private lateinit var buttonConfirm: Button
    private lateinit var fightsAdapter: FightAdapter

    private var activeFightIndex = 0 //та новенька дичина
    private var currentIndex = 0
    private val itemsPerPage = 3
    private var tournamentName: String? = null
    private var recyclerState: Parcelable? = null
    private var fightGroup: FightGroup? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tournament_display_mode)

        // Ініціалізація FightJsonHelper для роботи з JSON-файлом та всяких інших компонентів
        jsonHelper = FightJsonHelper(this)
        recyclerViewThreeFights = findViewById(R.id.recyclerViewThreeFights)
        buttonBack = findViewById(R.id.buttonBack)
        buttonNext = findViewById(R.id.buttonNext)
        editTextScore1 = findViewById(R.id.editTextNumber)
        editTextScore2 = findViewById(R.id.editTextNumber2)
        buttonConfirm = findViewById(R.id.button9)
        buttonConfirm.isEnabled = false
        buttonBack.isEnabled = false
        buttonNext.isEnabled = false

        //блок скролу і падінгі
        recyclerViewThreeFights.isNestedScrollingEnabled = false
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //отримання всякої номер групи і назви
        val groupId = intent.getIntExtra("GROUP_ID", -1) // Отримуємо groupId
        Log.d("BufferFileContent", groupId.toString()) // Вивід groupId вмісту в лог
        tournamentName = intent.getStringExtra("tournament_name")
        if (tournamentName != null) {
            title = tournamentName
        }

        //гамбаре гамбаре спайсінг
        val spaceInPixels = 16
        recyclerViewThreeFights.addItemDecoration(SpacingItemDecoration(spaceInPixels))

        //jsonHelper.logBufferFileContent()
        // Отримання даних боїв із буферного файлу
        fightGroup = jsonHelper.getFightsByGroupId(groupId) //тут була зміна

        if (fightGroup != null) {

            buttonConfirm.isEnabled = true
            buttonBack.isEnabled = true
            buttonNext.isEnabled = true

            // Налаштування RecyclerView для відображення списку боїв
            recyclerViewThreeFights.layoutManager = LinearLayoutManager(this)

            //якісь адаптерські шутчки іііі передача списку боїв
            fightsAdapter = FightAdapter(this, ArrayList(fightGroup!!.fights.subList(currentIndex, currentIndex + itemsPerPage)))
            recyclerViewThreeFights.adapter = fightsAdapter

            //оновлює не через сраний адаптер
            updateVisibleItems()

            buttonNext.setOnClickListener {
                if (currentIndex + 1 < fightGroup!!.fights.size) {                    //заість 1 було itemsPerPage
                    currentIndex += 1
                    updateVisibleItems()
                }
            }

            buttonBack.setOnClickListener {
                if (currentIndex > 0) {
                    currentIndex -= 1
                    updateVisibleItems()
                }
            }

        } else {
            Toast.makeText(this@TournamentDisplayModeActivity, R.string.error_loading_fights_data, Toast.LENGTH_SHORT).show()
        }

        //для очків
        buttonConfirm.setOnClickListener {
            val score1Text = editTextScore1.text.toString()
            val score2Text = editTextScore2.text.toString()

            // Перевірка на заповнення полів
            if (score1Text.isNotEmpty() && score2Text.isNotEmpty()) {
                val score1 = score1Text.toInt()
                val score2 = score2Text.toInt()

                // Оновлюємо очки для активного бою
                updateFightScores(score1, score2)
            } else if (score1Text.isNotEmpty() && score2Text.isEmpty()){
                val score1 = score1Text.toInt()
                val score2 = 10
                updateFightScores(score1, score2)
            }else if (score2Text.isNotEmpty() && score1Text.isEmpty()){
                val score1 = 10
                val score2 = score2Text.toInt()
                updateFightScores(score1, score2)
            }else{
                Toast.makeText(this, R.string.fill_score_fields, Toast.LENGTH_SHORT).show()
            }
        }
        // Відновлення стану, якщо він був збережений
        if (savedInstanceState != null) {
            //recyclerState = savedInstanceState.getParcelable("recycler_state")
            currentIndex = savedInstanceState.getInt("currentIndex", 0)
            activeFightIndex = savedInstanceState.getInt("activeFightIndex", 0)
        }
        updateVisibleItems() // Викликаємо, щоб оновити видимі елементи
    }

    //для цих шоб бої були по 3 і активний був
    private fun updateVisibleItems() {
        if (fightGroup != null) {
            val endIndex = (currentIndex + itemsPerPage).coerceAtMost(fightGroup!!.fights.size)
            val newItems = fightGroup!!.fights.subList(currentIndex, endIndex)
            fightsAdapter.updateVisibleItems(newItems)

            activeFightIndex = 0
            fightsAdapter.setActiveFightIndex(activeFightIndex)

            recyclerViewThreeFights.scrollToPosition(0)
        }
    }

    //для очків функція
    private fun updateFightScores(score1: Int, score2: Int) {
        //val fightGroup = jsonHelper.getFightsFromBuffer()

        if (fightGroup != null && currentIndex + activeFightIndex < fightGroup!!.fights.size) {
            // Отримуємо активний бій
            val activeFight = fightGroup!!.fights[currentIndex + activeFightIndex]

            // Оновлюємо очки бійців
            activeFight.fighter1Score = score1
            activeFight.fighter2Score = score2

            // Оновлюємо буферний файл
            jsonHelper.updateBufferFile(fightGroup!!)

            // Очищення текстових полів
            editTextScore1.text.clear()
            editTextScore2.text.clear()

            // Оновлення конкретного елемента в RecyclerView
            //fightsAdapter.notifyItemChanged(activeFightIndex)
            fightsAdapter.updateFightScore(activeFightIndex, score1, score2)

            Toast.makeText(this, R.string.scores_updated_successfully, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, R.string.failed_to_update_scores, Toast.LENGTH_SHORT).show()
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentIndex", currentIndex)
        outState.putInt("activeFightIndex", activeFightIndex)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentIndex = savedInstanceState.getInt("currentIndex", 0)
        activeFightIndex = savedInstanceState.getInt("activeFightIndex", 0)
        updateVisibleItems() // Оновлюємо видимі елементи після відновлення
    }
}