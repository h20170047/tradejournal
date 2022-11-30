package com.svj.service;

import com.svj.dto.TradeEntryRequestDTO;
import com.svj.dto.TradeEntryResponseDTO;
import com.svj.entity.TradeEntry;
import com.svj.repository.TradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TradeServiceTest {

    @InjectMocks
    private TradeService service;
    @Mock
    private TradeRepository repository;

    public DateTimeFormatter dateTimeFormatter= DateTimeFormatter.ofPattern("dd-MM-yyyy[ [HH][:mm][:ss][.SSS]]");
    TradeEntryRequestDTO requestDTO;
    TradeEntry tradeEntry;

    @BeforeEach
    public void setup(){
        service= new TradeService(repository);
        requestDTO= new TradeEntryRequestDTO("SYM", LocalDateTime.parse("12-03-2000 10:00", dateTimeFormatter), LocalDateTime.parse("12-03-2000 10:30", dateTimeFormatter), 9, 11, 1, 10, 11, "Testing add fn");
        tradeEntry= new TradeEntry("id","SYM", LocalDateTime.parse("12-03-2000 10:00", dateTimeFormatter), LocalDateTime.parse("12-03-2000 10:30", dateTimeFormatter), 9, 11, 1, 10, 11, "Testing add fn");
    }

    @Test
    void addEntry() {
        when(repository.save(any(TradeEntry.class))).thenReturn(tradeEntry);
        TradeEntryResponseDTO tradeEntryResponseDTO = service.addEntry(requestDTO);
        assertThat(tradeEntryResponseDTO.getId()).isNotEmpty();
    }

    @Test
    void updateEntry() {
        when(repository.save(any(TradeEntry.class))).thenReturn(tradeEntry);
        when(repository.findById(any(String.class))).thenReturn(Optional.of(tradeEntry));
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
        List<TradeEntry> entities= Arrays.asList(
                new TradeEntry("id-1","SYM", LocalDateTime.parse("12-03-2000 10:00", dateTimeFormatter), LocalDateTime.parse("12-03-2000 10:30", dateTimeFormatter), 9, 11, 1, 10, 11, "Testing add fn"),
                new TradeEntry("id-2","SYM", LocalDateTime.parse("12-03-2000 10:00", dateTimeFormatter), LocalDateTime.parse("12-03-2000 10:30", dateTimeFormatter), 9, 11, 1, 10, 11, "Testing add fn")
        );
        when(repository.findAll()).thenReturn(entities);
        List<TradeEntryResponseDTO> entries = service.getAllEntries();
        assertThat(entries.size()).isEqualTo(2);
    }

    @Test
    void deleteEntry() {
        doNothing().when(repository).deleteById(any(String.class));
        String response = service.deleteEntry("id-1");
        assertThat(response).contains("id-1");

    }

    @Test
    void getEntriesByDate() {
        LocalDateTime tradeDay = LocalDateTime.parse("12-03-2000 10:00", dateTimeFormatter);
        List<TradeEntry> entries= Arrays.asList(
                new TradeEntry("id-1","SYM", LocalDateTime.parse("12-03-2000 10:00", dateTimeFormatter), LocalDateTime.parse("12-03-2000 10:30", dateTimeFormatter), 9, 11, 1, 10, 11, "Testing add fn"),
                new TradeEntry("id-2","SYM", LocalDateTime.parse("12-03-2000 10:00", dateTimeFormatter), LocalDateTime.parse("12-03-2000 10:30", dateTimeFormatter), 9, 11, 1, 10, 11, "Testing add fn")
        );
        when(repository.findEntriesByDate(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(entries);
        List<TradeEntryResponseDTO> entriesByDate = service.getEntriesByDate(tradeDay);
        assertThat(entriesByDate.size()).isEqualTo(2);
    }
}