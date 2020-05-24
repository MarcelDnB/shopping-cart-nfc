/*
* 1. El dispositivo se conecta a una red WIFI. DONE
* 2. Se imprime por pantalla "Esperando escaneo". 
* 3. El dispositivo se conecta al servidor MQTT correspondiente. El mecanismo MQTT implementado permite mandar "bajo demanda" muestras de
*    las redes WIFI necesarias. DONE
* 4. El usuario debe introducir las intolerancias ayudandose del display y de los botónes.DONE
* 5. Se ejecuta constantemente la función "leerNFC", en la que hay una búsqueda activa de un tag NFC, pero además, esta función 
*    en caso de detectar un escaneo NFC, manda una solicitud de las intolerancias del id del producto escaneado mediante API Rest. 
*    Además, llama a una función, que es la encargada de calcular las intolerancias (si las hay) del producto y de las correspondientes
*    al usuario. 
*    Por ultimo, llama a una función que coloca en la base de datos el producto escaneado de dicho usuario con determinadas intolerancias. DONE (EN CURSO)
* 6. Dentro del bucle principal, ademas hay un bloque if que comprueba si la variable reusltado está vacia, ya que en caso contrario,
*    imprimiría por el LCD el resultado del escaneo. DONE
*/
#include <Arduino.h>
#include <ArduinoJson.h>
#include <WiFi.h>
#include <HTTPClient.h>
#include <ArduinoOTA.h>
#include <Wire.h>
#include <SPI.h>
#include "Adafruit_PN532.h"
#include <LiquidCrystal.h>
#include <iostream>
#include <vector>
#include <PubSubClient.h>
#include "DHTesp.h"
#include <stdlib.h>
#include <WiFiUdp.h>
#include "NTPClient.h"
#include <set>
#define PN532_IRQ (15)
#define PN532_RESET (2)
#define NTP_OFFSET 60 * 60     // In seconds
#define NTP_INTERVAL 60 * 1000 // In miliseconds
#define NTP_ADDRESS "europe.pool.ntp.org"
#define btnRIGHT 0
#define btnUP 1
#define btnDOWN 2
#define btnLEFT 3
#define btnSELECT 4
#define btnNONE 5
using namespace std;

//WIFI
const char *SSID = "MiFibra-5058";
const char *PASS = "wdwj5mqF";
const int COMERCIO = 1;

//MQTT
const int mqttPort = 1885;
const char *mqttServer = "192.168.1.34";
const char *mqttUser = "mqttbroker";
const char *mqttPassword = "mqttbrokerpass";

//PARA MUESTRAS WIFI
const char *red1 = "MiFibra-5058";
const char *red2 = "JAZZTEL_GwWr";
const char *red3 = "JAZZTEL_RwKc";
const char *red4 = "vodafone644A";

//TIMESTAMP
//WiFiUDP ntpUDP;
//NTPClient timeClient(ntpUDP, NTP_ADDRESS, NTP_OFFSET, NTP_INTERVAL);

void sendPutNuevoUsuario();
void leerNFC();
void sendGetIntolerancias(int);
void getCompatibilidad();
void grabarNFC();
void sendPutAfterScan(int);
void sendPutWifiRead();
void sendGetAllIntelerances();
void sendPutUsuario();
void sendPutIntoleranciasUsuario();
void conectarMQTT();
void callback(char *, byte *, unsigned int);
void enviarMQTT(long rssi, String ssid);
double getRSSI(const char *target_ssid);
int Leer_Botones();
void leerTeclado();
void loop1(void *parameter);
TaskHandle_t Task1;
TaskHandle_t Task2;

