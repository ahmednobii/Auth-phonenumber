package net.youbox.network.paging.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import co.youbox.Network.API.MainAPI
import net.youbox.model.InterestsFeed
import net.youbox.network.paging.NetworkState
import net.youbox.responses.InterestResponse
import retrofit2.Call
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.Executor

class PostsDataSource constructor(
    val api: MainAPI,
    val token: String,
    private val excutor: Executor
) :
    PageKeyedDataSource<String, InterestsFeed>() {
    val TAG = "Paging"
    private lateinit var postPage: MediatorLiveData<InterestResponse>
    private var retry: (() -> Any)? = null
    val networkState = MutableLiveData<NetworkState>()
    val intialLoad = MutableLiveData<NetworkState>()
    fun retryAllFailed() {
        val pevRetry = retry
        retry = null
        pevRetry?.let {
            excutor.execute {
                it.invoke()
            }
        }

    }

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<String, InterestsFeed>
    ) {
        Log.d("Home" , "Loading  intial starts")

        val request = api.getNewsFeed(PAGE.toString(),"Bearer $token")
        networkState.postValue(NetworkState.LOADING)
        networkState.postValue(NetworkState.LOADING)

        try {
            val response = request.execute()
            val data = response.body()
          val items = data?.posts?.map { it } ?: emptyList()
            Log.d("Home",response.code().toString())

            retry = null
            networkState.postValue(NetworkState.LOADED)
            intialLoad.postValue(NetworkState.LOADED)
            callback.onResult(items, null ,(PAGE+1).toString())

        } catch (ioException: IOException) {
            retry = {
                loadInitial(params, callback)
            }

            val errorState =
                NetworkState.error(
                    ioException.message ?: "Something went Wrong"
                )
            Log.d("