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


    // ✅ 1) Total registrations
    @Query("SELECT COUNT(e) FROM EntryForm e WHERE e.status = '1'")
    long getTotalRegistrations();

    // ✅ 2) Payment confirmed count
    @Query("SELECT COUNT(e) FROM EntryForm e WHERE e.paymentConfirmed = true AND e.status = '1'")
    long getPaymentConfirmedCount();

    // ✅ 3) Payment pending count
    @Query("SELECT COUNT(e) FROM EntryForm e WHERE e.paymentConfirmed = false AND e.status = '1'")
    long getPaymentPendingCount();

    // ✅ 4) Total amount (payment confirmed only)
    @Query("SELECT COALESCE(SUM(e.amountPaid), 0) FROM EntryForm e WHERE e.paymentConfirmed = true AND e.status = '1'")
    long getTotalConfirmedAmount();
}
