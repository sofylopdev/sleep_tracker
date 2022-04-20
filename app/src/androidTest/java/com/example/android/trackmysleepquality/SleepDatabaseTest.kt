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

package com.example.android.trackmysleepquality

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import org.junit.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


/**
 * This is not meant to be a full set of tests. For simplicity, most of your samples do not
 * include tests. However, when building the Room, it is helpful to make sure it works before
 * adding the UI.
 */


@RunWith(AndroidJUnit4::class)
class SleepDatabaseTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var sleepDao: SleepDatabaseDao
    private lateinit var db: SleepDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, SleepDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        sleepDao = db.sleepDatabaseDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetTonight() {
        val night = SleepNight()
        sleepDao.insert(night)
        val tonight = sleepDao.getTonight()
        assertEquals(tonight?.sleepQuality, -1)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndUpdateValue() {
        val night = SleepNight()
        sleepDao.insert(night)
        val tonight = sleepDao.getTonight()
        tonight?.sleepQuality = 3
        sleepDao.update(tonight!!)
        val tonight2 = sleepDao.getTonight()
        assertEquals(tonight2?.sleepQuality, 3)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetNight() {
        val night = SleepNight()
        sleepDao.insert(night)
        val tonight = sleepDao.getTonight()
        val current = sleepDao.get(tonight!!.nightId)
        assertEquals(current?.nightId, tonight.nightId)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndClear() {
        val night = SleepNight()
        sleepDao.insert(night)
        sleepDao.clear()
        val tonight = sleepDao.getTonight()
        assertEquals(tonight, null)
    }


    @Test
    @Throws(Exception::class)
    fun insertAndGetList() {
        val nights: LiveData<List<SleepNight>> =
            sleepDao.getAllNights()

        nights.observeForever {
            Log.d("TEST", "list updated")
        }

        val night = SleepNight()
        night.sleepQuality = 3
        sleepDao.insert(night)
        val nightSecond = SleepNight()
        sleepDao.insert(nightSecond)

        assertEquals(nights.value?.size, 2)
    }
}

//Update (JUnit 5)
//
//If you're using JUnit5, then you can use this extension instead of the Rule explained in Update (JUnit4) above.
//
//class InstantTaskExecutorExtension : BeforeEachCallback, AfterEachCallback {
//
//    override fun beforeEach(context: ExtensionContext?) {
//        ArchTaskExecutor.getInstance().setDelegate(object : TaskExecutor() {
//            override fun executeOnDiskIO(runnable: Runnable) {
//                runnable.run()
//            }
//
//            override fun postToMainThread(runnable: Runnable) {
//                runnable.run()
//            }
//
//            override fun isMainThread(): Boolean {
//                return true
//            }
//        })
//    }
//
//    override fun afterEach(context: ExtensionContext?) {
//        ArchTaskExecutor.getInstance().setDelegate(null)
//    }
//}
//Use this extension by annotating your test class like so:
//
//@ExtendWith(InstantTaskExecutorExtension::class)
//class MyTestClass { ... }




