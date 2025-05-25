package com.springtunnel.service;

import com.springtunnel.config.WireGuardServerProperties;
import com.springtunnel.persistence.model.WireguardConfig;
import com.springtunnel.persistence.repository.WireguardConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class WireguardService {

    private final WireguardConfigRepository wireguardConfigRepository;
    private final WireGuardServerProperties serverProperties;


    public Resource downloadClientConfig() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        WireguardConfig config = wireguardConfigRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wireguard config not found"));

        StringBuilder confBuilder = new StringBuilder();
        confBuilder.append("[Interface]\n");
        confBuilder.append("PrivateKey = ").append(config.getPrivateKey()).append("\n");
        confBuilder.append("Address = ").append(config.getClientAddress()).append("\n");
        confBuilder.append("DNS = ").append("10.0.0.1").append("\n");
        confBuilder.append("MTU = ").append("1280").append("\n\n");

        confBuilder.append("[Peer]\n");
        confBuilder.append("PublicKey = ").append(serverProperties.getPublicKey()).append("\n");
        confBuilder.append("Endpoint = ").append(serverProperties.getIp()).append(":").append(serverProperties.getEndpointPort()).append("\n");
        confBuilder.append("AllowedIPs = ").append("0.0.0.0/0").append("\n");
        confBuilder.append("PersistentKeepalive  = ").append("25").append("\n");

        byte[] fileContent = confBuilder.toString().getBytes(StandardCharsets.UTF_8);

        return new ByteArrayResource(fileContent);
    }


}
