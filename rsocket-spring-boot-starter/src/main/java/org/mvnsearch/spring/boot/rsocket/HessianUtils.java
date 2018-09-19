package org.mvnsearch.spring.boot.rsocket;

import com.caucho.hessian.io.HessianSerializerInput;
import com.caucho.hessian.io.HessianSerializerOutput;
import org.mvnsearch.spring.boot.rsocket.io.ByteBufferBackedInputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

/**
 * hessian utils
 *
 * @author linux_china
 */
public class HessianUtils {

    public static byte[] output(Object obj) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            HessianSerializerOutput output = new HessianSerializerOutput(bos);
            output.writeObject(obj);
            output.flush();
            return bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object input(byte[] content) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(content);
            HessianSerializerInput input = new HessianSerializerInput(bis);
            return input.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object input(ByteBuffer buffer) throws Exception {
        HessianSerializerInput input = new HessianSerializerInput(new ByteBufferBackedInputStream(buffer));
        return input.readObject();
    }

}
