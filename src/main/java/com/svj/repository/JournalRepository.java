package com.svj.repository;

import com.svj.entity.TradeEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface JournalRepository extends MongoRepository<TradeEntry, String> {
    @Query("{'buy':{$gte: ?0, $lte: ?1} }")
    List<TradeEntry> findEntriesByDate(LocalDate from, LocalDate to);
}
