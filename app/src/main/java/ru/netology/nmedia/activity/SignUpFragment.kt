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
import ru.netology.nmedia.databinding.FragmentSignUpBinding
import ru.netology.nmedia.viewmodel.SignUpViewModel

class SignUpFragment : Fragment() {

    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSignUpBinding.inflate(
            inflater,
            container,
            false
        )

        binding.buttonSignUp.setOnClickListener {
            if (binding.textPassword.text.toString() == binding.repeatTextPassword.text.toString()) {
                viewModel.registerUser(binding.textLogin.text.toString(), binding.textPassword.text.toString(), binding.textName.text.toString())
            } else {
                Toast.makeText(
                    activity,
                    R.string.error_repeat_password,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        viewModel.data.observe(viewLifecycleOwner, {
            AppAuth.getInstance().setAuth(it.id, it.token)
            findNavController().navigateUp()
        })

        return binding.root
    }
}