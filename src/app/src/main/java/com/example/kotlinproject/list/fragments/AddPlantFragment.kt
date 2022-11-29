package com.example.kotlinproject.list.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.kotlinproject.R
import com.example.kotlinproject.data.PlantApplication
import com.example.kotlinproject.data.Plant
import com.example.kotlinproject.databinding.FragmentAddPlantBinding
import com.example.kotlinproject.list.viewmodel.PlantViewModel
import com.example.kotlinproject.list.viewmodel.PlantViewModelFactory
import com.example.kotlinproject.utils.DatePickerFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.File

/**

Fragment allowing the addition of plants

 */

@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalCoroutinesApi

class AddPlantFragment : Fragment() {

    lateinit var plant: Plant

    // property: Local file uri
    private var localUri: Uri? = null

    // ViewModel recovery of database content
    private val viewModel: PlantViewModel by activityViewModels {
        PlantViewModelFactory(
            (activity?.application as PlantApplication).database
                .plantDao()
        )
    }

    companion object {
        const val IMAGE_REQUEST_CODE: Int = 1_000
    }

    // Recovery of the id of the clicked plant
    private val navigationArgs: PlantDetailFragmentArgs by navArgs()

    private var _binding: FragmentAddPlantBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddPlantBinding.inflate(inflater, container, false)

