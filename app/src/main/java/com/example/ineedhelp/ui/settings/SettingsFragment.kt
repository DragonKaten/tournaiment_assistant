package com.example.ineedhelp.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.ineedhelp.databinding.FragmentSettingsBinding
import java.util.*

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)

        // Встановлюємо мову перед тим, як застосовувати тему
        setLanguage(getCurrentLanguage())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Налаштування для перемикання теми
        binding.themeSwitch.isChecked = isDarkThemeEnabled()
        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            setDarkTheme(isChecked)
            saveThemePreference(isChecked)
        }

        // Налаштування для вибору мови
        val languages = arrayOf("English", "Українська")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, languages)
        binding.languageSpinner.adapter = adapter
        binding.languageSpinner.setSelection(getCurrentLanguageIndex())

        binding.languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedLanguage = if (position == 0) "en" else "uk"
                if (selectedLanguage != getCurrentLanguage()) {
                    setLanguage(selectedLanguage)
                    saveLanguagePreference(selectedLanguage)
                    restartActivityForLanguageChange()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        return root
    }

    private fun isDarkThemeEnabled(): Boolean {
        return sharedPreferences.getBoolean("dark_theme", false)
    }

    private fun setDarkTheme(enabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (enabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun saveThemePreference(isDark: Boolean) {
        sharedPreferences.edit().putBoolean("dark_theme", isDark).apply()
    }

    private fun getCurrentLanguageIndex(): Int {
        return if (getCurrentLanguage() == "uk") 1 else 0
    }

    private fun getCurrentLanguage(): String {
        return sharedPreferences.getString("language", "en") ?: "en"
    }

    private fun setLanguage(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        requireActivity().baseContext.resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun saveLanguagePreference(languageCode: String) {
        sharedPreferences.edit().putString("language", languageCode).apply()
    }

    private fun restartActivityForLanguageChange() {
        requireActivity().recreate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