/*
  nfc: variable mediante la cual inicializamos el nfc, pasandole la direccion de interrupcion y el reset
  client: varaible global que vamos a usar por ejemplo para las peticiones http
  SERVER_IP: dirección IP privada para conectarse a la API Rest.
  SERVER_PORT: puerto para conectarse a la API Rest.
  intoleranciasUsuario: vector en el cual vamos a guardar las intolerancias del usuario.
  intoleranciasProducto: vector en el cual vamos a guardar las intolerancias de un producto.
  resultado: el ultimo resultado de un escaneo de producto.
*/
Adafruit_PN532 nfc(2, 15, 4, 5);
WiFiClient client;
WiFiClient client12;
String SERVER_IP = "192.168.1.34";
int SERVER_PORT = 8081;
set<int> intoleranciasUsuario;  //desde la funcion sendPutNuevoUsuario()
vector<int> intoleranciasProducto; //desde la funcion sendGetIntolerancias()
vector<int> intoleranciasTodas;
int resultado = 0;
int muestrearWifi; //desde la funcion getCompatibilidad()
int ValorAnalog = 0;
int indexEleccion = 0;
char buffer[100];
int aux = btnRIGHT;
//Cliente MQTT
PubSubClient client1(client12);

/*
Funciones:
  1. Inicialización del Serial
  2. Inicialización del NFC
  3. Inicialización del Wifi + conexión
  4. Inicialización del LCD
*/
LiquidCrystal lcd(13, 14, 26, 25, 19, 22);
void setup()
{
  pinMode(A0, INPUT);
  //timeClient.begin();
  Serial.begin(9600);

  /* NFC INIT */
  nfc.begin();
  nfc.setPassiveActivationRetries(0xFF);
  nfc.SAMConfig();
  Serial.println("\nNFC OK!\n");
  /* WIFI INIT */
  WiFi.begin(SSID, PASS);
  Serial.print("Connecting...");
  while (WiFi.status() != WL_CONNECTED)
  {
    delay(500);
    Serial.print(".");
  }
  Serial.print("Connected, IP address: ");
  Serial.print(WiFi.localIP());
  Serial.println("\nWIFI OK!\n");
  
  
  sendPutNuevoUsuario(); // 1. Se ejecuta una vez por reset, se crea el perfil de usuario + sus intolerncias y se envian a la bbdd */
  Serial.println("\n Esperando tarjeta NFC...\n");
  lcd.begin(16, 2);
  leerTeclado();
  /*MQTT INIT*/
  client1.setServer(mqttServer, mqttPort);
  client1.setCallback(callback);
  Serial.println("\nMQTT OK!\n");

  xTaskCreatePinnedToCore(
                      loop1,   /* Task function. */
                      "rest",     /* name of task. */
                      100000,       /* Stack size of task */
                      NULL,        /* parameter of the task */
                      1,           /* priority of the task */
                      &Task2,      /* Task handle to keep track of created task */
                      0);
}

/*
Funciones:
  1. Se va a estar ejecutando constantemente leerNFC(), de forma que si escaneamos un producto entrará en la condición if
  que hay dentro de la función. Explicado mas detalladamente en el comentario de dicha función.
  2. Bloque if-else en el que comprobamos si hay datos en la variable resultados, en caso de que haya significa que
  hemos escaneado un producto y nos ha dado el resultado correspondiente a si el usuario es compatible con el producto
  escaneado, en caso contrario, mostramos un mensaje de "Esperando".
*/
void loop() {
conectarMQTT();  
}
void loop1(void *parameter)
{
  while(1) {
  delay(500);
  leerNFC(); //Se ejecuta constantemente
  // grabarNFC();
  if (aux == 1)
  {
    lcd.clear();
    snprintf(buffer, sizeof(buffer), "%s%d", "Nvl.Incompat:", resultado);
    lcd.print(buffer);
    if(resultado == 0){
      lcd.setCursor(0,1);
      lcd.print("Apto");
    }
    if (resultado == 1){
      lcd.setCursor(0,1);
      lcd.print("No recomendable"); 
    }
    if(resultado >= 2){
      lcd.setCursor(0,1);
      lcd.print("No apto");
    }
      delay(5000);
      aux = 0;
      lcd.clear();
      lcd.print("Esperando escaneo");
  }
  }
   vTaskDelay(10);
}

double getRSSI(const char *target_ssid)
{
  byte available_networks = WiFi.scanNetworks();

  for (int network = 0; network < available_networks; network++)
  {
    if (strcmp(WiFi.SSID(network).c_str(), SSID) == 0)
    {
      return WiFi.RSSI(network);
    }
  } 
  return 0;
}

