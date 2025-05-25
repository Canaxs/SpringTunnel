package com.springtunnel.controller;

import com.springtunnel.service.WireguardService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/wg")
public class WireguardController {

    private final WireguardService wireguardService;

    @GetMapping("/downloadConf")
    public ResponseEntity<Resource> downloadClientConfig() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=client.conf")
                .body(wireguardService.downloadClientConfig());
    }
}
