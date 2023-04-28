package com.lf.model;


import com.lf.util.OauthSerializerUtils;

import java.io.*;
import java.util.List;

/**
 * Used to marshall and demarshall the SecurityContext
 */
public class SecurityContext implements Serializable {
    private final static long serialVersionUID = 1;

    byte[] context;
    private List<String> hasRoles;
    private String userId, accountId, name;

    /**
     * Get the User id
     * @return String of userId.
     */
    public String getUserId() {
        return this.userId;
    }

    /**
     * Get the users accountId
     * @return String of accountId.
     */
    public String getAccountId() {
        return this.accountId;
    }

    /**
     * Get the users name
     * @return String of name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Allows a client to get the X-Dlvr-Security-Header
     * @return byte[], base64Encoded header of the SecurityContextClass.
     */
    public String getSecurityContextHeader() {
        return new String(this.context);
    }

    private void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    private void setUserId(String userId) {
        this.userId = userId;
    }

    private void setHasRoles(List<String> hasRoles) {
        this.hasRoles = hasRoles;
    }

    private void setContext(byte[] context) {
        this.context = context;
    }

    private void setName(String name) { this.name = name; }

    /**
     * Search for the role in the list.
     * @param role
     * @return (True, False) if the role is present or not.
     */
    public Boolean hasRole(String role) {
        return hasRoles.stream()
                .anyMatch(roles -> roles.equals(role));
    }

    /**
     * Allows a client to create a byte header of X-Dlvr-Security-Header
     * @param userId
     * @param accountId
     * @param hasRoles
     * @return byte[], base64Encoded header of the SecurityContextClass.
     */
    private byte[] createByteHeader(final String userId, final String accountId, final List<String> hasRoles, final String name) {
        // Create a dummy security context object, this does not set the values in the actual SC object explicitly.
        SecurityContext dummyContext = new SecurityContext();
        dummyContext.setUserId(userId);
        dummyContext.setHasRoles(hasRoles);
        dummyContext.setAccountId(accountId);
        dummyContext.setName(name);
        dummyContext.validateState();
        return dummyContext.buildHeader(dummyContext);
    }

    /**
     * Helper method to build the byte header
     * @param obj
     * @return byte[] of the X-Security-Context header.
     */
    private byte[] buildHeader(Object obj) {
        ObjectOutput out = null;
        byte[] toByteHeader = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(obj);
            out.flush();
            toByteHeader = bos.toByteArray();
        } catch(IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
        }
        return new OauthSerializerUtils().base64EncodeBytes(toByteHeader);
    }

    /**
     * Allows a client to build the security context from the Http header response.
     * @param xSecurityContext
     * @return SecurityContext Object
     */
    private void buildContext(final String xSecurityContext) {
        ObjectInputStream in = null;
        SecurityContext context = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(new OauthSerializerUtils().base64DecodeBytes(xSecurityContext.getBytes()));
        try {
            in = new ObjectInputStream(bis);
            Object o = in.readObject();
            context = (SecurityContext) o;
        } catch(IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        setUserId(context.userId);
        setHasRoles(context.hasRoles);
        setAccountId(context.accountId);
        setName(context.name);
        setContext(xSecurityContext.getBytes());
    }

    /**
     * Validation Utilities
     */
    private void validateUserId() {
        if(this.userId == null || this.userId.isEmpty()) {
            throw new IllegalStateException("The security context must contain the userId");
        }
    }

    private void validateAccountId() {
        if(this.accountId == null || this.accountId.isEmpty()) {
            throw new IllegalStateException("The security context must contain the accountId");
        }
    }

    private void validateHasRoles() {
        if(this.hasRoles == null || this.hasRoles.isEmpty()) {
            throw new IllegalStateException("The security context must contain the roles hasRoles");
        }
    }

    private void validateContext() {
        if(this.context == null) {
            throw new IllegalStateException("The security context must contain the context byte stream");
        }
    }

    private void validateName() {
        if(this.name == null || this.name.isEmpty()) {
            throw new IllegalStateException("The security context must contain the name");
        }
    }

    /**
     * Helper used to validate the internal state of the object so its not tampered with on disk. validateState
     * is called when readObject is implicitly called.
     */
    private void validateState() {
        validateUserId();
        validateHasRoles();
        validateAccountId();
        validateName();
    }

    /**
     * Called implicitly when overriding Serializable used in serialization/deserialization of an object.
     * @param inputStream
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private void readObject(ObjectInputStream inputStream) throws ClassNotFoundException, IOException {
        OauthSerializerUtils serializerUtils = new OauthSerializerUtils();
        this.userId = serializerUtils.base64DecodeString((byte[]) inputStream.readObject());
        this.hasRoles =  serializerUtils.base64DecodeList((byte[]) inputStream.readObject());
        this.accountId = serializerUtils.base64DecodeString((byte[]) inputStream.readObject());
        this.name = serializerUtils.base64DecodeString((byte[]) inputStream.readObject());
        // Make sure the class has not been tampered with maliciously
        validateState();
    }

    /**
     * Called implicitly when overriding Serializable used in serialization/deserialization of an object.
     * @param outputStream
     */
    private void writeObject(ObjectOutputStream outputStream) {
        OauthSerializerUtils serializerUtils = new OauthSerializerUtils();
        try {
            outputStream.writeObject(serializerUtils.base64EncodeString(this.userId));
            outputStream.writeObject(serializerUtils.base64EncodeList(this.hasRoles));
            outputStream.writeObject(serializerUtils.base64EncodeString(this.accountId));
            outputStream.writeObject(serializerUtils.base64EncodeString(this.name));
        } catch(IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * The SC constructor is passed the X-Security-Context header, this head is a base 64 encoded string of the SC
     * object. The context is built via the buildContext() method which handles setting the values in the SC object based
     * @param xSecurityHeader
     */
    public SecurityContext(String xSecurityHeader) {
        buildContext(xSecurityHeader);
    }

    /**
     * The private constructor is used to create a dummy SC for the initial creation of the X-Dlvr-Security Header.
     */
    private SecurityContext() {}
}
