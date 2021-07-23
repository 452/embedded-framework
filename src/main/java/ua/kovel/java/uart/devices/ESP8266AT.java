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

    public String udpSendReceive(String host, int remotePort, int localPort, String data) {
        // https://www.espressif.com/sites/default/files/documentation/4b-esp8266_at_command_examples_en.pdf
        int linkId = 4;
        sendCommand("AT+CIPSTART=" + linkId + ",\"UDP\",\"" + host + "\"," + remotePort + "," + localPort + ",0");
        expectOK();
        sendCommand("AT+CIPSEND=" + linkId + "," + data.length() + ",\"" + host + "\"," + remotePort);
        expectString(">", 1000);
        sendData(data);
        delay(1000);
        sendCommand("AT+CIPCLOSE=" + linkId);
        int timeout = 3000;
        long start = System.currentTimeMillis();
        String str = null;
        while (timeout == 0 || System.currentTimeMillis() < start + timeout) {
            str = read();
            if (contains(str, linkId + ",CLOSED\r")) {
                break;
            }
        }
        return parseUdpBody(str);
    }

    public String parseUdpBody(String data) {
        return data;
    }

    public String httpGet(String host, int port, String path) {
        if (path == null || path.isEmpty()) {
            path = "/";
        }
        int linkId = 4;
        sendCommand("AT+CIPSTART=" + linkId + ",\"TCP\",\"" + host + "\"," + port);
        expectOK();
        String cmd = "GET " + path + " HTTP/1.1\r\n" + "Host: " + host + LINE_END + "Connection: close\r\n\r\n";
        sendCommand("AT+CIPSEND=" + linkId + "," + cmd.length());
        expectString(">", 1000);
        sendData(cmd);
        // expectSendOK();
        // expectString(linkId + ",CLOSED", 3000);
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
        return rawHttp.parseResponse(str).getBody();
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
        debug("Connection Information");
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
        if (ssid == null || ssid.isEmpty() || password == null || password.isEmpty()) {
            throw new RuntimeException("Please provide wifi ssid and password");
        }
        stationMode();
        sendCommand("AT+CWJAP=\"" + ssid + "\",\"" + password + "\"");
        // expectOK();
        expectString("WIFI\nCONNECTED", 3000);
        expectString("WIFI\nGOT\nIP", 3000);
    }

    public void accessPoint(String ssid, String password) {
        stationMode();
        debug("Connecting to AP");
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
        debug("Turn on Multiple Connections");
        sendCommand("AT+CIPMUX=1");
        expectOK();
    }

    public void multipleConnectionModeOff() {
        debug("Turn off Multiple Connections");
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

    private static boolean contains(String source, String expected) {
        return source.indexOf(expected) >= 0;
    }

    public static String getBody(String response) {
        if (response == null || response.isEmpty()) {
            throw new RuntimeException("Have no response from ESP AT WiFi shield, check wire connection");
        }
        if (contains(response, "\rContent-Length:")) {
            return parseBodyWithContentLengthHeaderAlg(response);
        }
        // return parseBodyWithHexContentLengthAlg(response);
        return parseBodyWithNumberContentLengthAlg(response);
    }

    private static String parseBodyWithContentLengthHeaderAlg(String response) {
        String s0 = "\r\r+IPD,";
        String s1 = "\r\r";
        String s2 = "\r";
        String s3 = "\rContent-Length: ";
        int startBodyLength = response.indexOf(s3, response.indexOf(s0) + s0.length()) + s3.length();
        int endBodyLength = response.indexOf(s2, startBodyLength);
        if (startBodyLength < 1 || endBodyLength < 1 || startBodyLength == endBodyLength) {
            throw new RuntimeException("Unable to parse http body response " + response);
        }
        int bodyLength = Integer.valueOf(response.substring(startBodyLength, endBodyLength));
        int startBody = response.indexOf(s1, endBodyLength + s2.length()) + s1.length();
        int endBody = (startBody + bodyLength) - s1.length() - s2.length() - 1;
        String substring = response.substring(startBody, endBody);
        return substring;
    }

    private static String parseBodyWithHexContentLengthAlg(String response) {
        String s0 = "\r\r+IPD,";
        String s1 = "\r\r";
        String s2 = "\r";
        String s3 = "0\r\r";
        int startBodyLength = response.indexOf(s1, response.indexOf(s0) + s0.length()) + s1.length();
        int endBodyLength = response.indexOf(s2, startBodyLength);
        if (startBodyLength < 1 || endBodyLength < 1 || startBodyLength == endBodyLength) {
            throw new RuntimeException("Unable to parse http body response " + response);
        }
        int bodyLength = Integer.valueOf(response.substring(startBodyLength, endBodyLength), 16);
        int startBody = endBodyLength + s2.length();
        int endBody = (endBodyLength + bodyLength) - s3.length();
        if (startBody > endBody) {
            endBody = (endBodyLength + bodyLength) + s2.length();
        }
        String substring = response.substring(startBody, endBody);
        return substring;
    }

    private static String parseBodyWithNumberContentLengthAlg(String response) {
        String s0 = "\r\r\r+IPD,";
        String s1 = ",";
        String s2 = ":";
        int ipd = response.indexOf(s0) + s0.length();
        int startBodyLength = response.indexOf(s1, ipd) + s1.length();
        int endBodyLength = response.indexOf(s2, startBodyLength);
        if (startBodyLength < 1 || endBodyLength < 1 || startBodyLength == endBodyLength) {
            throw new RuntimeException("Unable to parse http body response " + response);
        }
        int bodyLength = Integer.valueOf(response.substring(startBodyLength, endBodyLength));
        int startBody = endBodyLength + s2.length();
        int endBody = (s2.length() + endBodyLength + bodyLength);
        if (startBody > endBody) {
            endBody = (endBodyLength + bodyLength) + s2.length();
        }
        String substring = response.substring(startBody, endBody);
        return substring;
    }
}

interface Config {
    String read();

    void write(String data);

    void debug(String data);
}