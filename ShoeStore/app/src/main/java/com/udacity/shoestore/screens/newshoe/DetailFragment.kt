package com.udacity.shoestore.screens.newshoe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.udacity.shoestore.MainActivityViewModel
import com.udacity.shoestore.databinding.DetailFragmentBinding

class DetailFragment : Fragment() {
    private lateinit var binding: DetailFragmentBinding
    private val mainActivityViewModel by activityViewModels<MainActivityViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DetailFragmentBinding.inflate(inflater, container, false)

        binding.viewModel = mainActivityViewModel
        binding.lifecycleOwner = this

        if (mainActivityViewModel.eventOnSave.value == true) {
            mainActivityViewModel.clearOnSave()
        }

        binding.saveButton.setOnClickListener {
            if (mainActivityViewModel.validate()) {
                mainActivityViewModel.addShoe()
                mainActivityViewModel.setOnSave()
                requireView().findNavController()
                    .navigate(DetailFragmentDirections.actionADDDetialFragmentToShoeListFragment())
            } else {
                Toast.makeText(context, "please complete all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.cancelButton.setOnClickListener {
            requireView().findNavController()
                .navigate(DetailFragmentDirections.actionCANCELDetialFragmentToShoeListFragment())
        }

        return binding.root
    }

}