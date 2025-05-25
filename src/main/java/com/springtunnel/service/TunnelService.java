package com.springtunnel.service;

import com.springtunnel.config.WireGuardServerProperties;
import com.springtunnel.dto.WireguardDto;
import com.springtunnel.persistence.model.WireguardConfig;
import com.springtunnel.persistence.repository.UserRepository;
import com.springtunnel.persistence.repository.WireguardConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TunnelService {

    private final UserRepository userRepository;

    private final WireguardConfigRepository wireguardConfigRepository;

    private final SshService sshService;

    private final WireGuardServerProperties serverProperties;

    public WireguardDto registerClient() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (wireguardConfigRepository.existsByUsername(username)) {
            throw new RuntimeException("User already has an existing WireGuard configuration");
        }

        try {
            String[] commandPrivate = {"wsl", "-d", "Ubuntu", "--", "wg", "genkey"};
            Process genPrivate = Runtime.getRuntime().exec(commandPrivate);
            BufferedReader privateReader = new BufferedReader(new InputStreamReader(genPrivate.getInputStream()));
            String privateKey = privateReader.readLine();
            genPrivate.waitFor();

            String[] commandPublic = {"wsl", "-d", "Ubuntu", "--", "sh", "-c", "echo " + privateKey + " | wg pubkey"};
            Process genPublic = Runtime.getRuntime().exec(commandPublic);
            BufferedReader publicReader = new BufferedReader(new InputStreamReader(genPublic.getInputStream()));
            String publicKey = publicReader.readLine();
            genPublic.waitFor();

            String clientIP = generateClientIP();

            String serverIp = serverProperties.getIp();
            String serverUser = serverProperties.getUser();
            String wgSetCommand = String.format("sudo wg set wg0 peer %s allowed-ips %s/32", publicKey, clientIP);
            sshService.executeCommand(serverIp, serverUser, wgSetCommand);

            WireguardConfig config = WireguardConfig.builder()
                    .user(userRepository.findByUsername(username).get())
                    .privateKey(privateKey)
                    .publicKey(publicKey)
                    .clientAddress(clientIP)
                    .createdAt(LocalDateTime.now())
                    .build();

            wireguardConfigRepository.save(config);

            return WireguardDto.builder()
                    .username(username)
                    .privateKey(privateKey)
                    .publicKey(publicKey)
                    .clientAddress(clientIP)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Key generation or SSH execution failed", e);
        }

    }

    private String generateClientIP() {
        List<String> usedIps = wireguardConfigRepository.getAllByClientAddress();

        for (int i = 2; i < 255; i++) {
            String ip = "10.0.0." + i;
            if (!usedIps.contains(ip)) {
                return ip;
            }
        }

        throw new RuntimeException("No available IP addresses left");
    }


}
