package com.jalandhara.hostelproject.repository;

import com.jalandhara.hostelproject.entity.Wifi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WifiRepo extends JpaRepository<Wifi, UUID> {
}
