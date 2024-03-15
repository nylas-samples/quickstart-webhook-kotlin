# How to run

1. Install the quickstart_webhooks_kotlin folder in your favourite webserver. Make sure to add an empty **CLIENT_SECRET** to your enviroment variables.

2. Run the project kotlin-create-webhooks

```bash
mvn package
java -jar target/kotlin-create-webhooks-1.0-SNAPSHOT-jar-with-dependencies.jar
```

3. Grab the client secret generated in step 2 and use it to replace the empty **CLIENT_SECRET** variable

4. env variables for java-webhooks-v3.rb

```env
export NYLAS_API_KEY=<API_KEY>
export NYLAS_API_URI=<https://api.us.nylas.com>
export EMAIL=<RECIPIENT_EMAIL_ADDRESS_HERE>
export WEBHOOK_URL=<YOUR_WEBHOOK_URL>
```

5. Open your browser and go to the link generated by your server.

6. Send or recieve emails and you will the webhooks notification showing up on your application main page.
