package ua.kovel.java.uart.devices;

/**
 * For see raw response from server use:<br>
 * <code>curl -is --raw http://webhook.site/abed78a7-65c6-4fc4-aacd-a2c3fe209783</code>
 *
 * @author ihor.lavryniuk
 */
public class ESP8266AT {

    private static final String OK = "\rOK\r";
    private static final String LINE_END = "\r\n";

    private Config config;

    public void checkModuleConnected() {
        sendCommand("AT");
        if (!expectOK()) {
            throw new RuntimeException("ESP AT module not connected");
        }
    }

    private boolean expectOK() {
        return expectString(OK, 3000);
    }

    private boolean expectSendOK() {
        return expectString("SEND OK\r", 35000);
    }

    private boolean expectString(String expected, int timeout) {
        long start = System.currentTimeMillis();
        while (timeout == 0 || System.currentTimeMillis() < start + timeout) {
            String str = read();
            if (contains(str, expected)) {
                return true;
            }
        }
        debug("Have no expect response");
        return false;
    }

    public boolean contains(String source, String expected) {
        return source.indexOf(expected) >= 0;
    }

    public String httpGet(String host, String path) {
        int linkId = 4;
        sendCommand("AT+CIPSTART=" + linkId + ",\"TCP\",\"" + host + "\"," + 80);
        expectOK();
        String cmd = "GET " + path + " HTTP/1.1\r\n" + "Host: " + host + LINE_END + "Connection: close\r\n\r\n";
        sendCommand("AT+CIPSEND=" + linkId + "," + cmd.length());
        expectString(">", 1000);
        sendData(cmd);
        // expectSendOK();
//         expectString(linkId + ",CLOSED", 3000);
        // delay(1000);
        // sendCommand("AT+CIPCLOSE=" + linkId);
        // expectOK();
        // expectString("+IPD," + linkId, 1000);
        int timeout = 3000;
        long start = System.currentTimeMillis();
        String str = null;
        while (timeout == 0 || System.currentTimeMillis() < start + timeout) {
            str = read();
            if (contains(str, linkId + ",CLOSED\r")) {
                break;
            }
        }
        RawHttp rawHttp = new RawHttp();
        return "{" + rawHttp.parseResponse(str).getBody() + "}";// .replace("\n", " ");
    }

    public void update() {
        sendCommand("AT+CIUPDATE");
        expectOK();
    }

    public void autoConnectToAP() {
        sendCommand("AT+CWAUTOCONN=1");
        expectOK();
    }

    public void setStaticIp(String ip, String defaultGateway, String subnetMask) {
        sendCommand("AT+CIPSTA_DEF=\"" + ip + "\",\"" + defaultGateway + "\",\"" + subnetMask + "\"");
        expectOK();
    }

    public void hostname(String hostName) {
        sendCommand("AT+CWHOSTNAME=\"" + hostName + "\"");
        expectOK();
    }

    public void pingGoogle() {
        ping("8.8.8.8");
    }

    public void ping(String address) {
        sendCommand("AT+PING=\"" + address + "\"");
        expectOK();
    }

    public void dhcp() {
        sendCommand("AT+CWDHCP=2,1");
        expectOK();
    }

    public String getIpAndMacAddressAsString() {
        sendCommand("AT+CIFSR");
        return extractData();
    }

    public String getIp() {
        return substringBetween(getIpAndMacAddressAsString(), "+CIFSR:STAIP,\"", "\"");
    }

    public String getMac() {
        return substringBetween(getIpAndMacAddressAsString(), "+CIFSR:STAMAC,\"", "\"");
    }

    private String substringBetween(String data, String beginKeyWord, String endKeyWord) {
        int start = data.indexOf(beginKeyWord) + beginKeyWord.length();
        int end = data.indexOf("\"", start);
        if (start < 0 || end < 1) {
            throw new RuntimeException("Unable to parse " + data);
        }
        return data.substring(start, end);
    }

    public void connectToWiFi(String ssid, String password) {
        stationMode();
        sendCommand("AT+CWJAP=\"" + ssid + "\",\"" + password + "\"");
        // expectOK();
        expectString("WIFI\nCONNECTED", 3000);
        expectString("WIFI\nGOT\nIP", 3000);
    }

    public void accessPoint(String ssid, String password) {
        stationMode();
        sendCommand("AT+CWSAP=\"" + ssid + "\", \"" + password + ", 5, 3");
        expectOK();
    }

