package com.example.note.utils.cookie_tool

import com.example.note.utils.cookie_tool.JSONBodyBuilder.toRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject

object JSONBodyBuilder {

    /** 创建一个空的 JSONObject */
    fun build(): JSONObject = JSONObject()

    /** 往 JSONObject 里添加字段，支持 List 自动转 JSONArray */
    fun JSONObject.addParams(key: String, value: Any?): JSONObject {
        if (value is List<*>) {
            val jsonArray = JSONArray()
            value.forEach { item ->
                when (item) {
                    null -> jsonArray.put(JSONObject.NULL)
                    is String, is Number, is Boolean -> jsonArray.put(item)
                    else -> jsonArray.put(item.toString()) // 简单处理，必要时你可以用 Gson 序列化
                }
            }
            this.put(key, jsonArray)
        } else {
            this.put(key, value)
        }
        return this
    }

    /** 把 JSONObject 转成 application/json 的 RequestBody */
    fun JSONObject.toRequestBody(): RequestBody {
        return RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            this.toString()
        )
    }

    /** 与参考项目一致：链式结尾，转成 RequestBody */
    fun JSONObject.submit(): RequestBody = toRequestBody()
}

/** Map -> JSON RequestBody */
fun createJsonBody(maps: Map<String, Any?>): RequestBody {
    val jsonObject = JSONBodyBuilder.build()
    maps.forEach { (k, v) -> jsonObject.put(k, v) }
    return jsonObject.toRequestBody()
}

/** 单个键值对 -> JSON RequestBody */
fun createJsonBody(pair: Pair<String, Any?>): RequestBody {
    val jsonObject = JSONBodyBuilder.build()
    jsonObject.put(pair.first, pair.second)
    return jsonObject.toRequestBody()
}