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

package com.example.android.trackmysleepquality.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

// Database DAO
// DAO = Database Access Object
// 데이터베이스에 접근하는 객체를 의미

// 만드는 법
// 1. dao annotation사용
// 2. function name과 반환형등 메서드를 만들어줍니다.

// Dao 어노테이션
// 기본적으로 Insert , Update , Delete 어노테이션이 존재
// 이후 데이터베이스 연산은 QUERY문을 통해 처리를 할 수 있습니다.
@Dao
interface SleepDatabaseDao{
    @Insert
    fun insert(night : SleepNight)

    @Update
    fun update(night : SleepNight)

    @Query("SELECT * FROM daily_sleep_quality_table WHERE nightId = :key")
    fun get(key : Long) : SleepNight

    @Query("DELETE FROM daily_sleep_quality_table")
    fun clear()

    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY nightId DESC")
    fun getAllNights() : LiveData<List<SleepNight>> // Room 에서는 LiveData를 다시 되 찾아올 수 있습니다.
    // 즉, Room은 데이터베이스가 업데이트 될 떄마다 라이브데이터가 업데이트 되도독 할 수 있습니다.
    // 즉, AllNights 목록을 한 번만 가져오면 된다는 의미입니다.

    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY nightId DESC LIMIT 1")
    fun getTonight() : SleepNight?

}
