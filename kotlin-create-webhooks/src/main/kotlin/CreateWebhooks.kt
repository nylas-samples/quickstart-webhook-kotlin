// Import Nylas packages
import com.nylas.NylasClient
import com.nylas.models.*
import com.nylas.resources.Webhooks

// Import DotEnv to handle .env files
import io.github.cdimascio.dotenv.dotenv

// The main function
fun main(args: Array<String>){
    // Load our env variable
    val dotenv = dotenv()

    // Initialize Nylas client
    val nylas: NylasClient = NylasClient(
        apiKey = dotenv["NYLAS_API_KEY"],
        apiUri = dotenv["NYLAS_API_URI"]
    )

    // Which triggers are we're going to use
    val triggersList: List<WebhookTriggers> = listOf(WebhookTriggers.MESSAGE_CREATED)
    // Create the webhook triggers request
    val webhookRequest: CreateWebhookRequest = CreateWebhookRequest(
        triggersList,
        dotenv["WEBHOOK_URL"],
        "My first webhook",
        listOf(dotenv["GRANT_ID"])
    )
    // Create the webhook and return the response
    try{
        val webhook: Response<WebhookWithSecret> = Webhooks(nylas).create(webhookRequest)
        println(webhook.data)
    }catch(exception : Exception){
        println("Error :$exception")
    }
}