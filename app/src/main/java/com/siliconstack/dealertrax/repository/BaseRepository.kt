package com.siliconstack.dealertrax.repository

import android.arch.lifecycle.MutableLiveData
import com.bol.instantapp.exception.AppException
import com.bol.instantapp.exception.NoNetworkException
import com.siliconstack.dealertrax.AppApplication
import com.siliconstack.dealertrax.R
import com.siliconstack.dealertrax.model.ErrorResponse
import com.siliconstack.dealertrax.model.Resource
import com.siliconstack.dealertrax.view.utility.Utility
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class BaseRepository{
    companion object {
        open class MyRetrofitCallback<T>(var data: MutableLiveData<Resource<T>>) : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if(response.code()==401)
                {
                    try {
                        response.errorBody()?.let {
                            val jsonObject = JSONObject(it.string())
                            val message = jsonObject.get("Message") as String? ?: ""
                            data.value = Resource.unauthorized(message) as Resource<T>?

                        }
                    }
                    catch (exp:Exception){
                        data.value= Resource.unauthorized(exp.message) as Resource<T>?
                    }
                }
                else {
                    if (response.body() != null)
                        data.value = Resource.success(response.body());
                    else {
                        var errorResponse = ErrorResponse()

                        if (response.errorBody() != null) {
                            try {
                                val jsonObject = JSONObject(response.errorBody()!!.string())
                                if (jsonObject != null) {
                                    errorResponse.message = jsonObject.get("Message") as String? ?: ""
                                    data.value = Resource.info(errorResponse) as Resource<T>?
                                } else {
                                    data.value = Resource.error(AppException(Exception("Parsing exception " + jsonObject.toString())))
                                }
                            } catch (exp: Exception) {
                                data.value = Resource.error(AppException(Exception("Unknown exception " + exp.toString())))
                            }

                        } else data.value = Resource.error(AppException(Exception("Unknown exception")))

                    }
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                var exception = AppException(t)
                if(!Utility.isNetworkAvailable(AppApplication.instance)) {
                    exception = NoNetworkException(Exception(AppApplication.instance.getString(R.string.msg_no_network)))
                }
                data.value = Resource.error(exception)
            }

        }


    }
}