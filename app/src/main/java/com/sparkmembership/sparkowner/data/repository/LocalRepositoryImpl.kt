package com.sparkmembership.sparkfitness.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.sparkmembership.sparkowner.domain.repository.LocalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.lang.reflect.Type
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(private val dataStore: DataStore<Preferences>):
    LocalRepository {

    override suspend fun writeObject(key: String, data: Any?) {
        val json = Gson().toJson(data)
        dataStore.edit{ pref -> pref[stringPreferencesKey(key)] = json }
    }

    override suspend fun <T> readObject(key: String, classOfT: Type): Flow<T> {
        return dataStore.data.catch { exception ->
            if(exception is IOException){
                Log.d("datastore", "readObject: " + exception.message.toString())
                emit(emptyPreferences())
            }else{
                emit(emptyPreferences())
            }
        }.map { pref ->
            val json = pref[stringPreferencesKey(key)] ?: ""
            Gson().fromJson(json, classOfT)
        }
    }

    override suspend fun clearSpecificObject(key: String) {
        dataStore.edit { pref ->
            if (pref.contains(stringPreferencesKey(key))) {
                pref.remove(stringPreferencesKey(key))
            }
        }
    }

    override suspend fun clearAllObjects() {

        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}