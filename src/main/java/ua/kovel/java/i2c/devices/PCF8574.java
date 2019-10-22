package ua.kovel.java.i2c.devices;

import java.io.IOException;

/**
 * 0x27 is the address of the fc-113 PCF8574 expander
 * i2cset -y 1 0x27 0xf0 is the output state as we set, 4 bits in HIGH and 4 bits in LOW
 * i2cget -y 9 0x27 read the state of PCF8574 I/O ports
 */
public class PCF8574 {

    public static final int ADDRESS = 0x27;

    private Config config;

    /**
     * because the PCF8574 is a 'quasi-bidirectional' any value written
     * will be changed by a read, so cache the values written to keep
     * track of the current state of the port pins.
     */
    private int currentValue = 0x00;

    public PCF8574(Config config) throws IOException {
        this.config = config;
    }

    public void configure(Config config) {
        this.config = config;
    }

    public interface Config {
        int read() throws IOException;
        void write(int value) throws IOException;
    }

    public void write(int value) throws IOException {
        config.write(value);
    }

    /**
     * return the bit value of pin number
     * @param pin number [0:7]
     * @return bit value [0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80] etc
     */
    public static int BV(int pin) {
      return (1 << pin);
    }

    /**
     * Write a byte to the IO port applying a mask to the data. This enables the
     * setting of port state while not affecting the state of other port pins.
     * @param mask 8 bit mask, data does not affect port state when mask bit is 1
     * @param data The data to write to the port
     * @return true when data written to port
     * @throws IOException
     */
    private void writeByte(int mask, int data) throws IOException {

      int value = currentValue;

      data &= ~mask;  // apply the mask to the data
      value &= mask;  // apply the mask to the current value
      value |= data;  // apply the data to the current value
      value &= 0xFF;  // clear any higher bits

      config.write((byte) (value & 0xFF));
      currentValue = value;
    }

    /**
     * Read the last value written to the port.
     * @return last value written to the port (LSB is value)
     */
    public int readValue() {
      return (currentValue & 0xFF);
    }

    /**
     * set the given port pin to the given value
     * @param pin the pin to set [0:7]
     * @param state true to set it, false to clear it
     * @throws IOException 
     */
    public void setPin(int pin, boolean state) throws IOException {
      if (pin < 0 || pin > 7) return;
      int mask = ~BV(pin);
      int data = state ? BV(pin) : 0;
      writeByte(mask, data & 0xFF);
    }

    /**
     * Get the state of the given port pin
     * @param pin the pin to get [0:7]
     * @return true when set (logic 1), false when clear (logic 0)
     * @throws IOException 
     */
    public boolean getPin(int pin) throws IOException {
      if (pin < 0 || pin > 7) return false;
      int value = readByte();
      return ((value & BV(pin)) == BV(pin));
    }

    /**
     * Read the port data, the state of all pins.
     * @return the byte read from the port (as an int LSB is port data)
     * @throws IOException 
     */
    public int readByte() throws IOException {
      return config.read();
    }

    public void turnOnAllPins() throws IOException {
        config.write(255);
    }

    public void  turnOffAllPins() throws IOException {
        config.write(0);
    }
}