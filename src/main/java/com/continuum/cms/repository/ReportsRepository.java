package com.continuum.cms.repository;

import com.continuum.cms.entity.Report;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReportsRepository extends JpaRepository<Report, UUID> {
    Page<Report> findAll(Specification<Report> searchSpec, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Report r SET r.status = :status, r.fileId = :fileId WHERE r.id = :id")
    void updateReportStatusById(@Param("id") UUID id, @Param("status") String status, @Param("fileId") UUID fileId);

}
