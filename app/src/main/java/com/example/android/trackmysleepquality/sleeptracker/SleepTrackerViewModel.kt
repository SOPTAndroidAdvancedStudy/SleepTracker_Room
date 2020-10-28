/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.*

/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(
        val database: SleepDatabaseDao,
        application: Application) : AndroidViewModel(application) {
    // ViewModel은 데이터베이스의 데이터에 액세스 해야합니다.
    // 이는 DAO에 정의된 인터페이스를 통해 이루어 집니다.
    // 그래서 SleepDatabaseDao의 인스턴스를 매개변수로 전달해줍니다.
    // resource style string과 같은 것에 액세스하기 위해 application이 필요합니다.

    // 1. job 생성
    private var viewModelJob = Job() // 모든 코루틴을 관리하려면 job객체가 필요하다
    // 이 viewModelJob을 통해 ViewModel이 시작한 모든 코루틴을 취소할 수 있습니다.
    // ViewModel은 더이상 파괴되지 않으므로 코루틴을 끝내주어야할 것이 필요합니다
    // ViewModel이 destoryed 될떄, onCleared가 호출된다
    // 이 메소드를 override하여 viewModel에서 실행되는 모든 코루틴을 취소시킬 수 있다.

    // 2. 코루틴이 실행될 수 있도록 스코프가 필요함
    // Dispatcher.Main -> 코루틴이 UI스코프에서 실행될 것이고 이는 즉 ui쓰레드에서 진행된다는 의미이다.
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var tonight = MutableLiveData<SleepNight?>()

    private val nights = database.getAllNights()

    val nightString = Transformations.map(nights){nights ->
        formatNights(nights,application.resources) // 문자열 리소스에 액세스 할 수 있기 떄문입니다
    }

    init {
        initializeTonight()
    }

    private fun initializeTonight() {
        uiScope.launch {
            tonight.value = getTonightFromDatabase()
        }
    }

    private suspend fun getTonightFromDatabase(): SleepNight? { // 코루틴에서 호출한 함수이기에 suspend와 ui쓰레드의 진행을 방해하지 않기 위해서 사용
        return withContext(Dispatchers.IO){
            var night = database.getTonight()
            if(night?.endTimeMilli != night?.startTimeMilli){
                night = null
            }
            night
        }
    }

    // ui에 작업을 해야하는 경우 ui쓰레드에서 코루틴을 호출하는 것이 맞습니다.
    // 근데 만약 그 코루틴에서 해결해야하는 것이 longrunningWork 즉 시간이 오래걸리는 경우
    // 코루틴을 사용했기 때문에 결과를 기다리는 동안 UI쓰레드의 진행을 막지 않을 것입니다
    fun onStartTracking(){
        uiScope.launch {
            val newNight = SleepNight()

            insert(newNight)
            tonight.value = getTonightFromDatabase()
        }
    }

    // 이 함수는 UI와 아무 상관없는 일을 한다
    // 그래서 컨텍스트를 ioContext로 변경해 우리가 작업할 수 있게됩니다
    // 정확히는 ui쓰레드와 관련없는 최적화된 어디 사이드로 가서 작업을 진행합니다
    private suspend fun insert(newNight: SleepNight) {
        withContext(Dispatchers.IO){
            database.insert(newNight)
        }
    }

    fun onStopTracking(){
        uiScope.launch {
            // 코틀린에서 return@label구문은 이문이 반환하는 여러 중첩 함수 중 어떤 함수를 지정합니다 이 경우엔 launch입니다 람다가 아니라
            val oldNight = tonight.value ?: return@launch

            oldNight.endTimeMilli = System.currentTimeMillis()

            update(oldNight)
        }
    }

    private suspend fun update(night : SleepNight){
        withContext(Dispatchers.IO){
            database.update(night)
        }
    }

    fun onClear(){
        uiScope.launch {
            clear()
            tonight.value = null
        }
    }

    suspend fun clear() {
        withContext(Dispatchers.IO){
            database.clear()
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel() // ViewModel의 모든 코루틴에게 종료하라고 지시
    }
}

