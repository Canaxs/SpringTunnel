package com.springtunnel.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WireguardDto {
    private String username;
    private String privateKey;
    private String publicKey;
    private String clientAddress;
}
