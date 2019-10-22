package ua.kovel.java.i2c.devices;

import static org.junit.Assert.assertEquals;
import java.io.IOException;
import org.junit.Test;
import ua.kovel.java.i2c.devices.PCF8574.Config;
import ua.kovel.java.i2c.tools.I2C;

public class PCF8574Test {

    @Test
    public void pcf8574() throws Exception {
        I2C i2c = new I2C("i2c-tiny-usb", PCF8574.ADDRESS);
        Config config = new Config() {
            @Override
            public int read() throws IOException {
                return i2c.read();
            }

            @Override
            public void write(int command) throws IOException {
                i2c.write(command);
            }
        };

        PCF8574 pcf8574 = new PCF8574(config);

        pcf8574.turnOnAllPins();
        assertEquals(247, pcf8574.readByte());
        pcf8574.setPin(1, false);
        assertEquals(false, pcf8574.getPin(1));
        pcf8574.setPin(1, true);
        assertEquals(true, pcf8574.getPin(1));
        pcf8574.turnOffAllPins();
        assertEquals(0, pcf8574.readByte());
    }

}