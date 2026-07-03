package com.diamond.appcliente.util

import android.content.Context
import com.diamond.barbershop.shared.dto.servicio.ServicioDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServicioLocalCache @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("servicios_cache", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val listType = object : TypeToken<List<ServicioDto>>() {}.type

    fun save(servicios: List<ServicioDto>) {
        prefs.edit()
            .putString("data", gson.toJson(servicios))
            .apply()
    }

    fun load(): List<ServicioDto>? {
        val json = prefs.getString("data", null) ?: return null
        return try { gson.fromJson(json, listType) } catch (e: Exception) { null }
    }

    fun invalidate() {
        prefs.edit().remove("data").apply()
    }
}
