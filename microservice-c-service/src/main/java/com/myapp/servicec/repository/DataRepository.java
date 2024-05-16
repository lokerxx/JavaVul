package com.myapp.servicec.repository;

import com.myapp.servicec.entity.DataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DataRepository extends JpaRepository<DataEntity, Long> {
    @Query("SELECT d FROM DataEntity d ORDER BY d.timestamp DESC")
    List<DataEntity> findLatestData();
}