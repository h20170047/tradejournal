package com.svj.service;

import com.svj.entity.stockscreener.CPRWidth;
import com.svj.entity.stockscreener.Stock;
import com.svj.service.technica_analysis.TechnicalIndicators;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TechnicalIndicatorsTest {
    @Test
    public void narrowCPR(){
        TechnicalIndicators indicators= new TechnicalIndicators();
        Stock stock= new Stock("narrowCPR_stock", 100, 110, 90, 90, LocalDateTime.now());
        CPRWidth narrowCPR = indicators.narrowCPR(stock);
        assertThat(narrowCPR.isNarrowCPR()).isTrue();
    }

    @Test
    public void wideCPR(){
        TechnicalIndicators indicators= new TechnicalIndicators();
        Stock stock= new Stock("wideCPR_stock", 100, 150, 60, 90, LocalDateTime.now());
        CPRWidth narrowCPR = indicators.narrowCPR(stock);
        assertThat(narrowCPR.isNarrowCPR()).isFalse();
    }

    @Test
    @Disabled
    public void calcCPR(){
        TechnicalIndicators indicators= new TechnicalIndicators();
        Stock stock= new Stock("unknownStock", 838.05, 840, 822, 822, LocalDateTime.now());
        CPRWidth narrowCPR = indicators.narrowCPR(stock);
        assertThat(narrowCPR.isNarrowCPR()).isFalse();
    }

}