package com.example.ineedhelp.helpers

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.ineedhelp.R
import com.example.ineedhelp.model.FightGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FightJsonHelper(private val context: Context) {
    private val gson = Gson()
    private val bufferFileName = "buffer_tournament.json"

    fun generateBufferFile(fightGroups: List<FightGroup>) {
        val bufferFile = File(context.filesDir, bufferFileName)

        if (!bufferFile.exists()) {
            bufferFile.createNewFile()
        }

        try {
            FileWriter(bufferFile).use { writer ->
                gson.toJson(fightGroups, writer)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun extractSanitizedFileName(fullFileName: String): String {
        // Оновлений регекс для обробки мілісекунд
        val regex = """^(.*)_\d{4}-\d{2}-\d{2}_\d{2}-\d{2}-\d{2}-\d{3}\.json$""".toRegex()
        val matchResult = regex.find(fullFileName)
        return matchResult?.groups?.get(1)?.value ?: fullFileName
    }

    fun saveBufferToUniqueFile(tournamentFileName: String?): String {
        val sanitizedFileName = tournamentFileName?.replace("[^a-zA-Zа-щА-ЩґҐєЄіІїЇюЮяЯ0-9]".toRegex(), "_") ?: "tournament"

        // Оновлений формат дати для включення мілісекунд
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS", Locale.getDefault())
        val timestamp = dateFormatter.format(Date())
        val uniqueFileName = "${sanitizedFileName}_$timestamp.json"

        val bufferData = readFromFile(bufferFileName)
        if (bufferData != null) {
            saveToFile(uniqueFileName, bufferData)
            Toast.makeText(context, context.getString(R.string.saved_as, uniqueFileName), Toast.LENGTH_SHORT).show()
        }

        return uniqueFileName
    }


    private fun saveToFile(fileName: String, fightGroups: List<FightGroup>) {
        val file = File(context.filesDir, fileName)
        val fileWriter = FileWriter(file)
        gson.toJson(fightGroups, fileWriter)
        fileWriter.close()
        //Log.d("save To File", fileName)
    }

    fun copyUniqueFileToBuffer(uniqueFileName: String) {
        val uniqueFile = File(context.filesDir, uniqueFileName)
        val bufferFile = File(context.filesDir, bufferFileName)

        if (uniqueFile.exists()) {
            try {
                uniqueFile.copyTo(bufferFile, overwrite = true)
                //Log.d("copyUniqueFileToBuffer", "Content copied to buffer file")
                Toast.makeText(context, context.getString(R.string.content_copied), Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            //Log.d("copyUniqueFileToBuffer", "Unique file does not exist")
            Toast.makeText(context, context.getString(R.string.unique_file_not_exist), Toast.LENGTH_SHORT).show()
        }
    }


    fun readFromFile(fileName: String): List<FightGroup>? {
        val file = File(context.filesDir, fileName)
        return if (file.exists()) {
            val fileReader = FileReader(file)
            val type = object : TypeToken<List<FightGroup>>() {}.type
            val fightGroups: List<FightGroup> = gson.fromJson(fileReader, type)
            fileReader.close()
            fightGroups
        } else {
            null
        }
    }



    fun getFightsFromBuffer(): List<FightGroup>? {
        val bufferFile = File(context.filesDir, bufferFileName)

        if (!bufferFile.exists()) return null

        return try {
            val jsonString = bufferFile.readText()
            val type = object : TypeToken<List<FightGroup>>() {}.type
            gson.fromJson(jsonString, type)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }


    // 4. Очищення буферного файлу при виході
    fun clearBufferFile() {
        val bufferFile = File(context.filesDir, bufferFileName)
        if (bufferFile.exists()) {
            bufferFile.delete()
        }
    }

    fun updateBufferFile(updatedFightGroup: FightGroup) {
        // Читаємо всі групи боїв з буфера
        val fightGroups = getFightsFromBuffer()?.toMutableList() ?: mutableListOf()

        // Знаходимо існуючу групу з таким самим groupId, якщо вона є
        val existingGroupIndex = fightGroups.indexOfFirst { it.groupId == updatedFightGroup.groupId }

        if (existingGroupIndex != -1) {
            // Якщо група існує, замінюємо її оновленими даними
            fightGroups[existingGroupIndex] = updatedFightGroup
        } else {
            // Якщо групи з таким groupId немає, додаємо її як нову
            fightGroups.add(updatedFightGroup)
        }

        // Зберігаємо оновлений список груп боїв у файл
        saveToFile(bufferFileName, fightGroups)
    }

    fun getFightsByGroupId(groupId: Int): FightGroup? {
        val fightGroups = getFightsFromBuffer()
        return fightGroups?.find { it.groupId == groupId }
    }
}