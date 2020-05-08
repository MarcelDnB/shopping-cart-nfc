#include <Arduino.h>
#include "ArduinoJson.h"
#include <WiFi.h>
#include <HTTPClient.h>
#include <ArduinoOTA.h>
#include <Wire.h>
#include <SPI.h>
#include "Adafruit_PN532.h"
#include <LiquidCrystal.h>
#include <iostream>
#include <vector>
//#include <SoftwareSerial.h>

#define PN532_IRQ   (2)
#define PN532_RESET (3)
using namespace std;
Adafruit_PN532 nfc(PN532_IRQ, PN532_RESET);


char responseBuffer[300];
WiFiClient client;

const char *SSID = "MiFibra-5058";
const char *PASS = "wdwj5mqF";
const int COMERCIO = 1;
String SERVER_IP = "192.168.1.34";
int SERVER_PORT = 8081;

vector<int> intoleranciasUsuario; //desde la funcion sendPutNuevoUsuario()
vector<int> intoleranciasProducto; //desde la funcion sendGetIntolerancias()
int resultado; //desde la funcion getCompatibilidad()

void sendGetRequest();
void sendPostRequest();
void sendPutNuevoUsuario();
void leerNFC(void);
void sendGetIntolerancias(int);
int  getCompatibilidad();
void grabarNFC(void);
void sendPutAfterScan(int );
void sendPutWifiRead();

void setup() {
  Serial.begin(9600);

  // Configurar para leer/grabar etiquetas RFID
   nfc.begin();
   nfc.setPassiveActivationRetries(0xFF);
   nfc.SAMConfig();
 // para el wifi
  WiFi.begin(SSID, PASS);

  Serial.print("Connecting...");
  while (WiFi.status() != WL_CONNECTED){
    delay(500);
    Serial.print(".");
  }
  Serial.print("Connected, IP address: ");
  Serial.print(WiFi.localIP());
  //sendPutNuevoUsuario(); // 1. Se ejecuta una vez por reset, se crea el perfil de usuario + sus intolerncias y se envian a la bbdd
}

void loop() {

sendPutWifiRead();
  //leerNFC(); // 2. El if que hay dentro se ejecuta, ir a la funcion
  //if(resultado!=0) {
    //mostramos por pantalla el valor que tenga el resultado de haber escaneado un producto
  //}else{
    //imprimimos, esperando el escaneo o algo asi
  //}
  //sendGetRequest();
  //delay(3000);
  //sendPostRequest();
  //delay(3000);

  //6. Mostrar por pantalla el resultado de getCompatibilidad() que es almacenado en una cadena
  //   mientras esta no este vacia (""), cuando se escanee un nuevo producto, se reemplaza

  //Funcionalidad que se me ha ocurrido, tener que darle a reset cada vez que un nuevo cliente manipula el aparato
}



void sendGetRequest(){
  if (WiFi.status() == WL_CONNECTED){
    HTTPClient http;
    http.begin(client, SERVER_IP, SERVER_PORT, "/v2/5e91859a3300005d00e9cf04", true);
    int httpCode = http.GET();

    Serial.println("Response code: " + httpCode);

    String payload = http.getString();

    const size_t capacity = JSON_OBJECT_SIZE(3) + JSON_ARRAY_SIZE(2) + 60;
    DynamicJsonDocument doc(capacity);

    DeserializationError error = deserializeJson(doc, payload);
    if (error){
      Serial.print("deserializeJson() failed: ");
      Serial.println(error.c_str());
      return;
    }
    Serial.println(F("Response:"));
    String sensor = doc["sensor"].as<char*>();
    long time = doc["time"].as<long>();
    float data = doc["data"].as<float>();

    Serial.println("Sensor name: " + sensor);
    Serial.println("Time: " + String(time));
    Serial.println("Data: " + String(data));
  }
}

void sendPostRequest(){
  if (WiFi.status() == WL_CONNECTED){
    HTTPClient http;
    http.begin(client, SERVER_IP, SERVER_PORT, "/v2/5e91859a3300005d00e9cf04", true);
    http.addHeader("Content-Type", "application/json");

    const size_t capacity = JSON_OBJECT_SIZE(3) + JSON_ARRAY_SIZE(2) + 60;
    DynamicJsonDocument doc(capacity);
    doc["temperature"] = 18;
    doc["humidity"] = 78;
    doc["timestamp"] = 124123123;
    doc["name"] = "sensor1";

    String output;
    serializeJson(doc, output);

    int httpCode = http.PUT(output);

    Serial.println("Response code: " + httpCode);

    String payload = http.getString();

    Serial.println("Resultado: " + payload);
  }
}

