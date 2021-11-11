package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            if (binding.textFieldPassword.editText?.text.toString() == binding.textFieldRepeatPassword.editText?.text.toString()) {
                viewModel.registerUser(
                    binding.textFieldLogin.editText?.text.toString(),
                    binding.textFieldPassword.editText?.text.toString(),
                    binding.textFieldName.editText?.text.toString()
                )
            } else binding.textFieldRepeatPassword.error = getString(R.string.error_repeat_password)
        }

        binding.textFieldRepeatPassword.setErrorIconOnClickListener {
            binding.textFieldRepeatPassword.error = null
        }

        viewModel.data.observe(viewLifecycleOwner, {
            AppAuth.getInstance().setAuth(it.id, it.token)
            findNavController().navigateUp()
        })

        return binding.root
    }
}