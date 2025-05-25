package com.springtunnel.persistence.repository;

import com.springtunnel.persistence.model.User;
import com.springtunnel.persistence.model.WireguardConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WireguardConfigRepository extends JpaRepository<WireguardConfig , Long> {

    @Query("FROM WireguardConfig w WHERE w.user.username = :username")
    Optional<WireguardConfig> findByUsername(@Param("username") String username);

    @Query("SELECT CASE WHEN COUNT(w) > 0 THEN TRUE ELSE FALSE END FROM WireguardConfig w WHERE w.user.username = :username")
    Boolean existsByUsername(@Param("username") String username);

    @Query("SELECT w.clientAddress FROM WireguardConfig w ")
    List<String> getAllByClientAddress();
}
