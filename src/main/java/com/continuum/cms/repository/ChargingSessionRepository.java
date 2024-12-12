package com.continuum.cms.repository;

import com.continuum.cms.entity.ChargingSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

import java.util.List;

public interface ChargingSessionRepository extends JpaRepository<ChargingSession, UUID> {
    Page<ChargingSession> findAll(Specification<ChargingSession> specification, Pageable pageable);

    @Query("SELECT SUM(cs.usedEnergy) FROM ChargingSession cs")
    Integer getTotalUsedEnergy();

    long count();
    List<ChargingSession> findAll(Specification<ChargingSession> specification);

    @Query("SELECT COALESCE(SUM(TIMESTAMPDIFF(HOUR, cs.startAt, cs.stopAt)), 0.0) FROM ChargingSession cs")
    double countTotalUtilization();

    @Query("SELECT cs FROM ChargingSession cs " +
            "LEFT JOIN FETCH cs.vendorCustomer vc " +
            "LEFT JOIN FETCH cs.chargingStation csn " +
            "LEFT JOIN FETCH cs.vendorBankDetails vbd " +
            "LEFT JOIN FETCH cs.carbonCreditDetail ccd")
    List<ChargingSession> findAllWithRelations();

    @Query("SELECT cs FROM ChargingSession cs " +
            "LEFT JOIN FETCH cs.vendorCustomer vc " +
            "LEFT JOIN FETCH cs.chargingStation csn " +
            "LEFT JOIN FETCH cs.vendorBankDetails vbd " +
            "LEFT JOIN FETCH cs.carbonCreditDetail ccd " +
            "WHERE cs.startAt BETWEEN :startAt AND :stopAt")
    List<ChargingSession> findAllWithRelationsBetween(@Param("startAt") LocalDateTime startAt, @Param("stopAt") LocalDateTime stopAt);

    @Query("SELECT cs FROM ChargingSession cs " +
            "LEFT JOIN FETCH cs.vendorCustomer vc " +
            "LEFT JOIN FETCH cs.chargingStation csn " +
            "LEFT JOIN FETCH cs.vendorBankDetails vbd " +
            "LEFT JOIN FETCH cs.carbonCreditDetail ccd " +
            "WHERE vbd.cin IN :vendorCodes")
    List<ChargingSession> findAllWithRelationsByVendorCodes(@Param("vendorCodes") List<String> vendorCodes);

    @Query("SELECT cs FROM ChargingSession cs " +
            "LEFT JOIN FETCH cs.vendorCustomer vc " +
            "LEFT JOIN FETCH cs.chargingStation csn " +
            "LEFT JOIN FETCH cs.vendorBankDetails vbd " +
            "LEFT JOIN FETCH cs.carbonCreditDetail ccd " +
            "WHERE cs.startAt BETWEEN :startAt AND :stopAt " +
            "AND vbd.cin IN :vendorCodes")
    List<ChargingSession> findAllWithRelationsBetweenAndVendorCode(
            @Param("startAt") LocalDateTime startAt,
            @Param("stopAt") LocalDateTime stopAt,
            @Param("vendorCodes") List<String> vendorCodes);

}
