package ua.kovel.java.uart.devices.mock;

public class EspAtResponseMock {

    public static String version1200() {
        return "AT version:1.2.0.0(Jul  1 2016 20:04:45)\r\n"
                + "SDK version:1.5.4.1(39cb9a32)\r\n"
                + "Ai-Thinker Technology Co. Ltd.\r\n"
                + "v1.5.4.1-a Nov 30 2017 15:54:29\r\n";
    }

    public static String version1300() {
        return "AT version:1.3.0.0(Jul 14 2016 18:54:01)\r\n"
                + "SDK version:2.0.0(5a875ba)\r\n"
                + "v1.0.0.3\r\n"
                + "Mar 13 2018 09:37:06";
    }

    public static String ipAndMacResponse() {
        return "+CIFSR:STAIP,\"192.168.3.99\"\r\n"
                + "+CIFSR:STAMAC,\"84:f3:eb:cb:87:b4\"\r\n";
    }

    /*
     * 452
     */
    public static String curl_http1_1_Response200OK_GET_452() {
        return "Recv 93 bytes\r\n"
                + "\r\n"
                + "SEND OK\r\n"
                + "\r\n"
                + "+IPD,4,479:HTTP/1.1 200 OK\r\n"
                + "Server: nginx/1.14.2\r\n"
                + "Content-Type: text/plain; charset=UTF-8\r\n"
                + "Transfer-Encoding: chunked\r\n"
                + "Connection: close\r\n"
                + "Vary: Accept-Encoding\r\n"
                + "X-Request-Id: 313cde44-710d-4d9c-94af-d5c0a81fcd4b\r\n"
                + "X-Token-Id: 76158f46-9f73-4818-98f4-f37eb05453ab\r\n"
                + "Cache-Control: no-cache, private\r\n"
                + "Date: Sun, 15 Nov 2020 21:29:49 GMT\r\n"
                + "Set-Cookie: laravel_session=uu7k38SgU3fmNfYWSqu0vheHgiMD13rUeJVR5fMd; expires=Sun, 15-Nov-2020 23:29:49 GMT; Max-Age=7200; path=/; httponly\r\n"
                + "\r\n"
                + "3\r\n"
                + "452\r\n"
                + "0\r\n"
                + "\r\n"
                + "4,CLOSED";
    }

}