void sendPutAfterScan(int productId) {
  if (WiFi.status() == WL_CONNECTED){
    HTTPClient http;
    http.begin(client, SERVER_IP, SERVER_PORT, "/api/scan/put/produs/values", true);
    http.addHeader("Content-Type", "application/json");

    const size_t capacity = JSON_OBJECT_SIZE(3) + JSON_ARRAY_SIZE(2) + 60;
    DynamicJsonDocument doc(capacity);
    doc["idProducto"] = productId;
    doc["idUsuario"] = 1;
    String output;
    serializeJson(doc, output);
    int httpCode = http.PUT(output);
    Serial.println("Response code: " + httpCode);
    String payload = http.getString();
    Serial.println("Resultado: " + payload);
  }
}


void sendPutNuevoUsuario() {
  const size_t capacity = JSON_OBJECT_SIZE(3) + JSON_ARRAY_SIZE(2) + 60;
  if(WiFi.status() == WL_CONNECTED) {
    HTTPClient http;
    http.begin(client, SERVER_IP, SERVER_PORT, "/api/scan/put/usuario/values", true);
    http.addHeader("Content-Type", "application/json");
    DynamicJsonDocument doc(capacity);
    doc["idComercio"] = COMERCIO;
    String output;
    serializeJson(doc, output);
    int httpCode = http.PUT(output);
    Serial.println("Response code: " + httpCode);
    String payload = http.getString();
    Serial.println("Resultado: " + payload);
    //1. Cuando un usuario introduce sus intolerancias
    //    Hay que ejecutar putUsuario() y putIntoleranciasUsuario()
    //    Hay que poner en funcionamiento el teclado para ello
    //TIP: trabajar con variables globales
    //sql query donde cogemos las intolerancias disponible
    HTTPClient http1;
    http1.begin(client, SERVER_IP, SERVER_PORT, "/api/scan/get/intolerances", true);
    DynamicJsonDocument doc1(capacity);
    int httpCode1 = http1.GET();
    Serial.println("Response code: " + httpCode1);
    String payload1 = http1.getString();
    DeserializationError error = deserializeJson(doc1, payload);
    if (error){
      Serial.print("deserializeJson() failed: ");
      Serial.println(error.c_str());
      return;
    }
    Serial.println(F("Response:"));
    String nombreIntolerancia = doc1["nombreIntolerancia"].as<char*>();
    int id = doc1["id"].as<int>();
    Serial.println("Id: " + String(id));
    Serial.println("Intolerancia: " + nombreIntolerancia);


  /*  while(1) {
    //Cuando pongamos en marcha el display, recoger mediante los valores de los botones,
    //hacer el scroll en el display y un sistema de seleccion mediante los botones
    //meter dichos valores en la variable global intoleranciasUsuario
  }*/

      }
      HTTPClient http2;
      http2.begin(client, SERVER_IP, SERVER_PORT, "/api/scan/put/usuint/values", true);
      http2.addHeader("Content-Type", "application/json");
      DynamicJsonDocument doc2(capacity);
      JsonArray intolerances = doc2.createNestedArray("intolerances");
      for(int i:intoleranciasUsuario){
        intolerances.add(i);
      }
      String output;
      serializeJson(doc2, output);
      int httpCode2 = http2.PUT(output);
      Serial.println("Response code: " + httpCode2);
      String payload2 = http2.getString();
      Serial.println("Resultado: " + payload2);
}



void leerNFC(void){
  uint8_t success;
    uint8_t uid[] = { 0, 0, 0, 0, 0, 0, 0 };
    uint8_t uidLength;

    success = nfc.readPassiveTargetID(PN532_MIFARE_ISO14443A, uid, &uidLength);
    if (success)
    {
        Serial.println("Intentando autentificar bloque 4 con clave KEYA");
        uint8_t keya[6] = { 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF };

        success = nfc.mifareclassic_AuthenticateBlock(uid, uidLength, 4, 0, keya);
        if (success)
        {
          Serial.println("Sector 1 (Bloques 4 a 7) autentificados");
          uint8_t data[16];

          success = nfc.mifareclassic_ReadDataBlock(4, data);
          if (success)
          {
        Serial.println("Datos leidos de sector 4:");
            nfc.PrintHexChar(data, 16);
            Serial.println("");
            delay(5000);
          }
          else
          {
            Serial.println("Fallo al leer tarjeta");
          }
        }
        else
        {
          Serial.println("Fallo autentificar tarjeta");
        }
        int productId = 0; //va a ser igual a el id que saquemos de la etiqueta NFC
          //3. Si hemos llegado aqui, el usuario ha escaneado un producto
          //4. Tenemos que ejecutar sendGetIntolerancias() y mediante un vector global almacenamos las intoleranciasProducto
          sendGetIntolerancias(productId);
          //5. Ahora hay que llamar a otra funcion que nos diga la respuesta de las intolerancias, getCompatibilidad()
          getCompatibilidad();
          //FUNCIONALIDAD EXTRA---------
          //Llamar a las funciones REST restante putAfterScan()
          sendPutAfterScan(productId);
      }

      //Hacer un if en el loop() de forma que detecte si el vector esta vacio y dependiendo de eso, mostrar un mensaje u otro
}

