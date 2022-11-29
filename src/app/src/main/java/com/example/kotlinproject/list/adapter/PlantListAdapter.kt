package com.example.kotlinproject.list.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinproject.data.Plant
import com.example.kotlinproject.databinding.PlantListBinding

/**

Adapt lists from @MainActivity

 */

class PlantListAdapter(private val onPlantClicked: (Plant) -> Unit) :
    ListAdapter<Plant, PlantListAdapter.PlantViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PlantViewHolder {
        return PlantViewHolder(
            PlantListBinding.inflate(LayoutInflater.from(parent.context)) // Recovery of the plant_list.xml layout format
        )
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {

        // Click management
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onPlantClicked(current)
        }
        holder.bind(current)
    }

    class PlantViewHolder(private var binding: PlantListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Binding database content with textView and ImageView
        fun bind(plant: Plant) {
            val localUri = Uri.parse(plant.plantPhoto)
            binding.plantFirstName.text = plant.firstName
            binding.plantSecondName.text = plant.secondName
            binding.plantDateLastWatering.text = plant.dateLastWatering
            binding.plantDateLastNutrients.text = plant.dateLastNutrients
            binding.plantDateNextWatering.text = plant.dateNextWatering
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
                return oldPlant.id == newPlant.id
            }
        }
    }
}