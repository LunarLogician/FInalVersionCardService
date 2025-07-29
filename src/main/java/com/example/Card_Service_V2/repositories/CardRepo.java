package com.example.Card_Service_V2.repositories;

import com.example.Card_Service_V2.models.CardModel;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepo extends JpaRepository<CardModel, Integer> {

    @Query("SELECT c FROM CardModel c WHERE c.accountId = ?1")

    List<CardModel> findAllByAccountId(int accountId);

    Optional<CardModel> findByAccountId(int accountId);

    List<CardModel> findByCardstatus(String status);

    List<CardModel> findByUserid(int userId);

   @Query("SELECT c FROM CardModel c WHERE " +
       "(:status IS NULL OR c.cardstatus = :status) AND " +
       "(:type IS NULL OR c.type = :type) AND " +
       "(:userId IS NULL OR c.userid = :userId) AND " +
       "(:accountId IS NULL OR c.accountId = :accountId) AND " +
       "(:network IS NULL OR c.network = :network)")
    Page<CardModel> findWithFiltersPaged(
        @Param("status") String status,
        @Param("type") String type,
        @Param("userId") Integer userId,
        @Param("accountId") Integer accountId,
        @Param("network") String network,
        Pageable pageable);

    @Query("SELECT c FROM CardModel c WHERE " +
       "(:status IS NULL OR c.cardstatus = :status) AND " +
       "(:type IS NULL OR c.type = :type) AND " +
       "(:userId IS NULL OR c.userid = :userId) AND " +
       "(:accountId IS NULL OR c.accountId = :accountId) AND " +
       "(:network IS NULL OR c.network = :network)")
    List<CardModel> findWithFilters(
        @Param("status") String status,
        @Param("type") String type,
        @Param("userId") Integer userId,
        @Param("accountId") Integer accountId,
        @Param("network") String network);

    List<CardModel> findByCardstatusAndType(String status, String type);
    
    List<CardModel> findByUseridAndCardstatus(int userId, String status);
    
    List<CardModel> findByTypeAndCardstatus(String type, String status);

    List<CardModel> findByUseridAndAccountIdAndTypeAndNetwork(int userid, int accountid, String type, String network);

    List<CardModel> findByPlan_Id(Integer planId);
    List<CardModel> findByPlan_IdAndUserid(Integer planId, Integer userId);
}