/*
  Funciones:
    1. Hacer una llamada a la API Rest, enviando el id del producto escaneado, el usuario no es necesario mandarlo ya que
    esta programado de esa forma en la API Rest.


*/
void sendPutAfterScan(int productId)
{
  if (WiFi.status() == WL_CONNECTED)
  {
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
    Serial.println("Response code(PutAfterScan): " + httpCode);
    String payload = http.getString();
    Serial.println("Resultado(PutAfterScan): " + payload);
  }
}

/*
Funciones:
  1. Se crea un usuario al inicializar el dispositivo, pasandole el comercio.
*/
void sendPutUsuario()
{
  HTTPClient http;
  http.begin(client, SERVER_IP, SERVER_PORT, "/api/scan/put/usuario/values", true);
  const size_t capacity = JSON_OBJECT_SIZE(3) + JSON_ARRAY_SIZE(2) + 60;
  http.addHeader("Content-Type", "application/json");
  DynamicJsonDocument doc(capacity);
  doc["idComercio"] = COMERCIO;
  String output;
  serializeJson(doc, output);
  int httpCode = http.PUT(output);
  Serial.println("Response code(SendPutUsuario): " + httpCode);
  String payload = http.getString();
  Serial.println("Resultado(SendPutUsuario): " + payload);
}

/*
Funciones:
  1. Conseguimos todas las intolerancias disponibles.
*/
void sendGetAllIntelerances()
{
  HTTPClient http1;
  http1.begin(client, SERVER_IP, SERVER_PORT, "/api/scan/get/intolerances", true);
  const size_t capacity = JSON_OBJECT_SIZE(3) + JSON_ARRAY_SIZE(2) + 60 + 1000;
  DynamicJsonDocument doc1(capacity);
  int httpCode1 = http1.GET();
  Serial.println("Response code(sendGetAllIntolerances): " + httpCode1);
  String payload1 = http1.getString();
  DeserializationError error = deserializeJson(doc1, payload1);
  if (error)
  {
    Serial.print("deserializeJson() failed(sendGetAllIntolerances): ");
    Serial.println(error.c_str());
    return;
  }
  JsonArray array = doc1.as<JsonArray>();

  for (JsonVariant v : array)
  {
    JsonObject input = v.as<JsonObject>();
    intoleranciasTodas.push_back((int)input["id"]);
  }


  for(int i:intoleranciasTodas) {
    Serial.println(i);
  }
  

/*Serial.println(F("Response:"));
  String nombreIntolerancia = doc1["nombreIntolerancia"].as<char *>();
  int id = doc1["idIntolerancia"].as<int>();
  Serial.println("Id: " + String(id));
  Serial.println("Intolerancia: " + nombreIntolerancia);*/
}

/*
Fuciones:
  1. Llamamos a la función de crear el usuario.
  2. Conseguimos todas las intolerancias para mostrlas en el display.
  3. Introducimos las intolerancias del usuario en la BBDD mediante Rest.
*/
void sendPutNuevoUsuario()
{
  if (WiFi.status() == WL_CONNECTED)
  {
    sendPutUsuario();
    /*
    1. Cuando un usuario introduce sus intolerancias
        Hay que ejecutar putUsuario() y putIntoleranciasUsuario()
        Hay que poner en funcionamiento el teclado para ello
    TIP: trabajar con variables globales
    SQL query donde cogemos las intolerancias disponible
    */
    sendGetAllIntelerances();

    /* TODO:Cuando pongamos en marcha el display, recoger mediante los valores de los botones,
    hacer el scroll en el display y un sistema de seleccion mediante los botones
    meter dichos valores en la variable global intoleranciasUsuario

    while(1) {}
    */
  }
  
}

/*
Funciones:
  1. Enviamos las intolerancias del usuario mediante Rest.
*/
void sendPutIntoleranciasUsuario()
{
  HTTPClient http2;
  http2.begin(client, SERVER_IP, SERVER_PORT, "/api/scan/put/usuint/values", true);
  const size_t capacity = JSON_OBJECT_SIZE(3) + JSON_ARRAY_SIZE(2) + 60;
  DynamicJsonDocument doc2(capacity);
  JsonObject root = doc2.to<JsonObject>();
  JsonArray intolerances = root.createNestedArray("intolerancias");
  for (int i : intoleranciasUsuario)
  {
    intolerances.add(i);
  }
  String output;
  serializeJsonPretty(root, output);
  int httpCode2 = http2.PUT(output);
  Serial.println("Response code(sendPutIntoleranciasUsuario): " + httpCode2);
  String payload2 = http2.getString();
  Serial.println("Resultado(sendPutIntoleranciasUsuario): " + payload2);
}

