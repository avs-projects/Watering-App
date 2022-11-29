package com.example.kotlinproject.alarm.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinproject.data.Plant
import com.example.kotlinproject.databinding.PlantListAlarmWateringBinding

/**

Watering list adapter from @AlarmActivity

 */

class PlantListWateringAlarmAdapter(private val onPlantClicked: (Plant) -> Unit) :
    ListAdapter<Plant, PlantListWateringAlarmAdapter.PlantAlarmViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PlantAlarmViewHolder {
        return PlantAlarmViewHolder(
            PlantListAlarmWateringBinding.inflate(LayoutInflater.from(parent.context)) // Recovery of layout format plant_list_alarm_watering.xml
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

    class PlantAlarmViewHolder(private var binding: PlantListAlarmWateringBinding) : // In binding watering list view
        RecyclerView.ViewHolder(binding.root) {


        // Binding database content with textView and ImageView
        fun bind(plant: Plant) {
            val localUri = Uri.parse(plant.plantPhoto)
            binding.plantFirstName.text = plant.firstName
            binding.plantSecondName.text = plant.secondName
            binding.plantDateLastWatering.text = plant.dateLastWatering
            binding.plantDateNextWatering.text = plant.dateNextWatering
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