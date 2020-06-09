package ua.kovel.java.i2c.devices;

import java.io.IOException;
import org.junit.Test;
import ua.kovel.java.i2c.devices.LcdPcf8574.Config;
import ua.kovel.java.i2c.tools.I2C;

public class LCD1602PCF8574Test {

    @Test
    public void pcf8574() throws Exception {
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

        while (true) {
            lcd.begin(16, 2);
            lcd.setBacklight(true);
            lcd.home();
            lcd.clear();
            lcd.print("Hello LCD 1602");
            int[] heart = {0b00000, 0b01010, 0b11111, 0b11111, 0b11111, 0b01110, 0b00100, 0b00000};
            lcd.createChar(0, heart);
            lcd.setCursor(15, 0);
            lcd.write(0); // write :heart: custom character

            delay(1000);
            lcd.setBacklight(false);
            delay(400);
            lcd.setBacklight(true);
            delay(2000);

            lcd.clear();
            lcd.print("Cursor On");
            lcd.cursor();
            delay(2000);

            lcd.clear();
            lcd.print("Cursor Blink");
            lcd.blink();
            delay(2000);

            lcd.clear();
            lcd.print("Cursor OFF");
            lcd.noBlink();
            lcd.noCursor();
            delay(2000);

            lcd.clear();
            lcd.print("Display Off");
            lcd.noDisplay();
            delay(2000);

            lcd.clear();
            lcd.print("Display On");
            lcd.display();
            delay(2000);

            lcd.clear();
            lcd.setCursor(0, 0);
            lcd.print("*** first line.");
            lcd.setCursor(0, 1);
            lcd.print("*** second line.");
            delay(2000);

            lcd.scrollDisplayLeft();
            delay(2000);

            lcd.scrollDisplayLeft();
            delay(2000);

            lcd.scrollDisplayLeft();
            delay(2000);

            lcd.scrollDisplayRight();
            delay(2000);
        }
    }

    private void delay(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            System.err.println("Sleep error");
        }
    }
}