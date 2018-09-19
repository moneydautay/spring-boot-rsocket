package org.mvnsearch.spring.boot.rsocket;

import org.junit.Assert;
import org.junit.Test;
import org.mvnsearch.rsocket.RSocketProtos;

import java.nio.ByteBuffer;
import java.util.UUID;

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

}
