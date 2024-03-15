package dev.jtbw.retrofit

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlin.collections.set as setOriginal

class PrimitivesJson(private val moshi: Moshi = RetrofitUtils.moshi) {
  /**
   * This can be helpful if you just want to process JSON quick and dirty without modeling the data
   * as full kotlin objects. Use with [get], below
   */
  fun deserializeJsonObjectToPrimitives(json: String): Map<String, Any?> {
    val mapType = Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java)
    val adapter = moshi.adapter<Map<String, Any>>(mapType)
    return adapter.fromJson(json) ?: error("moshi deserialization returned null")
  }

  /**
   * This can be helpful if you just want to process JSON quick and dirty without modeling the data
   * as full kotlin objects. Use with [get], below
   */
  fun deserializeJsonListToPrimitives(json: String): List<Any> {
    val mapType = Types.newParameterizedType(List::class.java, Any::class.java)
    val adapter = moshi.adapter<List<Any>>(mapType)
    return adapter.fromJson(json) ?: error("moshi deserialization returned null")
  }

  /** Assumes the receiver is a map, and looks up the given key */
  operator fun Any?.get(key: String): Any? {
    try {
      val map = this!! as Map<String, *>
      return (Map<String, *>::get).invoke(map, key)
    } catch (exc: ClassCastException) {
      throw ClassCastException("cannot get key \"$key\", because: ${exc.message}")
    } catch (exc: NullPointerException) {
      throw NullPointerException("cannot get key \"$key\", because: ${exc.message}")
    }
  }

  /**
   * This can be helpful if you just want to process JSON quick and dirty without modeling the data
   * as full kotlin objects. Use with [get], below
   */
  fun serializeToJson(map: Map<String, Any?>): String {
    val mapType = Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java)
    val adapter = moshi.adapter<Map<String, Any?>>(mapType)
    return adapter.toJson(map) ?: error("moshi deserialization returned null")
  }

  /**
   * This can be helpful if you just want to process JSON quick and dirty without modeling the data
   * as full kotlin objects. Use with [get], below
   */
  fun serializeToJson(list: List<Any?>): String {
    val mapType = Types.newParameterizedType(List::class.java, Any::class.java)
    val adapter = moshi.adapter<List<Any?>>(mapType)
    return adapter.toJson(list) ?: error("moshi deserialization returned null")
  }

  /** Assumes the receiver is a map, and sets the given key */
  operator fun Any?.set(key: String, value: Any?) {
    try {
      val map = this!! as MutableMap<String, Any?>
      return (MutableMap<String, Any?>::setOriginal).invoke(map, key, value)
    } catch (exc: ClassCastException) {
      throw ClassCastException("cannot set key \"$key\", because: ${exc.message}")
    } catch (exc: NullPointerException) {
      throw NullPointerException("cannot set key \"$key\", because: ${exc.message}")
    }
  }
}