        return binding.root
    }

    /**
     * Call when creating the view
     * The id lets you know the mode used, either adding a plant or updating an existing plant
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieval of the id submitted when passing as arguments
        val id = navigationArgs.plantId

        // When clicking on the edit text to choose the date of the next classic watering, display of the date picker
        binding.plantDateLastWatering.setOnClickListener {
            val datePickerFragment = DatePickerFragment() // Recuperation du fragment
            val supportFragmentManager = requireActivity().supportFragmentManager

            // Recovery of the value written in the fragment
            supportFragmentManager.setFragmentResultListener(
                "REQUEST_KEY",
                viewLifecycleOwner
            ) { resultKey, bundle ->
                if (resultKey == "REQUEST_KEY") {
                    val date = bundle.getString("SELECTED_DATE")
                    binding.apply {
                        // Putting value in the edit text
                        plantDateLastWatering.setText(
                            date,
                            TextView.BufferType.SPANNABLE
                        )
                    }
                }
            }

            // Displaying the datePicker fragment
            datePickerFragment.show(supportFragmentManager, "DatePickerFragment")
        }

        // Same principle as before
        binding.plantDateLastNutrients.setOnClickListener {
            val datePickerFragment = DatePickerFragment()
            val supportFragmentManager = requireActivity().supportFragmentManager

            supportFragmentManager.setFragmentResultListener(
                "REQUEST_KEY",
                viewLifecycleOwner
            ) { resultKey, bundle ->
                if (resultKey == "REQUEST_KEY") {
                    val date = bundle.getString("SELECTED_DATE")
                    binding.apply {
                        plantDateLastNutrients.setText(
                            date,
                            TextView.BufferType.SPANNABLE
                        )
                    }
                }
            }
            datePickerFragment.show(supportFragmentManager, "DatePickerFragment")
        }

        // Click on button image launches the selection of the image
        binding.selectImage.setOnClickListener {
            pickImageFromGallery()
        }

        // Initialization of empty helperText

        // If id of the plant is positive, we retrieve from the database the content related to this plant
        // and the user can refresh.
        if (id > 0) {
            if (null == savedInstanceState) {
                viewModel.retrievePlant(id).observe(this.viewLifecycleOwner) { selectedPlant ->
                    plant = selectedPlant
                    bind(plant)
                    setTextHelperText()
                }
            } else {
                bind2(binding.plantFirstName.text.toString(),
                    binding.plantSecondName.text.toString(),
                    binding.plantFrequencyWatering1.text.toString(),
                    binding.plantFrequencyWatering2.text.toString(),
                    binding.plantDateLastWatering.text.toString(),
                    binding.plantDateLastNutrients.text.toString(),
                    binding.plantUriImage.text.toString())
                setTextHelperText()
            }

        } else { // else, we add a new plant
            binding.saveAction.setOnClickListener {
                addNewPlant()
            }
        }
    }

    /**
     * Launch recovery of the image from the gallery
     */
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    /**
     * When the image is chosen, set the URI of the image upload from the gallery
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            val inputStream =
                data?.data?.let { activity?.contentResolver?.openInputStream(it) }

            val path = activity?.filesDir
            val letDirectory = File(path, "LET")
            letDirectory.mkdirs()

            // Make the local filename to store the image
            val fileNamePrefix = "plante"
            val preferences = activity?.getSharedPreferences("numImage", Context.MODE_PRIVATE)
            val numImage = preferences?.getInt("numImage", 1)
            val fileName = "$fileNamePrefix$numImage"

            // Open outputStream to local file
            val file = File(letDirectory, fileName)
            val outputStream = file.outputStream()

            // Save new frame counter
            preferences?.edit()?.putInt("numImage", numImage!! + 1)?.apply()

            // Copy inputStream which points to the gallery image
            // to local file
            inputStream?.copyTo(outputStream)

            // Store local file Uri in localUr property
            localUri = file.toUri()
            outputStream.close()
            inputStream?.close()

            // Uri in edittext
            binding.plantUriImage.setText(localUri.toString())

            // Picture display
            binding.imageView.setImageURI(localUri)
        }
    }

    /**
     * Verification of the content of edit text in order to validate their addition to the database
     */
    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.plantFirstName.text.toString(),
            binding.plantFrequencyWatering1.text.toString(),
            binding.plantFrequencyWatering2.text.toString(),
            binding.plantDateLastWatering.text.toString(),
            binding.plantDateLastNutrients.text.toString(),
            binding.plantUriImage.text.toString(),
        )
    }

    /**
     * Retrieval of information from the database related to the id of the plant and display in the editText and imageView
     */
    private fun bind(plant: Plant) {
        binding.apply {
            val localUri = Uri.parse(plant.plantPhoto)
            plantFirstName.setText(plant.firstName)
            plantSecondName.setText(plant.secondName)
            plantFrequencyWatering1.setText(
                plant.frequencyWatering1.toString(),
                TextView.BufferType.SPANNABLE // The text is editable
            )
            plantFrequencyWatering2.setText(
                plant.frequencyWatering2.toString(),
                TextView.BufferType.SPANNABLE
            )
            plantDateLastWatering.setText(
                plant.dateLastWatering,
                TextView.BufferType.SPANNABLE
            )
            plantDateLastNutrients.setText(
                plant.dateLastNutrients,
                TextView.BufferType.SPANNABLE
            )
            plantUriImage.setText(
                plant.plantPhoto,
                TextView.BufferType.SPANNABLE
            )
            imageView.setImageURI(localUri)

            // If click on save button, call updatePlant
            saveAction.setOnClickListener { updatePlant() }
        }
    }

    /**
     * Retrieval of information from the database related to the id of the plant and display in the editText and imageView
     */
    private fun bind2(
        firstNameB: String,
        secondNameB: String,
        frequencyWatering1B: String,
        frequencyWatering2B: String,
        dateLastWateringB: String,
        dateLastNutrientsB: String,
        imageUriB: String,
    ) {
        binding.apply {
            val localUri = Uri.parse(imageUriB)
            plantFirstName.setText(firstNameB)
            plantSecondName.setText(secondNameB)
            plantFrequencyWatering1.setText(
                frequencyWatering1B,
                TextView.BufferType.SPANNABLE // The text is editable
            )
            plantFrequencyWatering2.setText(
                frequencyWatering2B,
                TextView.BufferType.SPANNABLE
            )
            plantDateLastWatering.setText(
                dateLastWateringB,
                TextView.BufferType.SPANNABLE
            )
            plantDateLastNutrients.setText(
                dateLastNutrientsB,
                TextView.BufferType.SPANNABLE
            )
            plantUriImage.setText(
                imageUriB,
                TextView.BufferType.SPANNABLE
            )
            imageView.setImageURI(localUri)

            // If click on save button, call updatePlant
            saveAction.setOnClickListener { updatePlant() }
        }
    }

    /**
     * Insertion d'une nouvelle plante dans la base de donnees et retour vers la liste des plantes
     */
    private fun addNewPlant() {
        if (isEntryValid()) { // Verification of the validity of the information submitted
            viewModel.addNewPlant(
                binding.plantFirstName.text.toString(),
                binding.plantSecondName.text.toString(),
                binding.plantFrequencyWatering1.text.toString().toInt(),
                binding.plantFrequencyWatering2.text.toString().toInt(),
                binding.plantDateLastWatering.text.toString(),
                binding.plantDateLastNutrients.text.toString(),
                binding.plantUriImage.text.toString()
            )

            // Once finished we return to the list of plants
            val action = AddPlantFragmentDirections.actionAddPlantFragmentToPlantListFragment()
            findNavController().navigate(action)
        } else {
            setTextHelperText() // Verification of editText and definition of helper text according to the results
            showConfirmationDialog()
        }
    }

    /**
     * Updates of a plant and return to the list of plants fragment
     */
    private fun updatePlant() {
        if (isEntryValid()) { // Verification of the validity of the information submitted
            viewModel.updatePlant(
                this.navigationArgs.plantId,
                this.binding.plantFirstName.text.toString(),
                this.binding.plantSecondName.text.toString(),
                this.binding.plantFrequencyWatering1.text.toString().toInt(),
                this.binding.plantFrequencyWatering2.text.toString().toInt(),
                this.binding.plantDateLastWatering.text.toString(),
                this.binding.plantDateLastNutrients.text.toString(),
                this.binding.plantUriImage.text.toString()
            )

            // Once finished we return to the list of plants
            val action = AddPlantFragmentDirections.actionAddPlantFragmentToPlantListFragment()
            findNavController().navigate(action)
        } else {
            setTextHelperText() // Verification of editText content and definition of helpertext
            showConfirmationDialog()
        }
    }

    /**
     * Call before destruction of the fragment
     */
    override fun onDestroyView() {
        super.onDestroyView()
        // Hide the keyboard
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }

    /**
     * Show dialog if data is missing in editText or imageView
     */
    private fun showConfirmationDialog() {

        // Definition of the error message with missing information
        var message = getString(R.string.forgetEditText)
        if (binding.plantFirstName.text.toString().isBlank()) {
            message += "\n\t - First Name"
        }
        if (binding.plantFrequencyWatering1.text.toString().isBlank()) {
            message += "\n\t - Frequency Watering 1"
        }
        if (binding.plantFrequencyWatering2.text.toString().isBlank()) {
            message += "\n\t - Frequency Watering 2"
        }
        if (binding.plantDateLastWatering.text.toString().isBlank()) {
            message += "\n\t - Date Last Watering"
        }
        if (binding.plantDateLastNutrients.text.toString().isBlank()) {
            message += "\n\t - Date Last Nutrients"
        }
        if (binding.plantUriImage.text.toString().isBlank()) {
            message += "\n\t - Image"
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(message)
            .setCancelable(false)
            .setNegativeButton(getString(R.string.ok)) { _, _ -> }
            .show()
    }

    /**
     * Definition of helperText content
     */
    private fun setTextHelperText() {
        binding.plantFirstNameLabel.helperText = setTestFirstNameHelperText()
        binding.plantFrequencyWatering1Label.helperText = setTestFrequencyWatering1HelperText()
        binding.plantFrequencyWatering2Label.helperText = setTestFrequencyWatering2HelperText()
        binding.plantDateLastWateringLabel.helperText = setTestDateLastWateringHelperText()
        binding.plantDateLastNutrientsLabel.helperText = setTestDateLastNutrientsHelperText()
        binding.plantUriImageLabel.helperText = setTestImageHelperText()
    }

    /**
     * Setting helperText content to null
     */
    private fun setNullHelperText() {
        binding.plantFirstNameLabel.helperText = null
        binding.plantFrequencyWatering1Label.helperText = null
        binding.plantFrequencyWatering2Label.helperText = null
        binding.plantDateLastWateringLabel.helperText = null
        binding.plantDateLastNutrientsLabel.helperText = null
        binding.plantUriImageLabel.helperText = null
    }

    /**
     * Checking that an editText of firstName is not empty
     */
    private fun setTestFirstNameHelperText(): String? {
        val firstNameHelper = binding.plantFirstName.text.toString()
        if (firstNameHelper.isBlank()) {
            return "The plant need a first name"
        }
        return null
    }

    /**
     * Checking that the Watering 1 frequency editText is not empty
     */
    private fun setTestFrequencyWatering1HelperText(): String? {
        val frequency1Helper = binding.plantFrequencyWatering1.text.toString()
        if (frequency1Helper.isBlank()) {
            return "The plant need a frequency for winter"
        }
        return null
    }

    /**
     * Checking that the Watering 2 frequency editText is not empty
     */
    private fun setTestFrequencyWatering2HelperText(): String? {
        val frequency2Helper = binding.plantFrequencyWatering2.text.toString()
        if (frequency2Helper.isBlank()) {
            return "The plant need a frequency for summer"
        }
        return null
    }

    /**
     * Verification that an editText of date last Watering is not empty
     */
    private fun setTestDateLastWateringHelperText(): String? {
        val dateLastWateringHelper = binding.plantDateLastWatering.text.toString()
        if (dateLastWateringHelper.isBlank()) {
            return "The plant need a date for the last watering"
        }
        return null
    }

    /**
     * Checking that an editText of date last Nutrients is not empty
     */
    private fun setTestDateLastNutrientsHelperText(): String? {
        val dateLastNutrientsHelper = binding.plantDateLastNutrients.text.toString()
        if (dateLastNutrientsHelper.isBlank()) {
            return "The plant need a date for the last nutrients watering"
        }
        return null
    }


    /**
     * Verication that the editText of image is not empty
     */
    private fun setTestImageHelperText(): String? {
        val imageHelper = binding.plantUriImage.text.toString()
        if (imageHelper.isBlank()) {
            return "The plant need a picture"
        }
        return null
    }
}