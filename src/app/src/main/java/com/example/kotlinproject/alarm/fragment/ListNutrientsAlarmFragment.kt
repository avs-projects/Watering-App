package com.example.kotlinproject.alarm.fragment

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinproject.R
import com.example.kotlinproject.alarm.viewmodel.PlantAlarmNutrientsViewModel
import com.example.kotlinproject.alarm.viewmodel.PlantAlarmNutrientsViewModelFactory
import com.example.kotlinproject.alarm.adapter.PlantListNutrientsAlarmAdapter
import com.example.kotlinproject.data.Plant
import com.example.kotlinproject.databinding.FragmentListNutrientsAlarmBinding
import com.example.kotlinproject.utils.DatePickerFragment
import com.example.kotlinproject.data.PlantApplication
import com.example.kotlinproject.utils.onQueryTextChanged
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import kotlin.properties.Delegates

/**

Fragment of the list of nutrient waterings to do

 */

@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalCoroutinesApi

class ListNutrientsAlarmFragment : Fragment() {

    lateinit var plant: Plant
    var frequence by Delegates.notNull<Int>()

    // ViewModel recovery of database content
    private val viewModel: PlantAlarmNutrientsViewModel by activityViewModels {
        PlantAlarmNutrientsViewModelFactory(
            (activity?.application as PlantApplication).database.plantDao()
        )
    }

    private var _binding: FragmentListNutrientsAlarmBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        setHasOptionsMenu(true) // Added options menu (search for plant)

        _binding = FragmentListNutrientsAlarmBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*
        When clicking on a plant, display a dialog to know what watering the user will do
         */
        val adapter = PlantListNutrientsAlarmAdapter {
            showConfirmationDialog(it.id)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.adapter = adapter

        /*
        Attach an observer to all itemps to have an automatic update when changing date
         For example: when changing the watering date, plant disappears from the list
         */
        viewModel.plants.observe(this.viewLifecycleOwner) { items ->
            items.let {
                adapter.submitList(it)
            }
        }

        setHasOptionsMenu(true) // Added options menu (search for plant)
    }

