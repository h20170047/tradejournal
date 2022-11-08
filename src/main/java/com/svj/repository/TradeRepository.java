package com.svj.repository;

import com.svj.entity.TradeEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface TradeRepository extends MongoRepository<TradeEntry, String> {
    @Query("{'buy':{$gte: ?0, $lte: ?1} }")
    List<TradeEntry> findEntriesByDate(LocalDateTime from, LocalDateTime to);
}
