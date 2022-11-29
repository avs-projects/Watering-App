package com.example.kotlinproject.alarm.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinproject.data.Plant
import com.example.kotlinproject.databinding.PlantListAlarmNutrientsBinding

/**

Nutrient list adapter from @AlarmActivity

 */

class PlantListNutrientsAlarmAdapter(private val onPlantClicked: (Plant) -> Unit) :
    ListAdapter<Plant, PlantListNutrientsAlarmAdapter.PlantAlarmViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PlantAlarmViewHolder {
        return PlantAlarmViewHolder(
            PlantListAlarmNutrientsBinding.inflate(LayoutInflater.from(parent.context)) // Recovery of the plant_list_alarm_nutrients.xml layout format
        )
    }

    override fun onBindViewHolder(holder: PlantAlarmViewHolder, position: Int) {
        // Click management
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onPlantClicked(current)
        }
        holder.bind(current)
    }

    class PlantAlarmViewHolder(private var binding: PlantListAlarmNutrientsBinding) : // In display binding nutrients list
        RecyclerView.ViewHolder(binding.root) {

        // Binding database content with textView and ImageView
        fun bind(plant: Plant) {
            val localUri = Uri.parse(plant.plantPhoto)
            binding.plantFirstName.text = plant.firstName
            binding.plantSecondName.text = plant.secondName
            binding.plantDateLastNutrients.text = plant.dateLastNutrients
            binding.plantDateNextNutrients.text = plant.dateNextNutrients
            binding.imageView.setImageURI(localUri)
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Plant>() {
            override fun areItemsTheSame(oldPlant: Plant, newPlant: Plant): Boolean {
                return oldPlant === newPlant
            }

            override fun areContentsTheSame(oldPlant: Plant, newPlant: Plant): Boolean {
                return oldPlant.firstName == newPlant.firstName
            }
        }
    }
}