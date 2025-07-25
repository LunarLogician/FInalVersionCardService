package com.example.Card_Service_V2.repositories;

import com.example.Card_Service_V2.models.CardTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CardTransactionRepository extends JpaRepository<CardTransaction, Integer> {
    List<CardTransaction> findByCardIdAndTimestampBetween(Integer cardId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT SUM(t.amount) FROM CardTransaction t WHERE t.cardId = :cardId AND t.timestamp BETWEEN :start AND :end")
    Double sumAmountByCardIdAndDay(@Param("cardId") Integer cardId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
