package com.svj.integrationTests;

import com.svj.dto.PreferenceResponseDTO;
import com.svj.dto.ServiceResponse;
import com.svj.dto.TradeEntryRequestDTO;
import com.svj.entity.TraderPreference;
import com.svj.repository.JournalRepository;
import com.svj.repository.PreferenceRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;

import static com.svj.utilities.AppUtils.dateFormatter;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TradeJournalIntegrationTests {

    @LocalServerPort
    private int port;
    private String baseUrl= "http://localhost:";
    private String journalBaseUrl;
    private String preferenceBaseUrl;
    private static TestRestTemplate restTemplate;
    @Autowired
    private JournalRepository journalRepo;
    @Autowired
    private PreferenceRepository prefRepo;
    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeAll
    public static void init(){restTemplate= new TestRestTemplate();}

    @BeforeEach
    public void setUp(){
        journalBaseUrl= baseUrl.concat(String.valueOf(port)).concat("/trade-journal");
        preferenceBaseUrl= baseUrl.concat(String.valueOf(port)).concat("/preference");
    }

    @AfterEach
    public void tearDown(){
        mongoTemplate.getDb().drop();
    }

    // TODO- Once a trader is registered, check if journal entries save and updates gives the expected balance
    @Test
//    @Sql(statements = "INSERT INTO TBL_TraderPreference (id, trader_Name, capital, product, position) VALUES (1, 'John', 3000, 'INTRADAY', 'LONG')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//    @Sql(statements = "DELETE FROM TBL_TraderPreference WHERE trader_Name='John')", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
//    @Sql(statements = "DELETE FROM TBL_TradeEntries WHERE trader_Name='John')", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testBalanceCorrectness_OnSave(){
        TraderPreference trader= TraderPreference.builder().id("1").traderName("John").capital(3000.0).product("INTRADAY").position("LONG").build();
        mongoTemplate.insert(trader);
        // Add an entry
        String traderName="John";
        TradeEntryRequestDTO entryRequestDTO = TradeEntryRequestDTO.builder().traderName(traderName).symbol("TEST").quantity(1).entryDate(LocalDate.parse("1-03-2000", dateFormatter)).entryPrice(100.0).SL(90).T1(110).exitPrice(110.0).build();
        ServiceResponse journalServerResponse = restTemplate.postForObject(journalBaseUrl, entryRequestDTO, ServiceResponse.class);
        ServiceResponse<PreferenceResponseDTO> preferenceServerResponse = restTemplate.getForObject(preferenceBaseUrl.concat("/name/{traderName}"), ServiceResponse.class,traderName);
        assertThat(preferenceServerResponse.getResponse().getCapital()).isEqualTo(3010);

    }
}
