package com.hp.bingo.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hp.bingo.entities.Events;

public interface EventsRepository extends JpaRepository<Events, Long> {

    // Custom query methods can be added here if needed
    
}
