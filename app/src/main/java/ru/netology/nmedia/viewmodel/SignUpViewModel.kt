package ru.netology.nmedia.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dto.Auth
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    val data = MutableLiveData<Auth>()

    fun registerUser(name: String, login: String, pass: String) {
        viewModelScope.launch {
            try {
                val response = apiService.registerUser(name, login, pass)
                if (!response.isSuccessful) {
                    throw ApiError(response.message())
                }
                val body = response.body() ?: throw ApiError(response.message())
                data.value = Auth(body.id, body.token)
            } catch (e: IOException) {
                throw NetworkError
            } catch (e: Exception) {
                throw UnknownError()
            }
        }
    }
}