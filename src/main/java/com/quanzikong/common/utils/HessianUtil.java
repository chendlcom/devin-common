package com.quanzikong.common.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

/**
 * hessian serialize
 *
 * @author author 2018-04-26 02:53:29
 */
public class HessianUtil {

    /**
     * serialize
     *
     * @param obj obj
     * @param <T> T
     *
     * @return byte[]
     */
    public static <T> byte[] serialize(T obj) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        HessianOutput ho = new HessianOutput(os);
        try {
            ho.writeObject(obj);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        return os.toByteArray();
    }

    /**
     * deserialize
     *
     * @param bytes bytes
     * @param clazz clazz
     * @param <T>   T
     *
     * @return Object
     */
    public static <T> T deserialize(byte[] bytes, Class<T> clazz) {
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        HessianInput hi = new HessianInput(is);
        try {
            return (T)hi.readObject();
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

}