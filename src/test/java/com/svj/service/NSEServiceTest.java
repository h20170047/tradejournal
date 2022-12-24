package com.svj.service;

import com.svj.repository.BlackListRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.List;

import static com.svj.utilities.AppUtils.dateFormatter;

class NSEServiceTest {
    @Mock
    private BlackListRepository blackListRepository;
    @Test
    @Disabled
    public void getNSEData(){
        StockScreenerProcessor stocksProcessor= new StockScreenerProcessor("data/",
                "Nifty50List.txt",
                "NSEHolidays.txt",
                3,
                new NSEService("save/", "target/test-classes/data"),
                new BlackListService(blackListRepository, "Nifty50List.txt"));
        NSEService service= new NSEService("save/", "target/test-classes/data");
        LocalDate startDate= LocalDate.parse("13-12-2022", dateFormatter);
        LocalDate endDate= LocalDate.parse("16-12-2022", dateFormatter);
        List<LocalDate> businessDays = stocksProcessor.getBusinessDays(startDate, endDate);
        for(LocalDate day: businessDays){
            service.getBhavCopy(day);
        }
    }

}