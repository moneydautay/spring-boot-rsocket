package org.mvnsearch.spring.boot.rsocket;

import org.junit.Assert;
import org.junit.Test;
import org.mvnsearch.rsocket.RSocketProtos;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * encoding test
 *
 * @author linux_china
 */
public class EncodingTest {

    @Test
    public void testProtoEncoding() throws Exception {
        String service = "org.mvnsearch.UserService";
        RSocketProtos.PayloadMetadata metadata = RSocketProtos.PayloadMetadata.newBuilder()
                .setEncoding(RSocketProtos.PayloadMetadata.Encoding.PROTO)
                .setService(service)
                .setRpc("findById")
                .setTraceId(UUID.randomUUID().toString()).build();
        byte[] metadataBytes = metadata.toByteArray();
        RSocketProtos.PayloadMetadata metadata1 = RSocketProtos.PayloadMetadata.parseFrom(metadataBytes);
        Assert.assertEquals(metadata1.getService(), metadata.getService());
    }

    @Test
    public void testNumberEncoding() {
        int age = 12;
        ByteBuffer byteBuffer = ByteBuffer.allocate(4).putInt(age);
        byteBuffer.clear();
        int age2 = byteBuffer.getInt();
        Assert.assertEquals(age, age2);
    }

    @Test
    public void testWildInvoke() throws Exception {
        Method method = findOnlyMethod(this.getClass(), "findById");
        Object result1 = method.invoke(this, 1);
        Object result2 = method.invoke(this, new Object[]{1});
        Assert.assertEquals(result1, result2);
    }

    public String findById(Integer id) {
        return "nick";
    }

    private Method findOnlyMethod(Class clazz, String methodName) {
        return Stream.of(clazz.getMethods()).filter(method -> method.getName().equals(methodName)).findFirst().get();
    }

}
