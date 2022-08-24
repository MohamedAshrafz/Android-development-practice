package com.udacity.shoestore.screens.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.udacity.shoestore.R
import com.udacity.shoestore.databinding.LoginFragmentBinding

class LogInFragment : Fragment() {

    private lateinit var binding: LoginFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            LoginFragmentBinding.inflate(inflater, container, false)

        binding.loginButton.setOnClickListener {
            requireView().findNavController()
                .navigate(LogInFragmentDirections.actionLogInFragmentToWelcomeFragment())
        }
        binding.signupButton.setOnClickListener {
            requireView().findNavController()
                .navigate(LogInFragmentDirections.actionLogInFragmentToWelcomeFragment())
        }

        return binding.root
    }
}