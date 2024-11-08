package com.sparkmembership.sparkowner.domain.repository

import kotlinx.coroutines.flow.Flow
import java.lang.reflect.Type

interface LocalRepository {
    suspend fun writeObject(key: String, data: Any? = null)
    suspend fun <T> readObject(key: String, classOfT: Type): Flow<T>
    suspend fun clearSpecificObject(key: String)
    suspend fun clearAllObjects()
}