package com.svj.repository;

import com.svj.entity.BlackList;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BlackListRepository extends MongoRepository<BlackList, String> {

    public BlackList findByTraderName(String traderName);
}
