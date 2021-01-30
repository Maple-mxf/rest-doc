package smartdoc.dashboard.codesample

import okhttp3.CacheControl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.junit.Test
import java.util.concurrent.TimeUnit

/**
 * https://www.ietf.org/rfc/
 */
class KotlinOkHttpSample {

    private val client: OkHttpClient = OkHttpClient()

    @Test
    fun testInvokeAPI() {

        val requestBody = RequestBody.Companion
                .create("application/json".toMediaTypeOrNull()!!, "")

        val request = Request.Builder()
                .addHeader("", "")
                .url("")
                .method("", requestBody)
                .cacheControl(CacheControl.Builder().maxAge(100, TimeUnit.MILLISECONDS).build())
                .build()

        val response = client.newCall(request).execute()
        println(response.code)
    }
}