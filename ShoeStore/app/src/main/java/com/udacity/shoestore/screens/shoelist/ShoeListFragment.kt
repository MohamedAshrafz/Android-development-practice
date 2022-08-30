package com.udacity.shoestore.screens.shoelist

import android.os.Bundle
import android.view.*
import android.widget.Space
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.udacity.shoestore.MainActivityViewModel
import com.udacity.shoestore.R
import com.udacity.shoestore.databinding.ShoeViewBinding
import com.udacity.shoestore.databinding.ShoelistFragmentBinding

class ShoeListFragment : Fragment() {

    lateinit var binding: ShoelistFragmentBinding

    companion object {
        const val SPACING_MIN = 10 //10dp of spacing between each CardView
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding =
            ShoelistFragmentBinding.inflate(inflater, container, false)

        binding.addingNewShoeFloatingButton.setOnClickListener {
            requireView().findNavController()
                .navigate(ShoeListFragmentDirections.actionShoeListFragmentToNewShoeFragment())
        }

        val mainActivityViewModel by activityViewModels<MainActivityViewModel>()

        // inflate every shoe as custom view into the linear layout
        mainActivityViewModel.shoeList.observe(viewLifecycleOwner, Observer { shoeList ->
            for (shoeItem in shoeList) {
                val shoeViewBinding = ShoeViewBinding.inflate(
                    inflater,
                    container,
                    false
                )

                // using data binding to display the details of each shoe
                shoeViewBinding.shoe = shoeItem
                shoeViewBinding.lifecycleOwner = this

                binding.shoeListLinearLayout.addView(shoeViewBinding.shoeViewLayout)

                // adding spacing to distinct the elevation of the CardView
                val space = Space(context)
                space.minimumHeight = SPACING_MIN
                binding.shoeListLinearLayout.addView(space)
            }
        })

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.logout_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logOut -> requireView().findNavController()
                .navigate(ShoeListFragmentDirections.actionShoeListFragmentToLogInFragment())
        }
        return super.onOptionsItemSelected(item)
    }
}