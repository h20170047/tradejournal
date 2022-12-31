package com.svj.integrationTests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.svj.dto.PreferenceResponseDTO;
import com.svj.dto.ServiceResponse;
import com.svj.dto.TradeEntryRequestDTO;
import com.svj.dto.TradeSetupResponseDTO;
import com.svj.entity.BlackList;
import com.svj.entity.TraderPreference;
import com.svj.repository.BlackListRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;

import static com.svj.utilities.AppUtils.dateFormatter;
import static com.svj.utilities.JsonParser.objectMapper;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureDataMongo
public class StockScreenerIntegrationTests {
    @LocalServerPort
    private int port;
    private String baseUrl= "http://localhost:";
    private String stockScreenerBaseUrl;
    private static TestRestTemplate restTemplate;
    @Autowired
    private BlackListRepository blackListRepository;

    @BeforeAll
    public static void init(){restTemplate= new TestRestTemplate();}

    @BeforeEach
    public void setUp(){
        stockScreenerBaseUrl = baseUrl.concat(String.valueOf(port)).concat("/analysis");
    }

    @AfterEach
    public void tearDown(){
        blackListRepository.deleteAll();
    }

    @Test
    public void test_getBlackListedStocks(){
        // setup
        String traderName="John";
        BlackList list= BlackList.builder().traderName(traderName).blackListedStocks(new HashSet<>(Arrays.asList("WIPRO"))).build();
        blackListRepository.save(list);
        // check if blacklisted stocks are present in correct section of recommendation list
        ServiceResponse<TradeSetupResponseDTO> stockScreenerServerResponse = restTemplate.getForObject(stockScreenerBaseUrl.concat("/trade-setup/{tradeDate}?traderName={traderName}"), ServiceResponse.class, "2-1-2023", traderName);
        TradeSetupResponseDTO stockRecommendationList= objectMapper.convertValue(stockScreenerServerResponse.getResponse(), new TypeReference<TradeSetupResponseDTO>(){});
        assertThat(stockRecommendationList.getBlackListedStocks().size()).isEqualTo(1);
    }

    @Test
    public void test_getRecommendationWithoutBlackListedStocks(){
        // setup
        String traderName="John";
        // check if stocks are being recommended without a blacklist for this trader
        ServiceResponse<TradeSetupResponseDTO> stockScreenerServerResponse = restTemplate.getForObject(stockScreenerBaseUrl.concat("/trade-setup/{tradeDate}?traderName={traderName}"), ServiceResponse.class, "2-1-2023", traderName);
        TradeSetupResponseDTO stockRecommendationList= objectMapper.convertValue(stockScreenerServerResponse.getResponse(), new TypeReference<TradeSetupResponseDTO>(){});
        assertThat(stockRecommendationList.getBlackListedStocks()).isNull();
    }
}
