package com.svj.service;

import com.svj.dto.TradeSetupResponseDTO;
import com.svj.exceptionHandling.StockProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static com.svj.utilities.AppUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StocksProcessorTest {

    private StockScreenerProcessor service;

    @BeforeEach
    public void setup(){
        service = new StockScreenerProcessor("data/",
                "Nifty50List.txt",
                "NSEHolidays.txt",
                3,
                new NSEService("save/", "target/test-classes/data"));
    }

    @Test
    public void getStocksList_BullsAndBears(){
        TradeSetupResponseDTO result = service.getStocksList(LocalDate.parse("26-11-2022", dateFormatter));
        System.out.println(result);
        assertThat(result.getBullish()).isNotEmpty();
        assertThat(result.getBearish()).isNotEmpty();
    }

    @Test
    @Disabled
    public void getStocksList_getNextTradeSetup(){
        TradeSetupResponseDTO result = service.getStocksList(LocalDate.parse("16-12-2022", dateFormatter));
        System.out.println(result);
    }

    @Test
    @Disabled // wont fail as necessary files will be fetched from NSE before processing
    public void getStocksList_MissingNecessaryData(){
        assertThrows(StockProcessingException.class, ()-> service.getStocksList(LocalDate.parse("1-12-2022", dateFormatter)));
    }

    @Test
    public void getBusinessDays(){
        LocalDate startDate= LocalDate.parse("08-11-2022", dateFormatter);
        LocalDate endDate= LocalDate.parse("15-11-2022", dateFormatter);
        List<LocalDate> businessDays= service.getBusinessDays(startDate, endDate);
        assertThat(businessDays.size()).isEqualTo(4);
    }

    @Test
    public void getBusinessDays_invalidParameters(){
        LocalDate startDate= LocalDate.parse("8-11-2022", dateFormatter);
        LocalDate endDate= LocalDate.parse("7-11-2022", dateFormatter);
        List<LocalDate> businessDays= service.getBusinessDays(startDate, endDate);
        assertThat(businessDays).isNull();
    }

    @Test
    public void getHolidays(){
        List<LocalDate> holidays = service.getHolidayList(service.getHolidayFilePath());
        assertThat(holidays.size()).isEqualTo(346);
    }


    @Test
    public void getNiftyStocks(){
        List<String> holidays = getResourceFileAsStringList(service.getNiftyFilePath());
        assertThat(holidays.size()).isEqualTo(50);
    }

    @Test
    public void getFileNameFromDate_PrefixZero(){
        String fileName = getFileNameFromDate(LocalDate.parse("9-11-2022", dateFormatter));
        assertThat(fileName).isEqualTo("cm09NOV2022bhav.csv");
    }

    @Test
    public void getFileNameFromDate_normal(){
        String fileName = getFileNameFromDate(LocalDate.parse("19-4-2022", dateFormatter));
        assertThat(fileName).isEqualTo("cm19APR2022bhav.csv");
    }
}