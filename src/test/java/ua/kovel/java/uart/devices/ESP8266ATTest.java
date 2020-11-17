package ua.kovel.java.uart.devices;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ua.kovel.java.uart.devices.mock.EspAtResponseMock;
import ua.kovel.java.uart.devices.util.SerialPortArduinoHelper;

/*
Firmware versions on two modules
    AT version:1.2.0.0(Jul 1 2016 20:04:45) SDK version:1.5.4.1(39cb9a32) Ai-Thinker Technology Co. Ltd. v1.5.4.1-a Nov 30 2017 15:54:29 OK
    AT version:1.3.0.0(Jul 14 2016 18:54:01) SDK version:2.0.0(5a875ba) v1.0.0.3 Mar 13 2018 09:37:06 OK
*/
public class ESP8266ATTest {

    private static ESP8266AT shieldMock = mock(ESP8266AT.class);
    private static ESP8266AT shield = new ESP8266AT();
    private static Config config;
    private static SerialPortArduinoHelper serialPort;

    @BeforeClass
    public static void before() {
        when(shieldMock.getIpAndMacAddressAsString())
                .thenReturn(EspAtResponseMock.ipAndMacResponse());
    }

    @Test
    public void parseIpAddressTest() {
        doCallRealMethod().when(shieldMock).getIp();
        assertThat(shieldMock.getIp()).as("IP address").isEqualTo("192.168.3.99");
    }

    @Test
    public void parseMacAddressTest() {
        doCallRealMethod().when(shieldMock).getMac();
        assertThat(shieldMock.getMac()).as("MAC address").isEqualTo("84:f3:eb:cb:87:b4");
    }

    @Test
    public void espATresponseBodyTest() {
        assertThat(RawHttpResponse.getBody(EspAtResponseMock.esp_at_http1_1_Response200OK_GET_452())).as("Expected response text is '452'").isEqualTo("452");
    }

    @Test
    public void moduleConnectedTest() {
        setup();
        shield.checkModuleConnected();
    }

    @Test
    public void firmwareVersionTest() {
        setup();
        assertThat(shield.version()).as("Firmware version").isEqualToIgnoringWhitespace(EspAtResponseMock.version1300());
    }

    @Test
//    @Ignore
    public void esp8266WiFiConectionTest() {
        setup();
        System.out.println("Begin");
        shield.multipleConnectionModeOn();
        shield.hostname("Javaino");
        shield.connectToWiFi("}RooT-Ukraine{", "");
        System.out.println(shield.getIpAndMacAddressAsString());
//        while (true) {
//            // System.out.println(shield.version());
//            System.out.println(shield.httpGet("webhook.site", "/76158f46-9f73-4818-98f4-f37eb05453ab"));
            assertThat(shield.httpGet("webhook.site", "/76158f46-9f73-4818-98f4-f37eb05453ab"))
                    .as("Expected response text is '452'")
                    .isEqualTo("452");
//        }
    }

    private void setup() {
        if (serialPort == null) {
            serialPort = new SerialPortArduinoHelper("COM3", 115200);
            serialPort.openConnection();

            assertThat(serialPort).as("Serial port try to open").isNotNull();

            config = new Config() {

                @Override
                public String read() {
                    String recieved = serialPort.serialRead(3000);
                    if (!recieved.isEmpty()) {
                        debug("Received: [" + recieved + "]");
                    }
                    return recieved;
                }

                @Override
                public void write(String data) {
                    debug("Send: [" + data + "]");
                    serialPort.serialWrite(data);
                }

                @Override
                public void debug(String data) {
                    System.out.println("Debug: " + data);
                }
            };
            shield.configure(config);
            shield.switchesEchoOff();
        }
    }
}