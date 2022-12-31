package com.svj.repository;

import com.svj.entity.TradeEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface JournalRepository extends MongoRepository<TradeEntry, String> {
    @Query(value = "{'entryDate': {$gte: ?1, $lte: ?2} , 'traderName': ?0 }")
    List<TradeEntry> findEntriesByDate(String traderName, LocalDate from, LocalDate to);
}
