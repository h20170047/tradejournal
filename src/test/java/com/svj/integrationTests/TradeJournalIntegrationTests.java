package com.svj.integrationTests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.svj.dto.PreferenceResponseDTO;
import com.svj.dto.ServiceResponse;
import com.svj.dto.TradeEntryResponseDTO;
import com.svj.entity.TradeEntry;
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
import java.util.Arrays;
import java.util.List;

import static com.svj.utilities.AppUtils.dateFormatter;
import static com.svj.utilities.JsonParser.jsonToString;
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
        TradeEntry entryRequestDTO = TradeEntry.builder().traderName(traderName).symbol("UPL").quantity(1).entryDate(LocalDate.parse("1-03-2000", dateFormatter)).quantity(1).entryPrice(100.0).SL(90.0).T1(110.0).exitPrice(110.0).build();
        ServiceResponse journalServerResponse = restTemplate.postForObject(journalBaseUrl, entryRequestDTO, ServiceResponse.class);
        // check if balance of trader is updated
        ServiceResponse<PreferenceResponseDTO> preferenceServerResponse = restTemplate.getForObject(preferenceBaseUrl.concat("/name/{traderName}"), ServiceResponse.class,traderName);
        PreferenceResponseDTO preferenceResponse= objectMapper.convertValue(preferenceServerResponse.getResponse(), new TypeReference<PreferenceResponseDTO>(){});
        assertThat(preferenceResponse.getCapital()).isEqualTo(3010);
    }

    @Test
    public void incorrectInput(){
        // Add trade entry
        TradeEntry entryRequestDTO = TradeEntry.builder().traderName("John").symbol("TEST").entryDate(LocalDate.parse("1-03-2000", dateFormatter)).entryPrice(100d).SL(90d).T1(110d).exitPrice(110d).build();
        ServiceResponse journalServerResponse = restTemplate.postForObject(journalBaseUrl, entryRequestDTO, ServiceResponse.class);
        // check if right validation happened
        assertTrue(String.valueOf(journalServerResponse.getErrors().get(0)).contains("quantity : must not be null"));
    }

    @Test
    public void test_Stats_NumberOfInsancesAreCorrect(){
        // setup
        List<com.svj.entity.TradeEntry> entries= Arrays.asList(
                com.svj.entity.TradeEntry.builder().traderName("Test").symbol("INFY").quantity(1).entryDate(LocalDate.parse("19-12-2022", dateFormatter)).exitDate(LocalDate.parse("19-12-2022", dateFormatter)).entryPrice(1000d).SL(990d).T1(1010d).build(),
                com.svj.entity.TradeEntry.builder().traderName("OtherTrader1").symbol("INFY").quantity(1).entryDate(LocalDate.parse("20-12-2022", dateFormatter)).exitDate(LocalDate.parse("20-12-2022", dateFormatter)).entryPrice(1000d).SL(990d).T1(1010d).build(),
                com.svj.entity.TradeEntry.builder().traderName("Test").symbol("INFY").quantity(1).entryDate(LocalDate.parse("21-12-2022", dateFormatter)).exitDate(LocalDate.parse("21-12-2022", dateFormatter)).entryPrice(1000d).SL(990d).T1(1010d).build(),
                com.svj.entity.TradeEntry.builder().traderName("Test").symbol("INFY").quantity(1).entryDate(LocalDate.parse("22-12-2022", dateFormatter)).exitDate(LocalDate.parse("22-12-2022", dateFormatter)).entryPrice(1000d).SL(990d).T1(1010d).build(),
                com.svj.entity.TradeEntry.builder().traderName("Test").symbol("INFY").quantity(1).entryDate(LocalDate.parse("19-12-2022", dateFormatter)).exitDate(LocalDate.parse("19-12-2022", dateFormatter)).entryPrice(1000d).SL(990d).T1(1010d).build(),
                com.svj.entity.TradeEntry.builder().traderName("OtherTrader2").symbol("INFY").quantity(1).entryDate(LocalDate.parse("19-12-2022", dateFormatter)).exitDate(LocalDate.parse("19-12-2022", dateFormatter)).entryPrice(1000d).SL(990d).T1(1010d).build(),
                com.svj.entity.TradeEntry.builder().traderName("Test").symbol("INFY").quantity(1).entryDate(LocalDate.parse("23-12-2022", dateFormatter)).exitDate(LocalDate.parse("23-12-2022", dateFormatter)).entryPrice(1000d).SL(990d).T1(1010d).build()
            );
        journalRepo.saveAll(entries);
        // check if number of entries is correct for trader within date range
        ServiceResponse<List<TradeEntryResponseDTO>> journalServerResponse = restTemplate.getForObject(journalBaseUrl.concat("/entries/{fromDate}/{toDate}?traderName={traderName}"), ServiceResponse.class, "19-12-2022", "23-12-2022", "Test");
        List<TradeEntryResponseDTO>  userEntries= objectMapper.convertValue(journalServerResponse.getResponse(), new TypeReference<List<TradeEntryResponseDTO>>(){});
        assertThat(userEntries.size()).isEqualTo(5);
    }

    @Test
    @Disabled
    public void test_Stats_CorrectEntriesForMonth(){
        // setup
        List<com.svj.entity.TradeEntry> entries= Arrays.asList(
                com.svj.entity.TradeEntry.builder().traderName("Test").symbol("INFY").quantity(1).entryDate(LocalDate.parse("19-10-2022", dateFormatter)).exitDate(LocalDate.parse("19-10-2022", dateFormatter)).entryPrice(1000d).SL(990d).T1(1010d).build(),
                com.svj.entity.TradeEntry.builder().traderName("OtherTrader1").symbol("INFY").quantity(1).entryDate(LocalDate.parse("20-12-2022", dateFormatter)).exitDate(LocalDate.parse("20-12-2022", dateFormatter)).entryPrice(1000d).SL(990d).T1(1010d).build(),
                com.svj.entity.TradeEntry.builder().traderName("Test").symbol("INFY").quantity(1).entryDate(LocalDate.parse("21-12-2022", dateFormatter)).exitDate(LocalDate.parse("21-12-2022", dateFormatter)).entryPrice(1000d).SL(990d).T1(1010d).build(),
                com.svj.entity.TradeEntry.builder().traderName("Test").symbol("INFY").quantity(1).entryDate(LocalDate.parse("22-12-2022", dateFormatter)).exitDate(LocalDate.parse("22-12-2022", dateFormatter)).entryPrice(1000d).SL(990d).T1(1010d).build(),
                com.svj.entity.TradeEntry.builder().traderName("Test").symbol("INFY").quantity(1).entryDate(LocalDate.parse("19-12-2022", dateFormatter)).exitDate(LocalDate.parse("19-12-2022", dateFormatter)).entryPrice(1000d).SL(990d).T1(1010d).build(),
                com.svj.entity.TradeEntry.builder().traderName("OtherTrader2").symbol("INFY").quantity(1).entryDate(LocalDate.parse("19-12-2022", dateFormatter)).exitDate(LocalDate.parse("19-12-2022", dateFormatter)).entryPrice(1000d).SL(990d).T1(1010d).build(),
                com.svj.entity.TradeEntry.builder().traderName("Test").symbol("INFY").quantity(1).entryDate(LocalDate.parse("23-12-2022", dateFormatter)).exitDate(LocalDate.parse("23-12-2022", dateFormatter)).entryPrice(1000d).SL(990d).T1(1010d).build()
        );
        journalRepo.saveAll(entries);
        // check if number of entries is correct for trader within date range
        ServiceResponse<List<TradeEntryResponseDTO>> journalServerResponse = restTemplate.getForObject(journalBaseUrl.concat("/entries/{fromDate}/{toDate}?traderName={traderName}"), ServiceResponse.class, "19-12-2022", "23-12-2022", "Test");
        List<TradeEntryResponseDTO>  userEntries= objectMapper.convertValue(journalServerResponse.getResponse(), new TypeReference<List<TradeEntryResponseDTO>>(){});
        assertThat(userEntries.size()).isEqualTo(4);
    }

    @Test
    @Disabled
    public void test_Stats_CorrectEntriesForWeek(){
        // setup
        List<com.svj.entity.TradeEntry> entries= Arrays.asList(
                com.svj.entity.TradeEntry.builder().traderName("Test").symbol("INFY").quantity(1).entryDate(LocalDate.parse("19-10-2022", dateFormatter)).exitDate(LocalDate.parse("19-10-2022", dateFormatter)).entryPrice(1000d).SL(990d).T1(1010d).build(),
                com.svj.entity.TradeEntry.builder().traderName("OtherTrader1").symbol("INFY").quantity(1).entryDate(LocalDate.parse("20-12-2022", dateFormatter)).exitDate(LocalDate.parse("20-12-2022", dateFormatter)).entryPrice(1000d).SL(990d).T1(1010d).build(),
                com.svj.entity.TradeEntry.builder().traderName("Test").symbol("INFY").quantity(1).entryDate(LocalDate.parse("16-12-2022", dateFormatter)).exitDate(LocalDate.parse("16-12-2022", dateFormatter)).entryPrice(1000d).SL(990d).T1(1010d).build(),
                com.svj.entity.TradeEntry.builder().traderName("Test").symbol("INFY").quantity(1).entryDate(LocalDate.parse("22-12-2022", dateFormatter)).exitDate(LocalDate.parse("22-12-2022", dateFormatter)).entryPrice(1000d).SL(990d).T1(1010d).build(),
                com.svj.entity.TradeEntry.builder().traderName("Test").symbol("INFY").quantity(1).entryDate(LocalDate.parse("19-12-2022", dateFormatter)).exitDate(LocalDate.parse("19-12-2022", dateFormatter)).entryPrice(1000d).SL(990d).T1(1010d).build(),
                com.svj.entity.TradeEntry.builder().traderName("OtherTrader2").symbol("INFY").quantity(1).entryDate(LocalDate.parse("19-12-2022", dateFormatter)).exitDate(LocalDate.parse("19-12-2022", dateFormatter)).entryPrice(1000d).SL(990d).T1(1010d).build(),
                com.svj.entity.TradeEntry.builder().traderName("Test").symbol("INFY").quantity(1).entryDate(LocalDate.parse("23-12-2022", dateFormatter)).exitDate(LocalDate.parse("23-12-2022", dateFormatter)).entryPrice(1000d).SL(990d).T1(1010d).build()
        );
        journalRepo.saveAll(entries);
        // check if number of entries is correct for trader within date range
        ServiceResponse<List<TradeEntryResponseDTO>> journalServerResponse = restTemplate.getForObject(journalBaseUrl.concat("/entries/{fromDate}/{toDate}?traderName={traderName}"), ServiceResponse.class, "19-12-2022", "23-12-2022", "Test");
        List<TradeEntryResponseDTO>  userEntries= objectMapper.convertValue(journalServerResponse.getResponse(), new TypeReference<List<TradeEntryResponseDTO>>(){});
        assertThat(userEntries.size()).isEqualTo(3);
    }

    @Test
    @Disabled
    public void test_AddAndRetrieveAllEntries(){
        // setup
        // check if number of added entries is correct
        ServiceResponse<List<TradeEntryResponseDTO>> journalServerResponse = restTemplate.postForObject(journalBaseUrl.concat("/bulk-add"), null, ServiceResponse.class);
        List<TradeEntryResponseDTO>  userEntries= objectMapper.convertValue(journalServerResponse.getResponse(), new TypeReference<List<TradeEntryResponseDTO>>(){});
//        assertThat(userEntries.size()).isEqualTo(116);
        System.out.println(String.format("Preference of trader at end of bulk add is %s", jsonToString(prefRepo.findByTraderName("Swaraj"))));
//        assertThat(userEntries.stream().filter(entry-> entry.getProfit()>= 0).count()).isEqualTo(70);
        assertThat(userEntries.stream().filter(entry-> entry.getProfit()>= 0).count()).isEqualTo(10);
    }
}
