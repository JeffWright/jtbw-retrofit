package dev.jtbw.retrofit

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitUtils {
  val moshi: Moshi by lazy {
    Moshi.Builder()
      .addLast(KotlinJsonAdapterFactory())
      .addLast(LocalDateTimeAdapter())
      .addLast(ZonedDateTimeAdapter())
      .addLast(LocalDateAdapter())
      .build()
  }

  fun okHttpClient(
    loggingLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BASIC
  ): OkHttpClient {

    val retrofitLoggingInterceptor =
      HttpLoggingInterceptor { s -> println(s) }.also { it.level = loggingLevel }

    return OkHttpClient.Builder()
      .addInterceptor(retrofitLoggingInterceptor)
      .callTimeout(Duration.ofMinutes(1))
      .build()
  }

  fun getRetrofit(
    baseUrl: String,
    moshi: Moshi = this.moshi,
    okHttpClient: OkHttpClient = this.okHttpClient(),
  ): Retrofit {
    return Retrofit.Builder()
      .baseUrl(baseUrl)
      .client(okHttpClient)
      .addConverterFactory(ScalarsConverterFactory.create())
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()
  }
}

/** Parses an ISO-8601 string (e.g. "2011-12-03T10:15:30") into a LocalDateTime */
class LocalDateTimeAdapter : JsonAdapter<LocalDateTime>() {
  @ToJson
  override fun toJson(writer: JsonWriter, value: LocalDateTime?) {
    value?.let { writer.value(it.format(formatter)) }
  }

  @FromJson
  override fun fromJson(reader: JsonReader): LocalDateTime? {
    return if (reader.peek() != JsonReader.Token.NULL) {
      fromNonNullString(reader.nextString())
    } else {
      reader.nextNull<Any>()
      null
    }
  }

  private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

  private fun fromNonNullString(nextString: String): LocalDateTime =
    LocalDateTime.parse(nextString, formatter)
}

/** Parses an ISO-8601 string (e.g. "2011-12-03") into a LocalDate */
class LocalDateAdapter : JsonAdapter<LocalDate>() {
  @ToJson
  override fun toJson(writer: JsonWriter, value: LocalDate?) {
    value?.let { writer.value(it.format(formatter)) }
  }

  @FromJson
  override fun fromJson(reader: JsonReader): LocalDate? {
    return if (reader.peek() != JsonReader.Token.NULL) {
      fromNonNullString(reader.nextString())
    } else {
      reader.nextNull<Any>()
      null
    }
  }

  private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

  private fun fromNonNullString(nextString: String): LocalDate =
    LocalDate.parse(nextString, formatter)
}

/**
 * Parses an ISO-8601 string (e.g. "2011-12-03T10:15:30+01:00[Europe/Paris]") into a ZonedDateTime
 */
class ZonedDateTimeAdapter : JsonAdapter<ZonedDateTime>() {
  @ToJson
  override fun toJson(writer: JsonWriter, value: ZonedDateTime?) {
    value?.let { writer.value(it.format(formatter)) }
  }

  @FromJson
  override fun fromJson(reader: JsonReader): ZonedDateTime? {
    return if (reader.peek() != JsonReader.Token.NULL) {
      fromNonNullString(reader.nextString())
    } else {
      reader.nextNull<Any>()
      null
    }
  }

  private val formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME

  private fun fromNonNullString(nextString: String): ZonedDateTime =
    ZonedDateTime.parse(nextString, formatter)
}

/** Supports "basic" auth (username & password in the "Authorization" header) */
class BasicAuthInterceptor(username: String, password: String) : Interceptor {
  private var credentials: String = Credentials.basic(username, password)

  override fun intercept(chain: Interceptor.Chain): Response {
    val request: Request = chain.request()
    val authenticatedRequest: Request =
      request.newBuilder().header("Authorization", credentials).build()
    return chain.proceed(authenticatedRequest)
  }
}
