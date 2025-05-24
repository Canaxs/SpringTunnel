package com.springtunnel.persistence.repository;

import com.springtunnel.persistence.model.WireguardConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WireguardConfigRepository extends JpaRepository<WireguardConfig , Long> {
}
