package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentSignInBinding
import ru.netology.nmedia.viewmodel.SignInViewModel

class SignInFragment : Fragment() {

    private val viewModel: SignInViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSignInBinding.inflate(
            inflater,
            container,
            false
        )

        binding.buttonSignIn.setOnClickListener {
            viewModel.updateUser(binding.textLogin.text.toString(),binding.textPassword.text.toString())
        }

        viewModel.data.observe(viewLifecycleOwner, {
            AppAuth.getInstance().setAuth(it.id, it.token)
            findNavController().navigateUp()
        })

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            if (state.errorLogin) {
                Toast.makeText(
                    activity,
                    R.string.error_login,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        return binding.root
    }
}