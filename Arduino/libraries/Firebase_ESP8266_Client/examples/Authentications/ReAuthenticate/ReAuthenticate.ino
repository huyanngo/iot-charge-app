
/**
 * Created by K. Suwatchai (Mobizt)
 * 
 * Email: k_suwatchai@hotmail.com
 * 
 * Github: https://github.com/mobizt
 * 
 * Copyright (c) 2021 mobizt
 *
*/

/** This example will show how to re-authenticate after signed in with Email and password.
*/

#if defined(ESP32)
#include <WiFi.h>
#include <FirebaseESP32.h>
#elif defined(ESP8266)
#include <ESP8266WiFi.h>
#include <FirebaseESP8266.h>
#endif

//Provide the token generation process info.
#include <addons/TokenHelper.h>

//Provide the RTDB payload printing info and other helper functions.
#include <addons/RTDBHelper.h>

/* 1. Define the WiFi credentials */
#define WIFI_SSID "WIFI_AP"
#define WIFI_PASSWORD "WIFI_PASSWORD"

/** 2. Define the API key
 * 
 * The API key (required) can be obtained since you created the project and set up 
 * the Authentication in Firebase console. Then you will get the API key from
 * Firebase project Web API key in Project settings, on General tab should show the
 * Web API Key.
 * 
 * You may need to enable the Identity provider at https://console.cloud.google.com/customer-identity/providers 
 * Select your project, click at ENABLE IDENTITY PLATFORM button.
 * The API key also available by click at the link APPLICATION SETUP DETAILS.
 * 
*/
#define API_KEY "API_KEY"

/* 3. Define the user Email and password that already registerd or added in your project */
#define USER_EMAIL1 "USER_EMAIL1"
#define USER_PASSWORD1 "USER_PASSWORD1"

#define USER_EMAIL2 "USER_EMAIL2"
#define USER_PASSWORD2 "USER_PASSWORD2"

/* 4. If work with RTDB, define the RTDB URL */
#define DATABASE_URL "URL" //<databaseName>.firebaseio.com or <databaseName>.<region>.firebasedatabase.app

/* 5. Define the Firebase Data object */
FirebaseData fbdo;

/* 6. Define the FirebaseAuth data for authentication data */
FirebaseAuth auth;

/* 7. Define the FirebaseConfig data for config data */
FirebaseConfig config;

unsigned long dataMillis = 0;
int count = 0;
bool toggleUser = false;

void signIn(const char *email, const char *password)
{
    /* Assign the user sign in credentials */
    auth.user.email = email;
    auth.user.password = password;

    /* Initialize the library with the Firebase authen and config */
    Firebase.begin(&config, &auth);
}

void setup()
{

    Serial.begin(115200);

    WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
    Serial.print("Connecting to Wi-Fi");
    while (WiFi.status() != WL_CONNECTED)
    {
        Serial.print(".");
        delay(300);
    }
    Serial.println();
    Serial.print("Connected with IP: ");
    Serial.println(WiFi.localIP());
    Serial.println();

    Serial.printf("Firebase Client v%s\n\n", FIREBASE_CLIENT_VERSION);

    /* Assign the api key (required) */
    config.api_key = API_KEY;

    /* Assign the RTDB URL */
    config.database_url = DATABASE_URL;

    Firebase.reconnectWiFi(true);
    fbdo.setResponseSize(4096);

    /* Assign the callback function for the long running token generation task */
    config.token_status_callback = tokenStatusCallback; //see addons/TokenHelper.h

    /** Assign the maximum retry of token generation */
    config.max_token_generation_retry = 5;

    /** Sign in as user 1 */
    signIn(USER_EMAIL1, USER_PASSWORD1);
}

void loop()
{
    if (millis() - dataMillis > 5000)
    {
        dataMillis = millis();

        if (Firebase.ready())
        {
            String path = "/UsersData/";
            path += auth.token.uid.c_str(); //<- user uid of current user that sign in with Emal/Password
            path += "/test/int";
            Serial.printf("Current UID: %s\n", auth.token.uid.c_str());
            Serial.printf("Set int... %s\n", Firebase.setInt(fbdo, path.c_str(), count++) ? "ok" : fbdo.errorReason().c_str());

            //Swap users every 5 times
            if (count % 5 == 0)
            {
                Serial.println();

                if (toggleUser)
                    signIn(USER_EMAIL1, USER_PASSWORD1); /** Sign in as user 1 */
                else
                    signIn(USER_EMAIL2, USER_PASSWORD2); /** Sign in as user 2 */
                toggleUser = !toggleUser;
            }
        }
    }
}
