package com.svj.service;

import com.svj.dto.PreferenceRequestDTO;
import com.svj.dto.PreferenceResponseDTO;
import com.svj.entity.TraderPreference;
import com.svj.repository.PreferenceRepository;
import com.svj.utilities.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PreferenceServiceTest {
    @InjectMocks
    private PreferenceService service;
    @Mock
    private PreferenceRepository repository;
    PreferenceRequestDTO requestDTO;
    TraderPreference preference;
    @BeforeEach
    void setUp() {
        requestDTO= PreferenceRequestDTO.builder()
                .traderName("TEST").capital(1000.0).product("INTRADAY").position("LONG")
                .build();
        preference= TraderPreference.builder().id("ID").traderName("TEST").capital(1000.0).product("INTRADAY").position("LONG")
                .build();
    }

    @Test
    void addEntry() {
        when(repository.save(any(TraderPreference.class))).thenReturn(preference);
        PreferenceResponseDTO tradeEntryResponseDTO = service.addEntry(requestDTO);
        assertThat(tradeEntryResponseDTO.getId()).isNotEmpty();
    }

    @Test
    void updateEntry() {
        when(repository.save(any(TraderPreference.class))).thenReturn(preference);
        when(repository.findById(any(String.class))).thenReturn(Optional.of(preference));
        PreferenceResponseDTO responseDTO = service.updateEntry("id", requestDTO);
        assertThat(responseDTO.getId()).isNotEmpty();
    }

    @Test
    void getEntryByID() {
        when(repository.findById(any(String.class))).thenReturn(Optional.of(preference));
        PreferenceResponseDTO responseDTO = service.getEntryByID("id");
        assertThat(responseDTO.getId()).isNotEmpty();
    }

    @Test
    void getAllEntries() {
        List<TraderPreference> entities= Arrays.asList(
                TraderPreference.builder().id("ID-1").traderName("TEST").capital(1000.0).product("INTRADAY").position("LONG").build(),
                TraderPreference.builder().id("id-2").traderName("TEST").capital(1000.0).product("INTRADAY").position("LONG").build()
        );
        when(repository.findAll()).thenReturn(entities);
        List<PreferenceResponseDTO> entries = service.getAllEntries();
        assertThat(entries.size()).isEqualTo(2);
    }

    @Test
    void deleteEntry() {
        doNothing().when(repository).deleteById(any(String.class));
        String response = service.deleteEntry("id-1");
        assertThat(response).contains("id-1");
    }

    @Test
    void getEntriesByName() {
        when(repository.findByTraderName(any(String.class))).thenReturn(preference);
        PreferenceResponseDTO responseDTO = service.getEntriesByName("name");
        assertThat(responseDTO.getId()).isNotEmpty();
    }
}