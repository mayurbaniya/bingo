package com.hp.bingo.repo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hp.bingo.entities.EntryForm;

public interface EntryFormRepository extends JpaRepository<EntryForm, Long> {
    boolean existsByPhoneOrEmail(String phone, String email);
    long countByPhoneOrEmail(String phone, String email);

    boolean existsByRegistrationId(String registrationId);

    Page<EntryForm> findByPaymentConfirmedAndStatus(boolean paymentConfirmed, Pageable pageable, String status);
    Page<EntryForm> findAllByStatus(Pageable pageable, String status);

    Optional<EntryForm> findByRegistrationId(String registrationId);
    @Query("SELECT e FROM EntryForm e WHERE " +
            "LOWER(e.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(e.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(e.phone) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<EntryForm> search(@Param("searchTerm") String searchTerm, Pageable pageable);
}