    public void disableAccessPoint() {
        sendCommand("AT+CWSAP");
        expectOK();
    }

    public void switchesEchoOff() {
        sendCommand("ATE0");
        expectOK();
    }

    public void switchesEchoOn() {
        sendCommand("ATE1");
        expectOK();
    }

    public void multipleConnectionModeOn() {
        sendCommand("AT+CIPMUX=1");
        expectOK();
    }

    public void multipleConnectionModeOff() {
        sendCommand("AT+CIPMUX=0");
        expectOK();
    }

    public void webServer(int port) {
        multipleConnectionModeOn();
        sendCommand("AT+CIPSERVER=1," + port);
    }

    public void stationMode() {
        sendCommand("AT+CWMODE=1");
        expectOK();
    }

    public void networksList() {
        sendCommand("AT+CWLAP");
        expectOK();
    }

    public void disconnectsFromAP() {
        sendCommand("AT+CWQAP");
        expectOK();
    }

    public void restart() {
        sendCommand("AT+RST");
        expectOK();
    }

    public void setMaximumTXPower() {
        setTXPower(82);
        expectOK();
    }

    public void setTXPower(int txPower) {
        sendCommand("AT+RFPOWER=" + txPower);
        expectOK();
    }

    public void restoreFactoryDefaultSettings() {
        sendCommand("AT+RESTORE");
        expectOK();
    }

    private String extractData() {
        int timeout = 1000;
        long start = System.currentTimeMillis();
        String str = "";
        while (timeout == 0 || System.currentTimeMillis() < start + timeout) {
            str = read();
            if (contains(str, OK)) {
                break;
            }
        }
        return str.replace(OK, "");
    }

    public String version() {
        sendCommand("AT+GMR");
        return extractData();
    }

    public void get(String host, int port, String path) {
        sendCommand("AT+CIPSTART=4,\"TCP\",\"" + host + "\"," + port);
        delay(1000);
        String cmd = "GET " + path + " HTTP/1.1";
        sendCommand("AT+CIPSEND=4," + cmd.length() + 4);
        delay(1000);
        sendCommand(cmd);
        sendCommand("AT+CIPCLOSE");
        delay(1000);
    }

    public void connect() {
        sendCommand("AT+CIPSTART=\"TCP\",\"httpstat.us\",80");
        delay(1000);
        String cmd = "GET /200 HTTP/1.1";
        sendCommand("AT+CIPSEND=4," + cmd.length() + 4);
        delay(1000);
        sendCommand(cmd);
        delay(1000);
    }

    public void sendCommand(String command) {
        try {
            write(command + LINE_END);
        } catch (Exception exc) {
        }
    }

    public void sendData(String command) {
        try {
            write(command);
        } catch (Exception exc) {
        }
    }

    private void delay(int time) {
        try {
            Thread.sleep(time);
        } catch (Exception exc) {
        }
    }

    public void read(byte[] data) {
        String str = new String(data, 0, data.length);
        // read()
        if (str.equals("OK")) {
            debug("read read read debug");
        }
        debug("d[" + str + "]z");
    }

    public String read() {
        return config.read();
    }

    public void write(String data) {
        config.write(data);
    }

    public void debug(String data) {
        config.debug(data);
    }

    public void configure(Config config) {
        this.config = config;
    }
}

class RawHttp {

    public final RawHttpResponse parseResponse(String response) {
        return new RawHttpResponse(response);
    }

}

class RawHttpResponse {

    private String response;

    public RawHttpResponse(String response) {
        this.response = response;
    }

    public String getBody() {
        return getBody(response);
    }

    public static String getBody(String response) {
        String result = response;
        String s1 = "\r\n\r\n";
        String s2 = "\r\n";
        int startBodyLength = response.indexOf(s1, response.indexOf("+IPD,")) + s1.length();
        int endBodyLength = response.indexOf(s2, startBodyLength);
        int bodyLength = Integer.valueOf(response.substring(startBodyLength, endBodyLength));
//        int endBody = response.indexOf(s3) - s3.length();
//        if (startBodyLength < 1 || endBody < 1) {
//            throw new RuntimeException("Unable to parse http body response " + result);
//        }
        return result.substring(endBodyLength + s2.length(), endBodyLength + s2.length() + bodyLength);
    }
}

interface Config {
    String read();

    void write(String data);

    void debug(String data);
}