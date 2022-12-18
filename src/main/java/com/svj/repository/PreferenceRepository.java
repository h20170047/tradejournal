package com.svj.repository;

import com.svj.entity.TraderPreference;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PreferenceRepository extends MongoRepository<TraderPreference, String> {

    public TraderPreference findByTraderName(String traderName);
}
