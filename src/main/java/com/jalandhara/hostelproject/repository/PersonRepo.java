package com.jalandhara.hostelproject.repository;

import com.jalandhara.hostelproject.entity.Person;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PersonRepo extends JpaRepository<Person, UUID> {

    List<Person> findAllByRoomId(UUID roomId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM person_job WHERE job_id = :jobId", nativeQuery = true)
    void deleteAllJobLinksById(UUID jobId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM person_job ", nativeQuery = true)
    void deleteAllJobLinks();

}
