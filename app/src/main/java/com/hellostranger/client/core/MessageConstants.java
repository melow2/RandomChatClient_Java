package com.hellostranger.client.core;

import com.bumptech.glide.util.Preconditions;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
    public static Charset charset = Charset.forName("UTF-8");
    public static CharsetEncoder encoder = charset.newEncoder();

    public static ByteBuffer parseMessage(String msg) throws CharacterCodingException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.clear();
        buffer = encoder.encode(CharBuffer.wrap(msg));
        return buffer;
    }

}
