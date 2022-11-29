package com.example.kotlinproject.list.fragments

import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.kotlinproject.data.PlantApplication
import com.example.kotlinproject.R
import com.example.kotlinproject.data.Plant
import com.example.kotlinproject.databinding.FragmentPlantDetailBinding
import com.example.kotlinproject.list.viewmodel.PlantViewModel
import com.example.kotlinproject.list.viewmodel.PlantViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**

Fragment allowing the display of the complete details of the plant

 */

@ExperimentalCoroutinesApi
@RequiresApi(Build.VERSION_CODES.O)

class PlantDetailFragment : Fragment() {

    lateinit var plant: Plant

    // ViewModel recovery of database content
    private val viewModel: PlantViewModel by activityViewModels {
        PlantViewModelFactory(
            (activity?.application as PlantApplication).database.plantDao()
        )
    }

    // Recovery of the id of the clicked plant
    private val navigationArgs: PlantDetailFragmentArgs by navArgs()

    private var _binding: FragmentPlantDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlantDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Retrieval of information from the database related to the id of the plant and display in the editText and imageView
     */
    private fun bind(plant: Plant) {
        binding.apply {
            val localUri = Uri.parse(plant.plantPhoto)
            plantFirstName.text = plant.firstName
            plantSecondName.text = plant.secondName
            plantFrequencyWatering1.text = plant.frequencyWatering1.toString()
            plantFrequencyWatering2.text = plant.frequencyWatering2.toString()
            plantDateLastWatering.text = plant.dateLastWatering
            plantDateLastNutrients.text = plant.dateLastNutrients
            plantDateNextWatering.text = plant.dateNextWatering
            plantDateNextNutrients.text = plant.dateNextNutrients
            imageView.setImageURI(localUri)

            deletePlant.setOnClickListener { showConfirmationDialog() } // If click on the delete button display of a confirmation dialog
            editPlant.setOnClickListener { editPlant() } // If click on the floating button edit, display of the add plant fragment with the information of the plant to be updated
        }
    }


    /**
     * Go to the addPlant fragment by sending the id of the seen plant
     */
    private fun editPlant() {
        val action = PlantDetailFragmentDirections.actionPlantDetailFragmentToAddPlantFragment(
            getString(R.string.edit_fragment_title),
            plant.id
        )
        this.findNavController().navigate(action)
    }

    /**
     * Display of the dialog when clicking on delete
     */
    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deletePlant()
            }
            .show()
    }

    /**
     * Removal of the plant presented
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun deletePlant() {
        viewModel.deletePlant(plant)
        findNavController().navigateUp()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieving plant details via id
        val id = navigationArgs.plantId

        // Watch to see data and change only when changed
        viewModel.retrievePlant(id).observe(this.viewLifecycleOwner) { selectedPlant ->
            plant = selectedPlant
            bind(plant)
        }
    }

    /**
     * Call when destroying the fragment
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}