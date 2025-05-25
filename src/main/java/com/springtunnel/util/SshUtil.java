package com.springtunnel.util;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;

import java.io.*;

public class SshUtil {

    public static File getTempPemFileFromResources(String resourcePath) throws IOException {
        InputStream pemStream = SshUtil.class.getClassLoader().getResourceAsStream(resourcePath);
        if (pemStream == null) {
            throw new FileNotFoundException(resourcePath + " not found in resources");
        }

        File tempPemFile = File.createTempFile("wireguard", ".pem");
        tempPemFile.deleteOnExit();

        try (FileOutputStream out = new FileOutputStream(tempPemFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = pemStream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }

        return tempPemFile;
    }

    public static void addIdentityToJSch(JSch jsch, File pemFile) throws JSchException {
        jsch.addIdentity(pemFile.getAbsolutePath());
    }
}
