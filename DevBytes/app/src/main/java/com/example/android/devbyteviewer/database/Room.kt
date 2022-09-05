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
 *
 */

package com.example.android.devbyteviewer.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface VideoDao {
    @Query("SELECT * FROM databasevideo")
    fun getVideos(): LiveData<List<DatabaseVideo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg videos: DatabaseVideo)
}

@Database(entities = [DatabaseVideo::class], version = 1)
abstract class VideoDatabase : RoomDatabase() {

    abstract val videoDao: VideoDao

    companion object {

        @Volatile
        private var INSTANCE: VideoDatabase? = null

        // singleton pattern
        // only one instance of the database is allowed to exist
        // if there is no database make a one, if there is exist a database return it
        fun getInstance(context: Context): VideoDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        VideoDatabase::class.java,
                        "videos_database"
                    )
                        .build()

                    INSTANCE = instance
                }
                // returning the instance not INSTANCE to benefit from the smart casting
                // smart casting only works with local vars not class vars
                return instance
            }
        }
    }
}

