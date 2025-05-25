package com.springtunnel.controller;

import com.springtunnel.dto.WireguardDto;
import com.springtunnel.service.TunnelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/tunnel")
public class TunnelController {

    private final TunnelService tunnelService;

    @GetMapping("/registerClient")
    public ResponseEntity<WireguardDto> registerClient() {
        return ResponseEntity.ok(tunnelService.registerClient());
    }
}
