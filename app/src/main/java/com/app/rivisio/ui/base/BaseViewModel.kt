package com.app.rivisio.ui.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.rivisio.data.repository.Repository
import com.app.rivisio.utils.NetworkResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber

open class BaseViewModel(private val repository: Repository) : ViewModel() {

    var loggedOut = MutableLiveData<Boolean>()

    suspend fun <T : Any> handleApi(
        execute: suspend () -> Response<T>
    ): NetworkResult<T> {
        return withContext(Dispatchers.IO) {
            try {
                val response = execute()
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    NetworkResult.Success(body)
                } else {
                    if (response.errorBody() == null)
                        NetworkResult.Error(code = response.code(), message = response.message())
                    else{
                        val type = object : TypeToken<ErrorResponse>() {}.type
                        var errorResponse: ErrorResponse? = Gson().fromJson(response.errorBody()!!.charStream(), type)
                        if (errorResponse != null){
                            NetworkResult.Error(errorResponse.code,errorResponse.messages)
                        }
                        else{
                            NetworkResult.Error(code = response.code(), message = response.message())
                        }
                    }
                }
            } catch (e: HttpException) {
                Timber.e(e)
                NetworkResult.Error(code = e.code(), message = e.message())
            } catch (e: Throwable) {
                Timber.e(e)
                NetworkResult.Exception(e)
            }
        }
    }

    fun onTokenExpire() {
        viewModelScope.launch {
            //dataManager.setUserLoggedOut()
            //loggedOut.value = true
        }
    }

}
