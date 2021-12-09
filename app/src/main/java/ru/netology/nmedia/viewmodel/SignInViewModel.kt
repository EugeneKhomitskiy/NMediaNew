package ru.netology.nmedia.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dto.Auth
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.model.FeedModelState
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    val data = MutableLiveData<Auth>()

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    fun updateUser(name: String, pass: String) {
        viewModelScope.launch {
            try {
                val response = apiService.updateUser(name, pass)
                if (!response.isSuccessful) {
                    throw ApiError(response.message())
                }
                val body = response.body() ?: throw ApiError(response.message())
                data.value = Auth(body.id, body.token)
            } catch (e: IOException) {
                throw NetworkError
            } catch (e: Exception) {
                _dataState.postValue(FeedModelState(errorLogin = true))
            }
        }
    }
}

