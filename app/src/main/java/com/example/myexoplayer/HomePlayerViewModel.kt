package com.example.myexoplayer


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomePlayerViewModel : ViewModel() {

    private val mutableViewState: MutableLiveData<Event<HomePlayerViewState>> = MutableLiveData()

    val viewState: LiveData<Event<HomePlayerViewState>>
        get() = mutableViewState

    fun dispatch(event: HomePlayerEvent) {
        when (event) {
            HomePlayerEvent.HeaderPlayerGone -> changeState(HomePlayerViewState.MiniPlayerVisible)
            HomePlayerEvent.HeaderPlayerVisible -> changeState(HomePlayerViewState.MiniPlayerGone)
        }
    }

    private fun changeState(viewState: HomePlayerViewState) =
        mutableViewState.postValue(viewState.toEvent())
}

sealed class HomePlayerEvent() {
    object HeaderPlayerVisible : HomePlayerEvent()
    object HeaderPlayerGone : HomePlayerEvent()
}

sealed class HomePlayerViewState() {
    object MiniPlayerVisible : HomePlayerViewState()
    object MiniPlayerGone : HomePlayerViewState()
}
