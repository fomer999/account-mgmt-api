package com.lf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TestHelper {

    public static byte[] toByteArray(InputStream stream) {
        byte[] bytes = new byte[0];

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = stream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();
            bytes = buffer.toByteArray();
        } catch (IOException ioex) {
        }

        return bytes;
    }
}
