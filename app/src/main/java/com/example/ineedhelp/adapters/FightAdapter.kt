package com.example.ineedhelp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ineedhelp.R
import com.example.ineedhelp.model.FightModelClass

class FightAdapter(
    private val context: Context,
    private var visibleItems: ArrayList<FightModelClass>
) : RecyclerView.Adapter<FightAdapter.ViewHolder>() {

    private var activeFightIndex: Int = 0
    private var originalFights: List<FightModelClass> = ArrayList(visibleItems) // Оригінальні бої

    fun updateVisibleItems(newItems: List<FightModelClass>) {
        visibleItems.clear()
        visibleItems.addAll(newItems)

        notifyDataSetChanged()
    }

    fun setActiveFightIndex(index: Int) {
        val previousActiveIndex = activeFightIndex
        activeFightIndex = index

        // Оновлюємо виділення старого та нового активного бою
        notifyItemChanged(previousActiveIndex)
        notifyItemChanged(activeFightIndex)
    }

    fun updateFightScore(index: Int, score1: Int, score2: Int) {
        visibleItems[index].fighter1Score = score1
        visibleItems[index].fighter2Score = score2
        notifyItemChanged(index) // Оновлення конкретного елемента
    }




    override fun getItemCount(): Int = visibleItems.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.fight_custom_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fight = visibleItems[position]
        holder.fighter1Name.text = fight.fighter1Name
        holder.fighter2Name.text = fight.fighter2Name
        holder.fighterScores.text = "${fight.fighter1Score} : ${fight.fighter2Score}"

        // Виділення активного бою
        holder.itemView.isSelected = position == activeFightIndex
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fighter1Name: TextView = view.findViewById(R.id.fighter1Name)
        val fighter2Name: TextView = view.findViewById(R.id.fighter2Name)
        val fighterScores: TextView = view.findViewById(R.id.fighterScores)
    }
}
