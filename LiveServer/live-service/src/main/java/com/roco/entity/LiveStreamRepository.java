package com.roco.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LiveStreamRepository extends JpaRepository<LiveStream, Long> {
    Optional<LiveStream> findByStreamKey(String streamKey);

    List<LiveStream> findByStatus(Integer status);

    List<LiveStream> findByUserId(Integer userId);

    @Query("SELECT ls FROM LiveStream ls WHERE ls.userId = :userId AND ls.status = 1")
    List<LiveStream> findActiveStreamsByUserId(@Param("userId") Integer userId);
}