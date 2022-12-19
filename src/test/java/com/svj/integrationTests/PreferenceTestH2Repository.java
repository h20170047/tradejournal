package com.svj.integrationTests;

import com.svj.entity.TraderPreference;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PreferenceTestH2Repository extends MongoRepository<TraderPreference, String> {
}
