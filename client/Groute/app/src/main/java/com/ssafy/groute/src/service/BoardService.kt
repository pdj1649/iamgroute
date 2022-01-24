package com.ssafy.groute.src.service

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ssafy.groute.src.dto.BoardDetail
import com.ssafy.groute.util.RetrofitCallback
import com.ssafy.groute.util.RetrofitUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "BoardService_groute"
class BoardService {

    fun getBoardList(): LiveData<List<BoardDetail>> {
        val responseLiveData: MutableLiveData<List<BoardDetail>> = MutableLiveData()
        val boardListRequest: Call<MutableList<BoardDetail>> = RetrofitUtil.boardService.listBoard()
        boardListRequest.enqueue(object : Callback<MutableList<BoardDetail>> {
            override fun onResponse(call: Call<MutableList<BoardDetail>>, response: Response<MutableList<BoardDetail>>) {
                val res = response.body()
                if(response.code() == 200){
                    if (res != null) {
                        responseLiveData.value = res
                        Log.d(TAG, "onResponse: $res")
                    }
                } else {
                    Log.d(TAG, "onResponse: Error Code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<MutableList<BoardDetail>>, t: Throwable) {
                Log.d(TAG, t.message ?: "통신오류")
            }
        })
        return responseLiveData
    }

    fun getBoardDetailList(boardId: Int): LiveData<List<BoardDetail>> {
        val responseLiveData: MutableLiveData<List<BoardDetail>> = MutableLiveData()
        val boardDetailListRequest: Call<MutableList<BoardDetail>> = RetrofitUtil.boardService.listBoardDetail(boardId)
        boardDetailListRequest.enqueue(object : Callback<MutableList<BoardDetail>> {
            override fun onResponse(call: Call<MutableList<BoardDetail>>, response: Response<MutableList<BoardDetail>>) {
                val res = response.body()
                if(response.code() == 200){
                    if (res != null) {
                        responseLiveData.value = res
                        Log.d(TAG, "onResponse: $res")
                    }
                } else {
                    Log.d(TAG, "onResponse: Error Code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<MutableList<BoardDetail>>, t: Throwable) {
                Log.d(TAG, t.message ?: "통신오류")
            }
        })
        return responseLiveData
    }

    fun insertBoardDetail(boardDetail:BoardDetail, callback: RetrofitCallback<Boolean>){
        RetrofitUtil.boardService.insertBoardDetail(boardDetail).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                val res = response.body()
                if(response.code() == 200){
                    if(res != null){
                        callback.onSuccess(response.code(), res)
                    }
                }else{
                    callback.onFailure(response.code())
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                callback.onError(t)
            }

        })
    }

    fun deleteBoardDetail(boardDetailId:Int, callback: RetrofitCallback<Boolean>){
        RetrofitUtil.boardService.deleteBoardDetail(boardDetailId).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                val res = response.body()
                if(response.code() == 200){
                    if(res!=null){
                        callback.onSuccess(response.code(),res)
                    }
                }else{
                    callback.onFailure(response.code())
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                callback.onError(t)
            }

        })
    }
}