package com.lf.util;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Serialization utilites for X-Security-Context Header values
 */
public class OauthSerializerUtils {
    public byte[] serialize(Object obj) {
            try (ByteArrayOutputStream b = new ByteArrayOutputStream()) {
                try (ObjectOutputStream o = new ObjectOutputStream(b)) {
                    o.writeObject(obj);
                }
                return b.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }

    public Object deserialize(byte[] bytes) {
        try (ByteArrayInputStream b = new ByteArrayInputStream(bytes)) {
            try (ObjectInputStream o = new ObjectInputStream(b)) {
                return o.readObject();
            }
        } catch(ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] base64EncodeBytes(byte[] value) {
        return Base64.getEncoder().encode(value);
    }

    public byte[] base64EncodeString(String value) {
        return Base64.getEncoder().encode(value.getBytes());
    }

    public byte[] base64DecodeString(String value) {
        return Base64.getDecoder().decode(value.getBytes());
    }

    public byte[] base64EncodeList(List<?> someList) {
        byte[] hasRolesBytes = null;
        hasRolesBytes = serialize(someList);
        return Base64.getEncoder().encode(hasRolesBytes);
    }

    public byte[] base64DecodeBytes(byte[] value) {
        return Base64.getDecoder().decode(value);
    }

    public String base64DecodeString(byte[] bytes) {
        return new String(Base64.getDecoder().decode(bytes));
    }

    public List<String> base64DecodeList(byte[] list) {
        byte[] decodedByteList = null;
        List<String> listOfUserRoles = new ArrayList<>();
        decodedByteList = Base64.getDecoder().decode(list);
        listOfUserRoles = (List<String>) deserialize(decodedByteList);
        return listOfUserRoles;
    }
}
