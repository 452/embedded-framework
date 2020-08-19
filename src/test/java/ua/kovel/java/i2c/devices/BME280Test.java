package ua.kovel.java.i2c.devices;

import io.dvlopt.linux.i2c.I2CBus;
import io.dvlopt.linux.i2c.SMBus;
import org.junit.Test;
import ua.kovel.java.i2c.tools.I2C;
import ua.kovel.java.i2c.devices.BME280.Config;

import static org.junit.Assert.*;

import java.io.IOException;

public class BME280Test {

    @Test
    public void bme280() throws Exception {
        I2C i2c = new I2C("i2c-tiny-usb", BME280.ADDRESS);
        Config config = new Config() {
            @Override
            public int read(int address) throws IOException {
                return i2c.readByte(address);
            }

            @Override
            public void read(int address, byte[] buffer, int length) throws Exception {
                i2c.readBytes(address, buffer, length);
            }

            @Override
            public void write(int address, byte command) throws IOException {
                i2c.writeByte(address, command);
            }
        };

        BME280 bme280 = new BME280(config);

        bme280.calc();
        System.out.printf("Temperature in Celsius : %.2f C %n", bme280.getTemperature());
        //System.out.printf("Temperature in Fahrenheit : %.2f F %n", fTemp);
        System.out.printf("Pressure : %.2f hPa %n", bme280.getPressure());
        System.out.printf("Relative Humidity : %.2f %% RH %n", bme280.getHumidity());
        assertTrue(bme280.getTemperature() > 9);
        assertNotEquals(0, bme280.getTemperature());
    }

    @Test
    public void bme280Lib2() throws Exception {
        I2CBus bus = new I2CBus(1);
        bus.selectSlave(BME280.ADDRESS);
        Config config = new Config() {
            @Override
            public int read(int address) throws IOException {
                return bus.smbus.readByte(address);
            }

            @Override
            public void read(int address, byte[] buffer, int length) throws Exception {
                SMBus.Block block = new SMBus.Block();
                bus.smbus.readI2CBlock(address, block, length);
                for (int i = 0; i < length; i++) {
                    buffer[i] = (byte) block.get(i);
                }
            }

            @Override
            public void write(int address, byte command) throws IOException {
                bus.smbus.writeByte(command, address);
            }
        };

        BME280 bme280 = new BME280(config);

        bme280.calc();
        assertNotEquals(0, bme280.getTemperature());
        bus.close();
    }
}