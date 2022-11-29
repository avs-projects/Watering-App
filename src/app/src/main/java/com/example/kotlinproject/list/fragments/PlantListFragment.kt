package com.example.kotlinproject.list.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinproject.utils.onQueryTextChanged
import com.example.kotlinproject.AlarmActivity
import com.example.kotlinproject.data.PlantApplication
import com.example.kotlinproject.R
import com.example.kotlinproject.databinding.FragmentPlantListBinding
import com.example.kotlinproject.list.adapter.PlantListAdapter
import com.example.kotlinproject.list.viewmodel.PlantViewModel
import com.example.kotlinproject.list.viewmodel.PlantViewModelFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**

Fragment allowing the display of the list of all the plants

 */

@ExperimentalCoroutinesApi

class PlantListFragment : Fragment() {

    // ViewModel recovery of database content
    private val viewModel: PlantViewModel by activityViewModels {
        PlantViewModelFactory(
            (activity?.application as PlantApplication).database.plantDao()
        )
    }

    private var _binding: FragmentPlantListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPlantListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // If clicked on plant, direction to the details of this same plant with its id
        val adapter = PlantListAdapter {
            val action =
                PlantListFragmentDirections.actionPlantListFragmentToPlantDetailFragment(it.id)
            this.findNavController().navigate(action)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.adapter = adapter

        // Attach an observer to all the plants to have an automatic update when changing date
        viewModel.plants.observe(this.viewLifecycleOwner) { items ->
            items.let {
                adapter.submitList(it)
            }
        }

        setHasOptionsMenu(true) // Added options menu (search for plant)

        // Click on the add button, launches the add plant fragment
        binding.fabAddPlant.setOnClickListener {
            val action =
                PlantListFragmentDirections.actionPlantListFragmentToAddPlantFragment(getString(R.string.add_fragment_title))
            this.findNavController().navigate(action)
        }

        // Click on the alarm button, launch the alarm activity to see the plants to be watered during the day
        binding.fabGoToAlarm.setOnClickListener {
            val intent = Intent(activity, AlarmActivity::class.java)
            activity?.startActivity(intent)
        }
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
}