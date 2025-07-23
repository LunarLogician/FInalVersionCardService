package com.example.Card_Service_V2.repositories;

import com.example.Card_Service_V2.models.CardSensitiveData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardSensitiveDataRepository extends JpaRepository<CardSensitiveData, Integer> {
}
