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

import android.content.Context
import android.os.strictmode.InstanceCountViolation
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// singleton pattern으로 작성하는 것이 바람직
// entites는 테이블의 갯수에 따라서 갯수가 정해진다.
// 현재는 SleepNight에 대한 데이터베이스만 존재하므로 하나만 설정
// 스키마를 변경할 떄 마다 버전 번호를 올려야합니다.
// exportSchema는 기본적으로 true이며 데이터베이스의 스키마를 폴더에 저장합니다.
@Database(entities = [SleepNight::class],version = 1, exportSchema = false)
abstract class SleepDatabase : RoomDatabase(){
    abstract val sleepDatabaseDao : SleepDatabaseDao

    companion object{

        @Volatile // 모든 읽기 쓰기가 주 메모리에서 수행되도록 합니다. 이로써 다른 쓰레드에서 작업한 내용이 그 외 다른 쓰레드에서도 즉시 표시되어 확인 할 수 있게됩니다.
        private var INSTANCE : SleepDatabase? = null

        fun getInstance(context : Context) : SleepDatabase {
            synchronized(this){ // 여러 쓰레드에서 동시에 데이터베이스를 만들려고 하는 경우 문제가 발생할 수 있기 때문에 synchronized사용
                var instance = INSTANCE

                if(instance == null){
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            SleepDatabase::class.java,
                            "sleep_history_database"
                    )
                            .fallbackToDestructiveMigration()
                            .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}