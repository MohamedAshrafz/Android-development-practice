package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.repository.AsteroidSelection

class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMainBinding.inflate(inflater)

        val viewModelFactory = MainViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val adaptor =
            AsteroidRecyclerViewAdaptor(AsteroidRecyclerViewAdaptor.AsteroidItemClickListener { asteroid ->
                viewModel.onSelectAsteroid(asteroid)
            })

        binding.asteroidsRecyclerView.adapter = adaptor

        viewModel.selectedAsteroid.observe(viewLifecycleOwner, Observer { selectedAsteroid ->
            if (selectedAsteroid != null) {
                requireView().findNavController()
                    .navigate(MainFragmentDirections.actionShowDetail(selectedAsteroid))
                viewModel.onClearSelected()
            }
        })

        viewModel.loadingStatus.observe(viewLifecycleOwner, Observer { status ->
            when (status) {
                MainViewModel.LoadingStatusEnum.LOADING -> {
                    binding.statusLoadingWheel.visibility = View.VISIBLE
                }
                else -> {
                    binding.statusLoadingWheel.visibility = View.GONE
                }
            }
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.show_all_menuItem -> viewModel.selectFilter(AsteroidSelection.ALL)
            R.id.show_today_menuItem -> viewModel.selectFilter(AsteroidSelection.TODAY)
            else -> viewModel.selectFilter(AsteroidSelection.WEEK)
        }
        return true
    }
}
