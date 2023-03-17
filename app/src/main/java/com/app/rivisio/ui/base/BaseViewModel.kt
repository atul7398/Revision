package com.app.rivisio.ui.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.rivisio.data.repository.MainRepository
import com.app.rivisio.utils.NetworkResult
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response

open class BaseViewModel(private val mainRepository: MainRepository) : ViewModel() {

    var loggedOut = MutableLiveData<Boolean>()

    suspend fun <T : Any> handleApi(
        execute: suspend () -> Response<T>
    ): NetworkResult<T> {
        return try {
            val response = execute()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                NetworkResult.Success(body)
            } else {
                NetworkResult.Error(code = response.code(), message = response.message())
            }
        } catch (e: HttpException) {
            NetworkResult.Error(code = e.code(), message = e.message())
        } catch (e: Throwable) {
            NetworkResult.Exception(e)
        }
    }

    fun onTokenExpire() {
        viewModelScope.launch {
            //dataManager.setUserLoggedOut()
            //loggedOut.value = true
        }
    }

}
