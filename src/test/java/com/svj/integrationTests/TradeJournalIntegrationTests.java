package com.svj.integrationTests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.svj.dto.PreferenceResponseDTO;
import com.svj.dto.ServiceResponse;
import com.svj.dto.TradeEntryRequestDTO;
import com.svj.entity.TraderPreference;
import com.svj.repository.JournalRepository;
import com.svj.repository.PreferenceRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.LocalDate;

import static com.svj.utilities.AppUtils.dateFormatter;
import static com.svj.utilities.JsonParser.objectMapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureDataMongo
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

    @BeforeAll
    public static void init(){restTemplate= new TestRestTemplate();}

    @BeforeEach
    public void setUp(){
        journalBaseUrl= baseUrl.concat(String.valueOf(port)).concat("/trade-journal");
        preferenceBaseUrl= baseUrl.concat(String.valueOf(port)).concat("/preference");
    }

    @AfterEach
    public void tearDown(){
        prefRepo.deleteAll();
        journalRepo.deleteAll();
    }

    @Test
    public void testBalanceCorrectness_OnSave(){
        // setup
        String traderName="John";
        TraderPreference trader= TraderPreference.builder().id("1").traderName(traderName).capital(3000.0).product("INTRADAY").position("LONG").build();
        prefRepo.save(trader);
        // Add trade entry
        TradeEntryRequestDTO entryRequestDTO = TradeEntryRequestDTO.builder().traderName(traderName).symbol("TEST").quantity(1).entryDate(LocalDate.parse("1-03-2000", dateFormatter)).quantity(1).entryPrice(100.0).SL(90.0).T1(110.0).exitPrice(110.0).build();
        ServiceResponse journalServerResponse = restTemplate.postForObject(journalBaseUrl, entryRequestDTO, ServiceResponse.class);
        // check if balance of trader is updated
        ServiceResponse<PreferenceResponseDTO> preferenceServerResponse = restTemplate.getForObject(preferenceBaseUrl.concat("/name/{traderName}"), ServiceResponse.class,traderName);
        PreferenceResponseDTO preferenceResponse= objectMapper.convertValue(preferenceServerResponse.getResponse(), new TypeReference<PreferenceResponseDTO>(){});
        assertThat(preferenceResponse.getCapital()).isEqualTo(3010);

    }
    @Test
    public void incorrectInput(){
        // Add trade entry
        TradeEntryRequestDTO entryRequestDTO = TradeEntryRequestDTO.builder().traderName("John").symbol("TEST").entryDate(LocalDate.parse("1-03-2000", dateFormatter)).entryPrice(100d).SL(90d).T1(110d).exitPrice(110d).build();
        ServiceResponse journalServerResponse = restTemplate.postForObject(journalBaseUrl, entryRequestDTO, ServiceResponse.class);
        // check if right validation happened
        assertTrue(String.valueOf(journalServerResponse.getErrors().get(0)).contains("quantity : must not be null"));
    }
}
