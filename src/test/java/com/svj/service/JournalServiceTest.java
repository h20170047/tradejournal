package com.svj.service;

import com.svj.dto.TradeEntryRequestDTO;
import com.svj.dto.TradeEntryResponseDTO;
import com.svj.entity.TradeEntry;
import com.svj.entity.TradeStats;
import com.svj.entity.TraderPreference;
import com.svj.repository.BlackListRepository;
import com.svj.repository.JournalRepository;
import com.svj.repository.PreferenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.svj.utilities.AppUtils.dateFormatter;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JournalServiceTest {

    @InjectMocks
    private JournalService service;
    @Mock
    private JournalRepository repository;
    @InjectMocks
    private PreferenceService preferenceService;
    @Mock
    private PreferenceRepository preferenceRepository;
    @InjectMocks
    private BlackListService blackListService;
    @Mock
    private BlackListRepository blackListRepository;


    TradeEntryRequestDTO requestDTO;
    com.svj.entity.TradeEntry tradeEntry;
    TraderPreference preference;

    @BeforeEach
    public void setup(){
        service= new JournalService(repository, preferenceService, blackListService);
        requestDTO= TradeEntryRequestDTO.builder()
                .symbol("SYM").traderName("TEST").quantity(10).entryDate(LocalDate.parse("12-03-2000", dateFormatter))
                .entryPrice(100.0).SL(90.0).T1(110.0).build();
        tradeEntry= com.svj.entity.TradeEntry.builder()
                .symbol("SYM").id("ID").traderName("TEST").quantity(10)
                .entryDate(LocalDate.parse("12-03-2000", dateFormatter))
                        .entryPrice(100.0).SL(90.0).T1(110.0).build();
        preference= TraderPreference.builder().id("ID").traderName("TEST").capital(1000.0).product("INTRADAY").position("LONG")
                .build();
    }

    @Test
    void addEntry() {
        when(repository.save(any(com.svj.entity.TradeEntry.class))).thenReturn(tradeEntry);
        when(preferenceRepository.findByTraderName(any(String.class))).thenReturn(preference);
        TradeEntryResponseDTO tradeEntryResponseDTO = service.addEntry(requestDTO);
        assertThat(tradeEntryResponseDTO.getId()).isNotEmpty();
    }

    @Test
    void updateEntry() {
        when(repository.save(any(com.svj.entity.TradeEntry.class))).thenReturn(tradeEntry);
        when(repository.findById(any(String.class))).thenReturn(Optional.of(tradeEntry));
        when(preferenceRepository.findByTraderName(any(String.class))).thenReturn(preference);
        TradeEntryResponseDTO responseDTO = service.updateEntry("id", requestDTO);
        assertThat(responseDTO.getId()).isNotEmpty();
    }

    @Test
    void getEntryByID() {
        when(repository.findById(any(String.class))).thenReturn(Optional.of(tradeEntry));
        TradeEntryResponseDTO responseDTO = service.getEntryByID("id");
        assertThat(responseDTO.getId()).isNotEmpty();
    }

    @Test
    void getAllEntries() {
        List<com.svj.entity.TradeEntry> entities= Arrays.asList(
                com.svj.entity.TradeEntry.builder().id("id-1").traderName("TEST").quantity(10).symbol("SYM").entryDate(LocalDate.parse("12-03-2000", dateFormatter)).build(),
                com.svj.entity.TradeEntry.builder().id("id-2").traderName("TEST").quantity(10).symbol("SYM").entryDate(LocalDate.parse("12-03-2000", dateFormatter)).build()
        );
        when(repository.findAll()).thenReturn(entities);
        List<TradeEntryResponseDTO> entries = service.getAllEntries();
        assertThat(entries.size()).isEqualTo(2);
    }

    @Test
    void deleteEntry() {
        doNothing().when(repository).deleteById(any(String.class));
        when(repository.findById(any(String.class))).thenReturn(Optional.of(tradeEntry));
        when(preferenceRepository.findByTraderName(any(String.class))).thenReturn(preference);
        String response = service.deleteEntry("id-1");
        assertThat(response).contains("id-1");

    }

    @Test
    void getEntriesBetweenDate() {
        LocalDate tradeDay = LocalDate.parse("12-03-2000", dateFormatter);
        List<com.svj.entity.TradeEntry> entries= Arrays.asList(
                com.svj.entity.TradeEntry.builder().id("id-1").symbol("SYM").traderName("TEST").quantity(10).entryDate(LocalDate.parse("12-03-2000", dateFormatter)).build(),
                com.svj.entity.TradeEntry.builder().id("id-2").symbol("SYM").traderName("TEST").quantity(10).entryDate(LocalDate.parse("12-03-2000", dateFormatter)).build()
        );
        when(repository.findEntriesByDate(any(String.class), any(LocalDate.class), any(LocalDate.class))).thenReturn(entries);
        List<TradeEntryResponseDTO> entriesByDate = service.getEntriesBetweenDates("Test", tradeDay, tradeDay.plusDays(1));
        assertThat(entriesByDate.size()).isEqualTo(2);
    }

    @Test
    void getStats(){
        LocalDate fromDate = LocalDate.parse("1-03-2000", dateFormatter);
        LocalDate toDate = LocalDate.parse("12-03-2000", dateFormatter);
        List<TradeEntryResponseDTO> entries= Arrays.asList(
                TradeEntryResponseDTO.builder().id("id-1").symbol("SYM").traderName("TEST").quantity(10).entryDate(LocalDate.parse("12-03-2000", dateFormatter)).entryPrice(100.0).exitPrice(110.0).profit(10.0).profitPercent(2d).build(),
                TradeEntryResponseDTO.builder().id("id-2").symbol("SYM").traderName("TEST").quantity(10).entryDate(LocalDate.parse("12-03-2000", dateFormatter)).entryPrice(95.0).exitPrice(85.0).profit(-10.0).profitPercent(-1d).build()
        );
        TradeStats tradeStats = service.computeStats(entries, fromDate, toDate);
        assertThat(tradeStats.getTotalTrades()).isEqualTo(2);
    }
}