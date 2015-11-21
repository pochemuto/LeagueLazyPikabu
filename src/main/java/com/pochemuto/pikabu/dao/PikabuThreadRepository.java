package com.pochemuto.pikabu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * @author Alexander Kramarev (pochemuto@gmail.com)
 * @date 17.11.2015
 */
@Repository
public interface PikabuThreadRepository extends JpaRepository<PikabuThread, Long> {
    List<PikabuThread> findByIdIn(Collection<Long> ids);
}
