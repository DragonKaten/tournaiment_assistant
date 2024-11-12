package com.example.ineedhelp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.ineedhelp.CreateNewTournemantActivity
import com.example.ineedhelp.databinding.FragmentHomeBinding
import android.app.AlertDialog
import android.text.InputType
import android.widget.Toast
import com.example.ineedhelp.R


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //val homeViewModel =ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //обробка по кнопці
        binding.buttonCreateNewTournament.setOnClickListener {
            //техт поле
            val textNameNewTournament = EditText(requireContext()).apply {
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES}

            //dial
            val dialog = AlertDialog.Builder(requireContext())
                //Введіть назву турніру
                .setTitle(R.string.enter_tournament_name)
                .setView(textNameNewTournament)
                .setPositiveButton(R.string.ok) { dialogInterface, i ->
                    // get text
                    val tournamentName = textNameNewTournament.text.toString()
                    if (tournamentName.isNotEmpty()) {
                    // Відкриваємо нову активність і передаємо текст
                    val intent = Intent(activity, CreateNewTournemantActivity::class.java)
                    intent.putExtra("tournament_name", tournamentName)
                    startActivity(intent)
                    } else {
                        // Якщо текст порожній, показуємо повідомлення про помилку
                        Toast.makeText(
                            requireContext(),
                            R.string.please_fill_in_the_input_field,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .setNegativeButton(R.string.cancel, null)
                .create()

            dialog.show()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}