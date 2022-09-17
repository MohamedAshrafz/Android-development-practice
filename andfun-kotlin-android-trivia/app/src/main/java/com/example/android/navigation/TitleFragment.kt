package com.example.android.navigation

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.android.navigation.databinding.FragmentTitleBinding

class TitleFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentTitleBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_title, container, false
        )
        // needs javaCompatibility
//        binding.playButton.setOnClickListener {
//            Navigation.findNavController(it).navigate(R.id.action_titleFragment_to_gameFragment)
//            // Navigation: class for all navigation
//            // findNavController: for getting the navigation controller associated with the navigation host
//            // of the current view (which is in this case is the playButton)
//            // action_titleFragment_to_gameFragment: is the link which we made in the navigation.xml
//        }

        // needs javaCompatibility
//        binding.playButton.setOnClickListener { view: View ->
//            view.findNavController().navigate(R.id.action_titleFragment_to_gameFragment)
//        }

        // does not need javaCompatibility
        // The complete onClickListener with Navigation using createNavigateOnClickListener
        binding.playButton.setOnClickListener {
            requireView().findNavController()
                .navigate(TitleFragmentDirections.actionTitleFragmentToGameFragment())
        }
        // for enabling the menu
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.overflow_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }
}