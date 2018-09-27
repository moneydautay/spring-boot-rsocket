package org.mvnsearch.spring.boot.rsocket;

import org.mvnsearch.rsocket.RSocketProtos;
import reactor.core.publisher.Flux;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * java method metadata
 *
 * @author linux_china
 */
public class JavaMethodMetadata {
    private static byte[] EMPTY_BODY = new byte[]{0};
    private String classFullName;
    private String name;
    private int parameterCount;
    /**
     * bi directional indicate, parameter type & return type are both Flux
     */
    private boolean isBiDirectional = false;
    private RSocketProtos.PayloadMetadata.Encoding encoding;

    public JavaMethodMetadata(Method method) {
        this.classFullName = method.getDeclaringClass().getCanonicalName();
        this.name = method.getName();
        //parameter
        this.parameterCount = method.getParameterCount();
        if (parameterCount == 0) {
            encoding = RSocketProtos.PayloadMetadata.Encoding.VOID;
        } else if (parameterCount == 1) {
            Class<?> parameterType = method.getParameterTypes()[0];
            if (parameterType.equals(Boolean.TYPE) || parameterType.equals(Boolean.class)) {
                encoding = RSocketProtos.PayloadMetadata.Encoding.BOOL;
            } else if (parameterType.equals(Byte.TYPE) || parameterType.equals(Byte.class)) {
                encoding = RSocketProtos.PayloadMetadata.Encoding.BYTE;
            } else if (parameterType.equals(Character.TYPE) || parameterType.equals(Character.class)) {
                encoding = RSocketProtos.PayloadMetadata.Encoding.CHAR;
            } else if (parameterType.equals(Short.TYPE) || parameterType.equals(Short.class)) {
                encoding = RSocketProtos.PayloadMetadata.Encoding.SHORT;
            } else if (parameterType.equals(Integer.TYPE) || parameterType.equals(Integer.class)) {
                encoding = RSocketProtos.PayloadMetadata.Encoding.INT;
            } else if (parameterType.equals(Long.TYPE) || parameterType.equals(Long.class)) {
                encoding = RSocketProtos.PayloadMetadata.Encoding.LONG;
            } else if (parameterType.equals(Float.TYPE) || parameterType.equals(Float.class)) {
                encoding = RSocketProtos.PayloadMetadata.Encoding.FLOAT;
            } else if (parameterType.equals(Double.TYPE) || parameterType.equals(Double.class)) {
                encoding = RSocketProtos.PayloadMetadata.Encoding.DOUBLE;
            } else if (parameterType.equals(String.class)) {
                encoding = RSocketProtos.PayloadMetadata.Encoding.STRING;
            }
            //todo Date encoding
        }
        if (encoding == null) {
            encoding = RSocketProtos.PayloadMetadata.Encoding.HESSIAN;
        }
        //return type
        if (parameterCount == 1 && method.getParameterTypes()[0].equals(Flux.class)) {
            isBiDirectional = true;
        }
    }

    public String getClassFullName() {
        return classFullName;
    }

    public void setClassFullName(String classFullName) {
        this.classFullName = classFullName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getParameterCount() {
        return parameterCount;
    }

    public void setParameterCount(int parameterCount) {
        this.parameterCount = parameterCount;
    }

    public RSocketProtos.PayloadMetadata.Encoding getEncoding() {
        return encoding;
    }

    public void setEncoding(RSocketProtos.PayloadMetadata.Encoding encoding) {
        this.encoding = encoding;
    }

    public boolean isBiDirectional() {
        return isBiDirectional;
    }

    public void setBiDirectional(boolean biDirectional) {
        isBiDirectional = biDirectional;
    }

    public ByteBuffer encodingBody(Object[] args) {
        //todo no strategy pattern because of performance  and more encoding, byte buffer performance
        if (this.encoding == RSocketProtos.PayloadMetadata.Encoding.VOID) {
            return ByteBuffer.wrap(EMPTY_BODY);
        } else if (this.encoding == RSocketProtos.PayloadMetadata.Encoding.INT) {
            ByteBuffer buf = ByteBuffer.allocate(4).putInt((int) args[0]);
            buf.flip();
            return buf;
        } else if (this.encoding == RSocketProtos.PayloadMetadata.Encoding.LONG) {
            ByteBuffer buf = ByteBuffer.allocate(8).putLong((long) args[0]);
            buf.flip();
            return buf;
        } else if (this.encoding == RSocketProtos.PayloadMetadata.Encoding.STRING) {
            return ByteBuffer.wrap(((String) args[0]).getBytes(StandardCharsets.UTF_8));
        } else { // encoding by hessian default
            return ByteBuffer.wrap(HessianUtils.output(args));
        }

    }
}