/*
Funciones:
  1. Comprueba constantemente si hemos escaneado algun producto.
  2. En caso de haber escaneado un producto -> conseguimos mediante Rest las intolerancias del producto escaneado
  3. En caso de haber escaneado un producto -> comprobamos la compatibilidad entre las intolerancias del producto
  comparandolas con las que tiene el usuario.
  4. Por ultimo, introducimos en la BBDD mediante Rest el producto escaneado enlazandolo con el usuario.
*/
void leerNFC(void)
{
  int productId = 0; //va a ser igual a el id que saquemos de la etiqueta NFC
  uint8_t success;
  uint8_t uid[] = {0, 0, 0, 0, 0, 0, 0}; // Buffer to store the returned UID
  uint8_t uidLength;                     // Length of the UID (4 or 7 bytes depending on ISO14443A card type)
  uint8_t data[16] = {};

  // Wait for an ISO14443A type cards (Mifare, etc.).  When one is found
  // 'uid' will be populated with the UID, and uidLength will indicate
  // if the uid is 4 bytes (Mifare Classic) or 7 bytes (Mifare Ultralight)
  success = nfc.readPassiveTargetID(PN532_MIFARE_ISO14443A, uid, &uidLength);

  if (success)
  {
    // Display some basic information about the card
    Serial.println("Found an ISO14443A card");
    Serial.print("  UID Length: ");
    Serial.print(uidLength, DEC);
    Serial.println(" bytes");
    Serial.print("  UID Value: ");
    nfc.PrintHex(uid, uidLength);
    Serial.println("");

    if (uidLength == 4)
    {
      // We probably have a Mifare Classic card ...
      Serial.println("Seems to be a Mifare Classic card (4 byte UID)");

      // Now we need to try to authenticate it for read/write access
      // Try with the factory default KeyA: 0xFF 0xFF 0xFF 0xFF 0xFF 0xFF
      Serial.println("Trying to authenticate block 4 with default KEYA value");
      uint8_t keya[6] = {0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF};

      // Start with block 4 (the first block of sector 1) since sector 0
      // contains the manufacturer data and it's probably better just
      // to leave it alone unless you know what you're doing
      success = nfc.mifareclassic_AuthenticateBlock(uid, uidLength, 4, 0, keya);

      if (success)
      {
        Serial.println("Sector 1 (Blocks 4..7) has been authenticated");
        

        // If you want to write something to block 4 to test with, uncomment
        // the following line and this text should be read back in a minute
        //memcpy(data, (const uint8_t[]){ 'a', 'd', 'a', 'f', 'r', 'u', 'i', 't', '.', 'c', 'o', 'm', 0, 0, 0, 0 }, sizeof data);
        // success = nfc.mifareclassic_WriteDataBlock (4, data);

        // Try to read the contents of block 4
        success = nfc.mifareclassic_ReadDataBlock(4, data);

        if (success)
        {
          // Data seems to have been read ... spit it out
          Serial.println("Reading Block 4:");
          nfc.PrintHexChar(data, 16);
          Serial.println("");
          Serial.println("Esto es el DATA: \n");
          data[15] = '\0';
          Serial.print((char *)data);

          // Wait a bit before reading the card again
          delay(1000);
        }
        else
        {
          Serial.println("Ooops ... unable to read the requested block.  Try another key?");
        }
      }
      else
      {
        Serial.println("Ooops ... authentication failed: Try another key?");
      }
      productId = data[0] - '0';
    }

    if (uidLength == 7)
    {
      // We probably have a Mifare Ultralight card ...
      Serial.println("Seems to be a Mifare Ultralight tag (7 byte UID)");

      // Try to read the first general-purpose user page (#4)
      Serial.println("Reading page 4");
      uint8_t data[32];
      success = nfc.mifareultralight_ReadPage(4, data);
      if (success)
      {
        // Data seems to have been read ... spit it out
        nfc.PrintHexChar(data, 4);
        Serial.println("");
        Serial.println("Esto es el DATA:");
        data[15] = '\0';
        Serial.print((char *)data);

        // Wait a bit before reading the card again
        delay(1000);
      }
      else
      {
        Serial.println("Ooops ... unable to read the requested page!?");
      }
      productId = data[0] - '0';
    }

   
    
    /* Si hemos llegado aqui, el usuario ha escaneado un producto,
       ahora conseguimos las intolerancias del producto escaneado*/
    sendGetIntolerancias(productId);
    /* Nos da la compatibilidad del usuario con el producto escaneado */
    getCompatibilidad();
    /* Enlazamos al usuario con el escaneo */
    sendPutAfterScan(productId);
  }
}

