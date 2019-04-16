package com.example.snake.services

import android.os.AsyncTask
import org.w3c.dom.Document
import org.xml.sax.InputSource
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.StringReader
import java.net.HttpURLConnection
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

// the purpose of this task is to make request and retrieve xml
class BaseRequestTask(val callback: (Document?,Boolean) -> Unit) : AsyncTask<URL, Void, Boolean>() {

    private var document: Document? = null;
    private val MAX_ATTEMPT = 3

    override fun doInBackground(vararg urls: URL?): Boolean {
        var isConnected = false
        var nbAttempt = 0
        try {
            do {
                val cookieManager = android.webkit.CookieManager.getInstance()
                val connection: HttpURLConnection = urls[0]?.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.doOutput = true
                connection.connectTimeout = 10000
                connection.readTimeout = 5000

                cookieManager.getCookie("snake")?.let {
                    connection.setRequestProperty("Cookie",it)
                }
                connection.connect()
                if(connection.responseCode == HttpURLConnection.HTTP_OK) {

                    val headerFields = connection.headerFields
                    val cookiesHeader = headerFields.get("Set-Cookie")

                    cookiesHeader?.let {
                        cookiesHeader.forEach { cookie ->
                            val cookieManager = android.webkit.CookieManager.getInstance()
                            cookieManager.setCookie("snake",cookie)
                        }
                    }

                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val buffer = StringBuffer()
                    for (line in reader.lines()) {
                        buffer.append(line)
                    }
                    reader.close()

                    println("Buffer $buffer")

                    val builderFactory = DocumentBuilderFactory.newInstance()
                    val documentBuilder = builderFactory.newDocumentBuilder()
                    val inputSource = InputSource(StringReader(buffer.toString()))
                    document = documentBuilder.parse(inputSource)

                    isConnected = true
                }
                nbAttempt++
            } while (!isConnected && nbAttempt < MAX_ATTEMPT)
        }
        catch (exception:Exception) {
            println(exception)
        }
        return isConnected
    }

    override fun onPostExecute(result: Boolean?) {
        result?.let {
            callback(document,it)
        }
    }
}