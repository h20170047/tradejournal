package com.svj.integrationTests;

import com.svj.entity.TradeEntry;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface JournalTestH2Repository extends MongoRepository<TradeEntry, String> {
}
