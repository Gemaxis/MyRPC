package com.custom.common.codecs;

import java.io.*;

/**
 * @author Gemaxis
 * @date 2024/07/11 14:51
 **/
public class ObjectSerializer implements Serializer {

    // Java IO 对象->字节数组
    @Override
    public byte[] serialize(Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            // 创建一个对象输出流，用于将对象写入字节数组输出流
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            // 将传入的对象写入对象输出流
            oos.writeObject(obj);
            // 刷新对象输出流，确保所有数据都被写入字节数组输出流
            oos.flush();
            bytes = bos.toByteArray();
            oos.close();
            bos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bytes;
    }


    // 字节数组->Java IO 对象
    @Override
    public Object deserialize(byte[] bytes, int messageType) {
        Object obj = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);

        try {
            ObjectInputStream ois = new ObjectInputStream(bis);
            obj = ois.readObject();
            ois.close();
            ;
            bis.close();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return obj;
    }

    @Override
    public int getType() {
        return 0;
    }
}
