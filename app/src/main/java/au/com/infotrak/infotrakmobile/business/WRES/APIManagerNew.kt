package au.com.infotrak.infotrakmobile.business.WRES

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class APIManagerNew {

    fun getCustomerList(apiUrl: String): JSONObject? {

        val httpClientBuilder = OkHttpClient.Builder()
                .readTimeout(1, TimeUnit.DAYS) // Server doesn't send you response data
        val httpClient = httpClientBuilder.build()
        val request = Request.Builder()
                .url(apiUrl)
                .build()
        var response: Response
        try {
            response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val result = response.body()!!.string()
                return JSONObject(result)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

        return null
    }

    fun getJobsiteList(apiUrl: String): JSONObject? {

        val httpClientBuilder = OkHttpClient.Builder()
                .readTimeout(1, TimeUnit.DAYS) // Server doesn't send you response data
        val httpClient = httpClientBuilder.build()
        val request = Request.Builder()
                .url(apiUrl)
                .build()
        var response: Response
        try {
            response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val result = response.body()!!.string()
                return JSONObject(result)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

        return null
    }

}