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
    public static String esp_at_http1_1_Response200OK_GET_452() {
        return "\rRecv 93 bytes\r\rSEND OK\r\r+IPD,4,479:HTTP/1.1 200 OK\rServer: nginx/1.14.2\rContent-Type: text/plain; charset=UTF-8\rTransfer-Encoding: chunked\rConnection: close\rVary: Accept-Encoding\rX-Request-Id: 4c3e916b-f534-4c5f-8474-8d8c0fcd287d\rX-Token-Id: 76158f46-9f73-4818-98f4-f37eb05453ab\rCache-Control: no-cache, private\rDate: Tue, 17 Nov 2020 08:29:20 GMT\rSet-Cookie: laravel_session=fwaVfvlnxTzbeVCjCDm7PIYMGz8bZc0sflc1B7B1; expires=Tue, 17-Nov-2020 10:29:20 GMT; Max-Age=7200; path=/; httponly\r\r3\r452\r0\r\r4,CLOSED\r";
    }

}