/*
Funciones:
  1. Para muestrear el Wifi periodicamente, probablemente la unica forma de hacerlo es conectandonos a la red en cuestion
  de la que queremos ver su PWR (RSSI). En caso de que sea asi, es necesario hacer un bucle en el cual nos conectemos a las
  redes wifi que nos haran falta para poder hacer la posterior triangulacion de la posicion del dispositivo.
*/
/* TODO: Funcionalidad de muestrear el Wifi periodicamente. */
void sendPutWifiRead()
{
  if (WiFi.status() != WL_CONNECTED)
  {
    Serial.println("Couldn't get a wifi connection");
    while (true)
      ;
  }
  // if you are connected, print out info about the connection:
  else
  {
    enviarMQTT(getRSSI(red1), red1);
    enviarMQTT(getRSSI(red2), red2);
    enviarMQTT(getRSSI(red3), red3);
    enviarMQTT(getRSSI(red4), red4);

    /* // Alternativa API REST de mandar las lecturas WIFI
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

    Serial.println("Resultado: " + payload);
    */
  }
}

/* TODO: Hacer el tema de la ubicacion, crear la triangulacion de alguna forma. mapeo? */

/*
Funciones:
  1. Pasando el id del producto escaneado, vamos a recibir las intolerancias asociadas a dicho producto.
*/
void  sendGetIntolerancias(int productId)
{
  intoleranciasProducto.clear();
  //4. Llamar REST
  if (WiFi.status() == WL_CONNECTED)
  {
    HTTPClient http;
    String buffer = "/api/scan/";
    buffer.concat(productId);
    http.begin(client, SERVER_IP, SERVER_PORT, buffer, true);
    int httpCode = http.GET();

    Serial.println("Response code(sendGetIntolerancias): " + httpCode);

    String payload = http.getString();
    const size_t capacity = JSON_OBJECT_SIZE(3) + JSON_ARRAY_SIZE(2) + 60;
    DynamicJsonDocument doc(capacity);

    DeserializationError error = deserializeJson(doc, payload);
    if (error)
    {
      Serial.println("deserializeJson() failed(sendGetIntolerancias): ");
      Serial.println(error.c_str());
      return;
    }

    Serial.println(F("Response(sendGetIntolerancias):"));
    JsonArray data = doc["idIntolerancia"].as<JsonArray>();
    for (JsonVariant v : data)
    {
      intoleranciasProducto.push_back(v.as<int>());
    }
  }
}

/*
Funciones:
  1. Una funcion para comprobar las intolerancias del producto escaneado con las intolerancias del usuario en cuestion.
*/
void getCompatibilidad()
{
  //5. Logica de ver si el producto es compatible con las intolerancias del usuario
  int valor = 0;
  for (int i : intoleranciasUsuario)
  {
    for (int j : intoleranciasProducto)
    {
      if (i == j)
      {
        valor++;
      }
    }
  }
 if(valor>=10) {
   valor = 10;
 }
  aux = 1;
  resultado = valor;
}