    /**
     * Search bar in the menu to find a plant
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)

        val searchItem = menu.findItem(R.id.menu_search)
        val searchView = searchItem.actionView as androidx.appcompat.widget.SearchView

        searchView.onQueryTextChanged {
            viewModel.searchQuery.value = it
        }
    }

    /**
     * Watering Condition Dialog Display
     */
    private fun showConfirmationDialog(id: Int) {
        viewModel.retrievePlant(id).observe(this.viewLifecycleOwner) { selectedPlant ->
            plant = selectedPlant
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.watering_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no_1day)) { _, _ ->
                updatePlantOneDay(plant) // Changing the date of the next nutrient watering to the next day
            }
            .setNeutralButton(getString(R.string.yes)) { _, _ ->
                updatePlantFreq(plant) // Changing the date of the next nutrient watering depending on the frequency
            }
            .setPositiveButton(getString(R.string.no_ChooseDate)) { _, _ ->
                val datePickerFragment = DatePickerFragment()
                val supportFragmentManager = requireActivity().supportFragmentManager

                supportFragmentManager.setFragmentResultListener(
                    "REQUEST_KEY",
                    viewLifecycleOwner
                ) { resultKey, bundle ->
                    if (resultKey == "REQUEST_KEY") {
                        val date = bundle.getString("SELECTED_DATE")
                        if (compareDate(date.toString())) {
                            showPbDateChooseDialog()
                        } else {
                            updatePlantChooseDate(
                                date.toString(),
                                plant
                            ) // Modification of the date of the next nutrient watering with the date entered
                        }
                    }
                }

                // Display a clickable calendar when choosing a date
                datePickerFragment.show(supportFragmentManager, "DatePickerFragment")
            }
            .show()
    }

    /**
     * Update of the date of the next nutrient watering according to the frequency
     *
     * The date of the last watering becomes the date of the day
     */
    private fun updatePlantFreq(plant: Plant) {
        viewModel.updatePlant(
            plant.id,
            plant.firstName,
            plant.secondName,
            plant.frequencyWatering1.toString().toInt(),
            plant.frequencyWatering2.toString().toInt(),
            plant.dateLastWatering,
            getLocalDate(), // Retrieval of today's date for updating the date of the last watering
            plant.dateNextNutrients,
            /*
            Retrieval of the updated date according to the frequency for the next watering
             */
            setDateNextNutrients(
                plant.frequencyWatering1.toString().toInt(),
                plant.frequencyWatering2.toString().toInt(),
                getLocalDate()
            ),
            plant.plantPhoto
        )
    }

    /**
     * Updating the date of the next nutrient watering to the next day
     */
    private fun updatePlantOneDay(plant: Plant) {
        viewModel.updatePlant(
            plant.id,
            plant.firstName,
            plant.secondName,
            plant.frequencyWatering1.toString().toInt(),
            plant.frequencyWatering2.toString().toInt(),
            plant.dateLastWatering,
            plant.dateLastNutrients,
            plant.dateNextWatering,
            setDateNextDay(),
            plant.plantPhoto
        )
    }

    /**
     * Update of the date of the next nutrient watering with the date submitted
     */
    private fun updatePlantChooseDate(dateFinal: String, plant: Plant) {
        viewModel.updatePlant(
            plant.id,
            plant.firstName,
            plant.secondName,
            plant.frequencyWatering1.toString().toInt(),
            plant.frequencyWatering2.toString().toInt(),
            plant.dateLastWatering,
            plant.dateLastNutrients,
            plant.dateNextWatering,
            dateFinal,
            plant.plantPhoto
        )

    }

    /**
     * Function creation date next watering nutrients according to the frequency
     */
    private fun setDateNextNutrients(
        frequencyWatering1: Int,
        frequencyWatering2: Int,
        dateLastNutrients: String
    ): String {

        // Recovery of the month
        val monthLocal = LocalDateTime.now().monthValue

        // Utilisation des frequences (Saisons)
        frequence =
            if (monthLocal in 1..6) { // If the month is between January and June take frequency 1
                frequencyWatering1 * 3 // x3 to respect the topic regarding nutrient watering
            } else { // Otherwise take frequency 2
                frequencyWatering2 * 3
            }

        /*
        Formatting the date in a particular form
         */
        val dateList: List<String> = dateLastNutrients.split("-")
        val day = dateList[2].toInt()
        val month = dateList[1].toInt()
        val year = dateList[0].toInt()

        // Incrementing the current date with the frequency value
        val period = Period.ofDays(frequence)
        val date = LocalDate.of(year, month, day)
        val modifiedDate = date.plus(period)

        return modifiedDate.toString()
    }

    /**
     * Function creation date next watering nutrients to the next day
     */
    private fun setDateNextDay(
    ): String {

        // Current date
        val dayLocal = LocalDateTime.now().dayOfMonth
        val monthLocal = LocalDateTime.now().monthValue
        val yearLocal = LocalDateTime.now().year
        val date = LocalDate.of(yearLocal, monthLocal, dayLocal)

        // Increment the current date by one day
        val period = Period.ofDays(1)
        val modifiedDate = date.plus(period)

        return modifiedDate.toString()
    }

    /**
     * Recovery of today's date
     */
    private fun getLocalDate(): String {
        val dayLocal = LocalDateTime.now().dayOfMonth
        val monthLocal = LocalDateTime.now().monthValue
        val yearLocal = LocalDateTime.now().year

        val date = LocalDate.of(yearLocal, monthLocal, dayLocal)

        return date.toString()
    }

    /**
     * Date comparison submitted when choosing the date with today's date
     */
    private fun compareDate(dateChoose: String): Boolean {
        val dayLocal = LocalDateTime.now().dayOfMonth
        val monthLocal = LocalDateTime.now().monthValue
        val yearLocal = LocalDateTime.now().year

        val date = LocalDate.of(yearLocal, monthLocal, dayLocal)

        val dateList: List<String> = dateChoose.split("-")
        val dayChoosed = dateList[2].toInt()
        val monthChoosed = dateList[1].toInt()
        val yearChoosed = dateList[0].toInt()

        val dateChooseF = LocalDate.of(yearChoosed, monthChoosed, dayChoosed)

        // If the submitted date is after today's date return true
        return dateChooseF.isBefore(date)

    }

    /**
     * Display of the dialog if the date of choose date is lower than that of the day
     */
    private fun showPbDateChooseDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.dateError))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.ok)) { _, _ -> }
            .show()
    }

}