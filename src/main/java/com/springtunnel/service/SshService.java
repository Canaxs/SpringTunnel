package com.springtunnel.service;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.springtunnel.util.SshUtil;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class SshService {


    static {
        JSch.setLogger(new com.jcraft.jsch.Logger() {
            @Override
            public boolean isEnabled(int level) {
                return true;
            }

            @Override
            public void log(int level, String message) {
                System.out.println("JSch log: " + message);
            }
        });
    }


    public void executeCommand(String host, String user, String command) {
        JSch jsch = new JSch();
        try {
            File pemFile = SshUtil.getTempPemFileFromResources("wireguard_openssh.pem");
            jsch.addIdentity(pemFile.getAbsolutePath());

            Session session = jsch.getSession(user, host, 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.connect();

            channel.disconnect();
            session.disconnect();
        } catch (Exception e) {
            throw new RuntimeException("SSH command failed", e);
        }
    }

}