/*
Funciones:
  1. Nos va a servir para grabar productos en el NFC como ultimo recurso, ya que prefereiblemente se va a hacer mediante
  un dispositivo móvil, ya que de esta forma es mucho mas sencilla.
*/
void grabarNFC()
{
  uint8_t success;
  uint8_t uid[] = {0, 0, 0, 0, 0, 0, 0};
  uint8_t uidLength;

  success = nfc.readPassiveTargetID(PN532_MIFARE_ISO14443A, uid, &uidLength);
  if (success)
  {
    if (uidLength == 4)
    {

      Serial.println("Intentando autentificar bloque 4 con clave KEYA");
      uint8_t keya[6] = {0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF};

      success = nfc.mifareclassic_AuthenticateBlock(uid, uidLength, 4, 0, keya);
      if (success)
      {
        Serial.println("Sector 1 (Bloques 4 a 7) autentificados");
        uint8_t data[16];

        memcpy(data, (const uint8_t[]){'1',0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, sizeof data);
        success = nfc.mifareclassic_WriteDataBlock(4, data);

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
  if (uidLength == 7)
  {
    Serial.println("Seems to be a Mifare Ultralight tag (7 byte UID)");
    uint8_t data[32];
    memcpy(data, (const uint8_t[]){ '4', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, sizeof(data));
    success = nfc.mifareultralight_WritePage(4, data);
    if (success)
    {
      delay(10000);
    }
    else
    {
      Serial.println("Fallo autentificar tarjeta");
      delay(1000);
    }
  }
}

// PARTE DE MQTT
void conectarMQTT()
{
    delay(500);
  if (!client1.connected())
  {
    Serial.print("Connecting ...");
    if (client1.connect("GIGI", "mqttbroker", "mqttbrokerpass"))
    {
      Serial.println("connected");
      client1.subscribe("wifi");
    }
    else
    {
      delay(5000);
    }
  }
  // Cliente a la escucha
  client1.loop();
  
}

void callback(char *topic, byte *payload, unsigned int length)
{
  payload[length] = '\0';
  String strTopic = String((char *)topic);
  String msg = "";
  if (strTopic == "wifi")
  {
    msg = (char *)payload;
    if (msg == "enviar")
    {
      Serial.print("recibo el mensaje enviar");
      sendPutWifiRead(); //Si recibe "enviar" por el chat, manda lectura WIFI
    }
  }
}

void enviarMQTT(long rssi, String ssid)
{
  const size_t capacity = JSON_OBJECT_SIZE(3) + JSON_ARRAY_SIZE(2) + 60;
  DynamicJsonDocument doc(capacity);
  doc["power"] = rssi;
  doc["timestamp"] = 100; //millis();
  doc["idWifi"] = 1;
  doc["idComercio"] = COMERCIO;
  doc["ssid"] = ssid;

  String output;
  serializeJson(doc, output);
  char JSONmessageBuffer[100];
  output.toCharArray(JSONmessageBuffer, sizeof(JSONmessageBuffer));
  Serial.println("Sending message to MQTT topic..");
  Serial.println(output);

  if (client1.publish("wifi", JSONmessageBuffer) == true)
  {
    Serial.println("Success sending message");
  }
  else
  {
    Serial.println("Error sending message");
  }
}

int Leer_Botones()
{                               // Función para leer los pulsadores
  ValorAnalog = analogRead(35); // Leemos la entrada analógica A0

  // Y los comparamos con un margen cómodo
  if(ValorAnalog > 900)
    return btnNONE;
  if (ValorAnalog < 50)
    return btnRIGHT;
  if (ValorAnalog > 600 && ValorAnalog < 780)
    return btnUP;
  return btnNONE; // Por si todo falla
}

void leerTeclado()
{
  int salir = 0;
  int boton = 0;
  lcd.clear();
  while (1)
  {
    boton = Leer_Botones();
    delay(200);
    lcd.clear();
    lcd.print("Intolerancia: ");
    lcd.setCursor(14, 0);
    lcd.print(indexEleccion);
    if (boton == btnUP)
    {
      if (indexEleccion == intoleranciasTodas.size())
      {
        indexEleccion = 0;
      }
      salir = 0;
      indexEleccion++;
    }
    if((boton == btnRIGHT) && (salir == 1)) {
      salir++;
    }

    if(salir == 2) {
      sendPutIntoleranciasUsuario();
      break;
    }
    else if (boton == btnRIGHT)
    {
      delay(500);
      salir++;
      intoleranciasUsuario.insert(intoleranciasTodas[indexEleccion-1]);
      //intoleranciasUsuario.push_back(intoleranciasTodas[indexEleccion-1]);
    }
  }
  salir = 0;
}

