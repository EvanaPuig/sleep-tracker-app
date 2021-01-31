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
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// export schema is true by default and saves the schema of the DB to a folder
// this provides us with a version history of my DB for complex DB's
// for this app this is not needed
@Database(entities = [SleepNight::class], version = 1, exportSchema = false)
abstract class SleepDatabase : RoomDatabase() {

    abstract val sleepDatabaseDao: SleepDatabaseDao

    companion object {

        @Volatile
        private var INSTANCE: SleepDatabase? = null

        fun getInstance(context: Context): SleepDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
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

/*
Steps:
1. Create an abstract class that extends the room database
2. Annote with @Database.
3. Add all tables in the 'entities' array.
4. Create an abstract value of the type of the Dao (you can have many Daos as well).
5. Create a companion object.
6. Create a variable to make a reference to the database, initialized to null which
will contain the instance once it's created. This avoids creating multiple instances
because DB instances are expensive.
7. It's annotated with Volatile because this allows us to ensure that the value of
INSTANCE is always up to date and the same to all execution threads. The value of a
volatile var will never be cached and all writes and reads will be done to and from
the main memory. Changes are visible to all threads immediately.
8. Back in the companion object we create a getInstance method that returns the
reference to the Database. Context is passed because the Database builder will
require context.
9. The getInstance() method has a synchronized  block so that only one thread
can access the code at a time.
10. Inside the synchronized block we create a local variable of the instance
of the DB, and return it.
11. We check whether the instance of the DB is null and if it is create it.
12. We need to create a migration object with a migration strategy when we create
the DB. For example when we change the number or type of columns. We would need to
convert the existing tables into the new schema. In this case we are using a
destructive migration, but that's not something recommended in prod.
13. We update the INSTANCE with the local variable instance, so that it is now
initialized.
 */
