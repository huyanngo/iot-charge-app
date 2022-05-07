#include <ESP8266WiFi.h>
#include <FirebaseESP8266.h>

// Connect to Firebase and Wifi.
#define FIREBASE_HOST "iotcharger-f97bc-default-rtdb.firebaseio.com"
#define FIREBASE_AUTH "bauQSjrsTgJjxAeHSGUOMid7FWt6ky5WjsqKfl79"
#define WIFI_SSID "ESP8266 Connect"
#define WIFI_PASSWORD "12345678"

int statusD1 = 0;
int statusD2 = 0;
int statusD3 = 0;
int statusD4 = 0;
FirebaseData fb;

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
  pinMode(D1,OUTPUT);
  pinMode(D2,OUTPUT);
  pinMode(D3,OUTPUT);
  pinMode(D4,OUTPUT);
}

void loop() {
  // put your main code here, to run repeatedly:

  //Repeatedly check the Status of Device 1
  if (Firebase.getInt(fb,"Device/1/status")){
    if(fb.dataType() == "int"){
      statusD1 = (fb.intData());
      if(statusD1 == 0){
        Serial.println("----> IoT Charge 1 OFF");
        digitalWrite(D1,LOW);
      }else if(statusD1 == 1){
        Serial.println("----> IoT Charge 1 ON");
        digitalWrite(D1,HIGH);
      }
    }
  }else{
    Serial.println(fb.errorReason());
    Serial.println("Failed");
  }


 
  //Repeatedly check the Status of Device 2
  if (Firebase.getInt(fb,"Device/2/status")){
    if(fb.dataType() == "int"){
      statusD2 = (fb.intData());
      if(statusD2 == 0){
        Serial.println("----> IoT Charge 2 OFF");
        digitalWrite(D2,LOW);
      }else if(statusD2 == 1){
        Serial.println("----> IoT Charge 2 ON");
        digitalWrite(D2,HIGH);
      }
    }
  }else{
    Serial.println(fb.errorReason());
    Serial.println("Failed");
  }


  
  //Repeatedly check the Status of Device 3
  if (Firebase.getInt(fb,"Device/3/status")){
    if(fb.dataType() == "int"){
      statusD3 = (fb.intData());
      if(statusD3 == 0){
        Serial.println("----> IoT Charge 3 OFF");
        digitalWrite(D3,LOW);
      }else if(statusD3 == 1){
        Serial.println("----> IoT Charge 3 ON");
        digitalWrite(D3,HIGH);
      }
    }
  }else{
    Serial.println(fb.errorReason());
    Serial.println("Failed");
  }



  //Repeatedly check the Status of Device 4
  if (Firebase.getInt(fb,"Device/4/status")){
    if(fb.dataType() == "int"){
      statusD4 = (fb.intData());
      if(statusD4 == 0){
        Serial.println("----> IoT Charge 4 OFF");
        digitalWrite(D4,LOW);
      }else if(statusD4 == 1){
        Serial.println("----> IoT Charge 4 ON");
        digitalWrite(D4,HIGH);
      }
    }
  }else{
    Serial.println(fb.errorReason());
    Serial.println("Failed");
  }
}
