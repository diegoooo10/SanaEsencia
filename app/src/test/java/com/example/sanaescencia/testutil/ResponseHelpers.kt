package com.example.sanaescencia.testutil

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

fun <T> responseOk(body: T): Response<T> = Response.success(body)

fun <T> responseEmptyOk(): Response<T> = Response.success(null)

fun <T> responseHttpError(code: Int, message: String = "error"): Response<T> =
    Response.error(code, """{"message":"$message"}""".toResponseBody("application/json".toMediaType()))
