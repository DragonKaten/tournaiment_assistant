package com.example.ineedhelp.ui.history

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ineedhelp.FileViewActivity
import com.example.ineedhelp.R
import com.example.ineedhelp.adapters.HistoryFileAdapter
import com.example.ineedhelp.databinding.FragmentHistoryBinding
import java.io.File

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //val historyViewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root
/*
        val files = getJsonFiles() // Отримання списку файлів JSON
        val adapter = HistoryFileAdapter(files) { fileName ->
            openFileContent(fileName) // Відкриття вмісту файла при натисканні
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

 */

        updateHistoryList() // Ініціалізуємо список файлів при створенні фрагменту
        return root
    }

   /* private fun getJsonFiles(): List<String> {
        val filesDir = requireContext().filesDir
        return filesDir.list { _, name -> name.endsWith(".json") }?.toList() ?: emptyList()
    }

    private fun openFileContent(fileName: String) {
        val intent = Intent(requireContext(), FileViewActivity::class.java)
        intent.putExtra("fileName", fileName)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    */

    private fun updateHistoryList() {
        val files = getJsonFiles() // Отримуємо актуальний список файлів JSON, крім буферного

        if (files.isEmpty()) {
            binding.recyclerView.visibility = View.GONE
            binding.emptyMessageTextView.visibility = View.VISIBLE // Повідомлення в центрі
            binding.emptyMessageTextView.text = getString(R.string.no_history_files) // Текст повідомлення
        } else {
            binding.recyclerView.visibility = View.VISIBLE
            binding.emptyMessageTextView.visibility = View.GONE

            val adapter = HistoryFileAdapter(files) { fileName ->
                openFileContent(fileName) // Відкриваємо файл при натисканні
            }

            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerView.adapter = adapter
        }
    }

    private fun getJsonFiles(): List<String> {
        val filesDir = requireContext().filesDir
        return filesDir.list { _, name ->
            name.endsWith(".json") && name != "buffer_tournament.json" // Виключаємо буферний файл
        }?.toList() ?: emptyList()
    }

    private fun openFileContent(fileName: String) {
        val intent = Intent(requireContext(), FileViewActivity::class.java)
        intent.putExtra("fileName", fileName)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        updateHistoryList() // Оновлення списку при поверненні до фрагменту
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}