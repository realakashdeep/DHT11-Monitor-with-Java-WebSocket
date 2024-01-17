#include "DHT.h"

#define DHTPIN 2      // DHT11 sensor connected to digital pin 4
#define DHTTYPE DHT11 // DHT 11 type

DHT dht(DHTPIN, DHTTYPE);


void setup() {
  Serial.begin(9600);
  dht.begin();
}

void loop() {
  delay(1000);
  int chk = dht.read(DHTPIN);
  float temperature = dht.readTemperature();
   float humidity = dht.readHumidity();   

   // Send data to Java server via serial
    Serial.print("T:");
    Serial.print(temperature);
    Serial.print(",H:");
    Serial.println(humidity);
  delay(1000);
}
