package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentSignInBinding
import ru.netology.nmedia.viewmodel.SignInViewModel
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SignInFragment : Fragment() {

    @Inject
    lateinit var appAuth: AppAuth

    private val viewModel: SignInViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSignInBinding.inflate(
            inflater,
            container,
            false
        )

        binding.buttonSignIn.setOnClickListener {
            viewModel.updateUser(
                binding.textFieldLogin.editText?.text.toString(),
                binding.textFieldPassword.editText?.text.toString()
            )
        }

        binding.textFieldPassword.setErrorIconOnClickListener {
            binding.textFieldPassword.error = null
        }

        viewModel.data.observe(viewLifecycleOwner, {
            appAuth.setAuth(it.id, it.token)
            findNavController().navigateUp()
        })

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            if (state.errorLogin) {
                binding.textFieldPassword.error = getString(R.string.error_login)
            }
        }
        return binding.root
    }
}