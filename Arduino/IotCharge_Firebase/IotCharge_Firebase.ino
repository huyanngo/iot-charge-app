#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>

// Connect to Firebase and Wifi.
#define FIREBASE_HOST "iotcharger-f97bc-default-rtdb.firebaseio.com"
#define FIREBASE_AUTH "bauQSjrsTgJjxAeHSGUOMid7FWt6ky5WjsqKfl79"
#define WIFI_SSID "Khanh Mai"
#define WIFI_PASSWORD "20052111"

int statusD1 = 0;

void setup() {
  // put your setup code here, to run once:

  Serial.begin(9600);

  // connect to wifi.
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("connecting");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }
  Serial.println();
  Serial.print("connected: ");
  Serial.println(WiFi.localIP());
  
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  pinMode(D1,OUTPUT)
}

void loop() {
  // put your main code here, to run repeatedly:

  //Repeatedly check the Status of Device 1
  statusD1 = Firebase.getInt("Device/1/status");
  
  if(statusD1 == 0){
    Serial.println("----> IoT Charge 1 OFF")
    digitalWrite(D1,LOW)
  }else if(statusD1 == 1){
    Serial.println("----> IoT Charge 1 ON")
    digitalWrite(D1,HIGH)
  }
}
