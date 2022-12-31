package com.svj.utilities;

import com.svj.dto.TradeEntryRequestDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static com.svj.utilities.AppUtils.dateFormatter;
import static org.assertj.core.api.Assertions.assertThat;

class AppUtilsTest {
    AppUtils appUtils= new AppUtils();

    @Test
    void getResourceFileAsStringList() {
        List<String> fileAsStringList = AppUtils.getResourceFileAsStringList("Nifty50List.txt");
        assertThat(fileAsStringList.size()).isEqualTo(50);
    }

    @Test
    void getFileNameFromDate() {
        String fileNameFromDate = appUtils.getFileNameFromDate(LocalDate.parse("15-11-2022", dateFormatter));
        assertThat(fileNameFromDate).isEqualTo("cm15NOV2022bhav.csv");
    }

    @Test
    void readJournalEntriesFromFile() {
        List<TradeEntryRequestDTO> tradeEntries = appUtils.readJournalEntriesFromFile("InputData.csv", "Swaraj");
        assertThat(tradeEntries).isNotNull();
    }
}