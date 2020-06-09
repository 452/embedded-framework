# Java Embedded Framework

Java embedded framework for create sensors drivers via i2c bus

With this framework you can develop your own libraries on Java, Groovy or any JVM language

This framework allow you to easy change and use any IO provider

under development
this code work, but be refactored

You need first https://github.com/452/java-i2c-tools

## How to build and use
Build
```sh
./gradlew publishMavenJavaPublicationToMavenLocal
```
add to your pom.xml
```xml
<dependency>
    <groupId>com.github.452</groupId>
    <artifactId>embedded-framework</artifactId>
    <version>0.0.1</version>
</dependency>
```

Groovy:
```groovy
// grape uninstall "com.github.452" "java-i2c-tools" "0.0.1"
// grape uninstall "com.github.452" "embedded-framework" "0.0.1"
@Grab(group='com.github.452', module='java-i2c-tools', version='0.0.1')
@Grab(group='com.github.452', module='embedded-framework', version='0.0.1')
import ua.kovel.java.i2c.tools.*;
import ua.kovel.java.i2c.devices.*;
import ua.kovel.java.i2c.devices.LcdPcf8574.Config;

I2C i2c = new I2C("i2c-tiny-usb", LcdPcf8574.ADDRESS);
Config config = new Config() {

    @Override
    public void write(byte[] data, int command) throws IOException {
        for (int position = 0; position < data.length; position++) {
            i2c.writeByte(command, data[position] & 0xFF);
        }
    }
};

LcdPcf8574 lcd = new LcdPcf8574(config);

lcd.begin(16, 2);
lcd.setBacklight(true);
lcd.home();
lcd.clear();
lcd.print("Hello Groovy LCD 1602");
sleep(1000)
while(1) {
    def now = new Date()
    utc = now.format("HH:mm:ss", TimeZone.getTimeZone('UTC'))
    us = now.format("HH:mm:ss", TimeZone.getTimeZone('US/Eastern'))
    lcd.setCursor(0, 0);
    lcd.print("UTC $utc")
    lcd.setCursor(0, 1);
    lcd.print("US  $us")
    sleep(1000)
}
```

## Usage example
[BME280 example](src/test/java/ua/kovel/java/i2c/devices/BME280Test.java)
