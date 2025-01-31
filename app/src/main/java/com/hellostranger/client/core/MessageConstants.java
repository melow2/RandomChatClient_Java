package com.hellostranger.client.core;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public abstract class MessageConstants {

    public static final String REQUIRE_ACCESS = "REQUIRE_ACCESS";
    public static final String MESSAGING = "MESSAGING";
    public static final String CONNECTION = "CONNECTION";
    public static final String MSG_DELIM = "/";
    public static final String NEW_CLIENT = "NEW_CLIENT";
    public static final String QUIT_CLIENT = "QUIT_CLIENT";
    public static final String RE_CONNECT = "RE_CONNECT";
    public static final String MSG_CONNECT_FAIL = "현재 서버에 사용자가 많습니다.";
    public static final String MSG_REQUIRE_RECONNECT="서버와 연결이 끊어졌습니다. 앱을 다시 실행 해주세요";
    public static String MSG_BUSY = "현재 사용자가 많습니다";
    public static Charset charset = Charset.forName("UTF-8");
    public static CharsetEncoder encoder = charset.newEncoder();

    public static ByteBuffer parseMessage(String msg) throws CharacterCodingException {
        ByteBuffer buffer = ByteBuffer.allocate(1024*100);
        buffer.clear();
        buffer = encoder.encode(CharBuffer.wrap(msg));
        return buffer;
    }

}
