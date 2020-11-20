package ua.kovel.java.uart.devices;

import com.github.tomakehurst.wiremock.core.Options.ChunkedEncodingPolicy;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
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

    private static final int MOCK_SERVER_PORT = 8089;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(
            options()
                    .port(MOCK_SERVER_PORT)
                    .useChunkedTransferEncoding(ChunkedEncodingPolicy.ALWAYS)
    );

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
    public void mockBodyWithoutContentLengthTest() {
        assertThat(RawHttpResponse.getBody(EspAtResponseMock.response200OK_GET_452())).as("Expected response text is '452'").isEqualTo("452");
    }

    @Test
    public void mockBodyWithContentLengthTest() {
        assertThat(RawHttpResponse.getBody(EspAtResponseMock.response200OK_GET_XmlWithContentLength())).as("Expected response xml").isEqualTo("<pre><a href=\"sheret.exe\">sheret.exe</a><a href=\"sheret.log\">sheret.log</a></pre>");
    }

    @Test
    public void mockBodyJsonTest() {
        assertThat(RawHttpResponse.getBody(EspAtResponseMock.response200OK_GET_Json())).as("Expected response json").isEqualTo("{\r   \"name\":\"John\",\r   \"age\":30,\r   \"car\":null\r}");
    }

    @Test
    public void mockBodyJsonWithMetadataTest() {
        assertThat(RawHttpResponse.getBody(EspAtResponseMock.response200OK_GET_JsonWithMetadata())).as("Expected response json without metadata")
                .isEqualTo(
                        "Set-Cookie: laravel_session=BQCKJm5bR68lF4G8iQS6WlsoZ4yAou8y7kGfYJUU; expires=Fri, 20-Nov-2020 20:56:02 GMT; Max-Age=7200; path=/; httponly\r\r34\r{\r   \"name\":\"John\",\r   \"age\":30,\r   \"car\":null\r}\r0\r\r4,CLOSED"
                );
    }

    @Test
    public void mockBody404Test() {
        assertThat(RawHttpResponse.getBody(EspAtResponseMock.response404_GET())).as("Expected response json without metadata")
                .isEqualTo(
                        "No response could be served as there are no stub mappings in this WireMock instance."
                );
    }

    @Test
    public void mockChunkedVaryAcceptEncodingSupportTest() {
        assertThat(RawHttpResponse.getBody(EspAtResponseMock.response200OK_GET_VaryAcceptEncodingChunked()))
                .isEqualTo(
                        "<response>Some \n content\r</response>"
                );
    }

    @Test
    public void queryParamTest() {
        // http://wiremock.org/docs/getting-started
        connectToWifi();

        String path = "/test/some.json?country=Ukraine&city=Kovel";
        stubFor(
                get(urlEqualTo(path))
                        .withQueryParam("country", equalTo("Ukraine"))
                        .withQueryParam("city", equalTo("Kovel"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "text/xml")
                                        .withBody("<response>Some\n content\r</response>")
                        )
        );
        assertThat(shield.httpGet(getIpAddress(), MOCK_SERVER_PORT, path))
                .isEqualTo("<response>Some content</response>");
    }

    private String getIpAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void usedForWriteingTestCaseTest() {
        connectToWifi();
        System.out.println("Begin");
        shield.hostname("Javaino");
        System.out.println(shield.getIpAndMacAddressAsString());
        // System.out.println(shield.httpGet("webhook.site", "/76158f46-9f73-4818-98f4-f37eb05453ab"));
        assertThat(shield.httpGet("webhook.site", 80, "/76158f46-9f73-4818-98f4-f37eb05453ab"))
                .as("Used for write test case")
                .isEqualTo("{\r   \"name\":\"John\",\r   \"age\":30,\r   \"car\":null\r}");
    }

    private void connectToWifi() {
        setup();
        shield.multipleConnectionModeOn();
        shield.connectToWiFi(getWifiSsid(), getWifiPassword());
    }

    private String getWifiSsid() {
        return System.getenv("WIFI_SSID");
    }

    private static String getWifiPassword() {
        return System.getenv("WIFI_PASSWORD");
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