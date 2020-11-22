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

    public static String response200OK_GET_452() {
        return "\rRecv 93 bytes\r\rSEND OK\r\r+IPD,4,479:HTTP/1.1 200 OK\rServer: nginx/1.14.2\rContent-Type: text/plain; charset=UTF-8\rTransfer-Encoding: chunked\rConnection: close\rVary: Accept-Encoding\rX-Request-Id: 4c3e916b-f534-4c5f-8474-8d8c0fcd287d\rX-Token-Id: 76158f46-9f73-4818-98f4-f37eb05453ab\rCache-Control: no-cache, private\rDate: Tue, 17 Nov 2020 08:29:20 GMT\rSet-Cookie: laravel_session=fwaVfvlnxTzbeVCjCDm7PIYMGz8bZc0sflc1B7B1; expires=Tue, 17-Nov-2020 10:29:20 GMT; Max-Age=7200; path=/; httponly\r\r3\r452\r0\r\r4,CLOSED\r";
    }

    public static String response200OK_GET_Json() {
        return "\rRecv 93 bytes\r\rSEND OK\r\r+IPD,4,529:HTTP/1.1 200 OK\rServer: nginx/1.14.2\rContent-Type: text/plain; charset=UTF-8\rTransfer-Encoding: chunked\rConnection: close\rVary: Accept-Encoding\rX-Request-Id: f9102c7b-9e61-4fcf-8e67-d25c6f378038\rX-Token-Id: 76158f46-9f73-4818-98f4-f37eb05453ab\rCache-Control: no-cache, private\rDate: Fri, 20 Nov 2020 18:56:02 GMT\rSet-Cookie: laravel_session=BQCKJm5bR68lF4G8iQS6WlsoZ4yAou8y7kGfYJUU; expires=Fri, 20-Nov-2020 20:56:02 GMT; Max-Age=7200; path=/; httponly\r\r34\r{\r   \"name\":\"John\",\r   \"age\":30,\r   \"car\":null\r}\r0\r\r4,CLOSED\r";
    }

    public static String response200OK_GET_JsonWithMetadata() {
        return "\rRecv 93 bytes\r\rSEND OK\r\r+IPD,4,691:HTTP/1.1 200 OK\rServer: nginx/1.14.2\rContent-Type: text/plain; charset=UTF-8\rTransfer-Encoding: chunked\rConnection: close\rVary: Accept-Encoding\rX-Request-Id: 37268ecf-dad4-4444-8dc3-f0588cce6179\rX-Token-Id: 76158f46-9f73-4818-98f4-f37eb05453ab\rCache-Control: no-cache, private\rDate: Fri, 20 Nov 2020 22:11:48 GMT\rSet-Cookie: laravel_session=p2OCkrP4GplYow70j2pxGsSfpTcP46DY1rFh1MrW; expires=Sat, 21-Nov-2020 00:11:48 GMT; Max-Age=7200; path=/; httponly\r\rd6\rSet-Cookie: laravel_session=BQCKJm5bR68lF4G8iQS6WlsoZ4yAou8y7kGfYJUU; expires=Fri, 20-Nov-2020 20:56:02 GMT; Max-Age=7200; path=/; httponly\r\r34\r{\r   \"name\":\"John\",\r   \"age\":30,\r   \"car\":null\r}\r0\r\r4,CLOSED\r0\r\r4,CLOSED\r";
    }

    public static String response200OK_GET_XmlWithContentLength() {
        return "\rRecv 59 bytes\r\rSEND OK\r\r+IPD,4,266:HTTP/1.1 200 OK\rContent-Type: text/html; charset=utf-8\rLast-Modified: Fri, 20 Nov 2020 22:48:12 GMT\rDate: Fri, 20 Nov 2020 23:03:25 GMT\rContent-Length: 85\rConnection: close\r\r<pre><a href=\"sheret.exe\">sheret.exe</a><a href=\"sheret.log\">sheret.log</a></pre>4,CLOSED\r";
    }

    public static String response200OK_GET_VaryAcceptEncodingChunked() {
        return "\rRecv 73 bytes\r\rSEND OK\r\r+IPD,4,152:HTTP/1.1 200 OK\rConnection: close\rContent-Type: text/xml\rMatched-Stub-Id: c1eca6b5-d44e-4644-ae97-c311c18600bf\rVary: Accept-Encoding, User-Agent\r\r\r+IPD,4,7:<respon\r+IPD,4,7:se>Some\r+IPD,4,7:  cont\r+IPD,4,7:ent\r</r\r+IPD,4,8:esponse>4,CLOSED\r";
    }

    public static String response404_GET() {
        return "\rRecv 73 bytes\r\rSEND OK\r\r+IPD,4,71:HTTP/1.1 404 Not Found\rConnection: close\rContent-Type: text/plain\r\r\r+IPD,4,84:No response could be served as there are no stub mappings in this WireMock instance.4,CLOSED\r";
    }

    public static String udpSensorResponse() {
        return "\rRecv 3 bytes\r\rSEND OK\r\r+IPD,4,107:BME280;1606057304;4.4;1002.3;91.7;63.8;-89;63.9990234375;1;202004152002;1042542;1458400;5C:CF:7F:0F:E8:6E;4,CLOSED\r\rOK\r";
    }

}