//TODO: Funcionalidad de muestrear el Wifi periodicamente.
void sendPutWifiRead() {
/*  if (WiFi.status() != WL_CONNECTED) {
    Serial.println("Couldn't get a wifi connection");
    while(true);
  }
  // if you are connected, print out info about the connection:
  else {*/
   // print the received signal strength:
   string str = "vodafone644A";
  const char*c = str.c_str();
  long rssi = WiFi.RSSI(c);

  Serial.print("RSSI:");
  Serial.println(rssi);
/*
    HTTPClient http;
    http.begin(client, SERVER_IP, SERVER_PORT, "/api/scan/put/wifi/values", true);
    http.addHeader("Content-Type", "application/json");

    const size_t capacity = JSON_OBJECT_SIZE(3) + JSON_ARRAY_SIZE(2) + 60;
    DynamicJsonDocument doc(capacity);
    doc["ssid"] = SSID;
    doc["power"] = rssi;
    doc["timestamp"] = 124123123;
    doc["idComercio"] = "sensor1";

    String output;
    serializeJson(doc, output);

    int httpCode = http.PUT(output);

    Serial.println("Response code: " + httpCode);

    String payload = http.getString();

    Serial.println("Resultado: " + payload);*/
  //}
}

//TODO: Hacer el tema de la ubicacion, crear la triangulacion de alguna forma. mapeo?

void sendGetIntolerancias(int productId) {
  //4. Llamar REST
  if (WiFi.status() == WL_CONNECTED){
    HTTPClient http;
    String buffer = "/api/scan/";
    buffer.concat(productId);
    Serial.println(buffer);
    http.begin(client, SERVER_IP, SERVER_PORT, buffer, true);
    int httpCode = http.GET();

    Serial.println("Response code: " + httpCode);

    String payload = http.getString();
    const size_t capacity = JSON_OBJECT_SIZE(3) + JSON_ARRAY_SIZE(2) + 60;
    DynamicJsonDocument doc(capacity);

    DeserializationError error = deserializeJson(doc, payload);
    if (error){
      Serial.println("deserializeJson() failed: ");
      Serial.println(error.c_str());
      return;
    }


    Serial.println(F("Response:"));
    JsonArray data = doc["idIntolerancia"].as<JsonArray>();
    for(JsonVariant v : data) {
      intoleranciasProducto.push_back(v.as<int>());
      //Serial.println(v.as<int>());
    }
  }
}

int getCompatibilidad() {
  //5. Logica de ver si el producto es compatible con las intolerancias del usuario
  int valor = 0;
  for(int i:intoleranciasUsuario) {
    for(int j:intoleranciasProducto) {
      if(i == j) {
        valor++;
        }
      }
    }
    if(valor<=10) {
      return valor;
    }else{
      return 10;
    }
    return 0;
  }

void grabarNFC(void){
  uint8_t success;
  uint8_t uid[] = { 0, 0, 0, 0, 0, 0, 0 };
  uint8_t uidLength;

  success = nfc.readPassiveTargetID(PN532_MIFARE_ISO14443A, uid, &uidLength);
  if (success) {
      Serial.println("Intentando autentificar bloque 4 con clave KEYA");
      uint8_t keya[6] = { 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF };

      success = nfc.mifareclassic_AuthenticateBlock(uid, uidLength, 4, 0, keya);
      if (success)
      {
        Serial.println("Sector 1 (Bloques 4 a 7) autentificados");
        uint8_t data[16];

        memcpy(data, (const uint8_t[]){ 'l', 'u', 'i', 's', 'l', 'l', 'a', 'm', 'a', 's', '.', 'e', 's', 0, 0, 0 }, sizeof data);
        success = nfc.mifareclassic_WriteDataBlock (4, data);

        if (success)
        {
          Serial.println("Datos escritos en bloque 4");
          delay(10000);
        }
        else
        {
          Serial.println("Fallo al escribir tarjeta");
          delay(1000);
        }
      }
      else
      {
        Serial.println("Fallo autentificar tarjeta");
        delay(1000);
      }
    }
}
