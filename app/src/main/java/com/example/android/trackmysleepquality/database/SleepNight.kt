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

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// TODO (01) Create the SleepNight class.
// Room data class 만드는 법
// 1. data class 생성
// 2. entity annotation 적용
// 3. entity annotation 에서 선택적으로 tableName을 적용하면 table의 이름을 식별해줄 수 있습니다.
// 4. primaryKey 적용은 필수적임

// Entity 어노테이션을 사용, data class
// table name을 식별하는 것은 추가적인 것 (Optional)
// but, 이름을 짓는다면 테이블을 포함하는 이름으로 짓는 것이 좋습니다.

// PrimaryKey는 필수적입니다.
// ColumnInfo = 컬럼 명을 지정해줄 수 있음
@Entity(tableName = "daily_sleep_quality_table")
data class SleepNight(
        @PrimaryKey(autoGenerate = true)
        var nightId : Long = 0L,
        @ColumnInfo(name = "start_time_milli")
        var startTimeMilli : Long = System.currentTimeMillis(),
        @ColumnInfo(name = "end_time_milli")
        var endTimeMilli : Long = startTimeMilli,
        @ColumnInfo(name = "quality_rating")
        var sleepQuality : Int = -1
)