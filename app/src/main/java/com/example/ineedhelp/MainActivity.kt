package com.example.ineedhelp

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.ineedhelp.databinding.ActivityMainBinding
import com.example.ineedhelp.helpers.FightJsonHelper

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    //актівіті ініціюється
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Застосування збереженої теми при запуску програми
        val sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        val isDarkTheme = sharedPreferences.getBoolean("dark_theme", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        //підкл макет через біндінг
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //робиться тулбар верхній
        setSupportActionBar(binding.appBarMain.toolbar)

        //бічне меню навігації налаштовується
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_history, R.id.nav_settings, R.id.nav_info
            ), drawerLayout
        )
        //при виборі елементів меню буде перехід
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    //ебаут кнопка назад
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    //при завершенні програми
    override fun onDestroy() {
        super.onDestroy()
        val jsonHelper = FightJsonHelper(this) // Ініціалізація FightJsonHelper
        jsonHelper.clearBufferFile() // Очищення буферного файлу при завершенні програми
        //Toast.makeText(this@MainActivity, "буферний файл очищено!", Toast.LENGTH_SHORT).show()

    }
}