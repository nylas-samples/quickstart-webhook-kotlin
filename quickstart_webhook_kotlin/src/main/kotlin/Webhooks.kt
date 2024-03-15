import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import spark.template.mustache.MustacheTemplateEngine;

import spark.ModelAndView
import spark.kotlin.Http
import spark.kotlin.ignite

import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import java.net.URLEncoder
import java.text.SimpleDateFormat

data class Webhook_Info(
    var id: String,
    var date: String,
    var subject: String,
    var fromEmail: String,
    var fromName: String
)

var array: Array<Webhook_Info> = arrayOf()

object Hmac {
    fun digest(
        msg: String,
        key: String,
        alg: String = "HmacSHA256"
    ): String {
        val signingKey = SecretKeySpec(key.toByteArray(), alg)
        val mac = Mac.getInstance(alg)
        mac.init(signingKey)

        val bytes = mac.doFinal(msg.toByteArray())
        return format(bytes)
    }

    private fun format(bytes: ByteArray): String {
        val formatter = Formatter()
        bytes.forEach { formatter.format("%02x", it) }
        return formatter.toString()
    }
}

fun addElement(arr: Array<Webhook_Info>,
               element: Webhook_Info): Array<Webhook_Info> {
    val mutableArray = arr.toMutableList()
    mutableArray.add(element)
    return mutableArray.toTypedArray()
}

fun dateFormatter(milliseconds: String): String {
    return SimpleDateFormat("dd/MM/yyyy HH:mm:ss").
    format(Date(milliseconds.toLong() * 1000)).toString()
}

fun main(args: Array<String>) {
    val http: Http = ignite()

    http.get("/webhook") {
        request.queryParams("challenge")
    }

    http.post("/webhook") {
        val mapper = jacksonObjectMapper()
        val model: JsonNode = mapper.readValue<JsonNode>(request.body())
            if(Hmac.digest(request.body(), URLEncoder.encode(System.getenv("CLIENT_SECRET"),
                    "UTF-8")) == request.headers("X-Nylas-Signature").toString()){
                array = addElement(array, Webhook_Info(model["data"]["object"]["id"].textValue(),
                        dateFormatter(model["data"]["object"]["id"].textValue()),
                        model["data"]["object"]["subject"].textValue(),
                        model["data"]["object"]["from"].get(0)["email"].textValue(),
                        model["data"]["object"]["from"].get(0)["name"].textValue()))
            }
        response.status(200)
        "Webhook Received"
    }

    http.get("/") {
        val model = HashMap<String, Any>()
        model["webhooks"] = array
        MustacheTemplateEngine().render(
            ModelAndView(model, "show_webhooks.mustache")
        )
    }
}
