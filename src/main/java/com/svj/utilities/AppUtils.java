package com.svj.utilities;

import com.svj.dto.TradeEntryRequestDTO;
import com.svj.dto.TradeEntryResponseDTO;
import com.svj.entity.TradeEntry;
import com.svj.entity.TradeStats;
import com.svj.exceptionHandling.FileException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.svj.utilities.EntityDTOConverter.computeDerivedValues;

public class AppUtils {
    public static DateTimeFormatter dateTimeFormatter= DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");

    public static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d-M-yyyy");
    public static DateTimeFormatter dateFormatter2 = DateTimeFormatter.ofPattern("dd/MM/yy");
    public static DateTimeFormatter dateFormatter3 = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    public static List<String> getResourceFileAsStringList(String fileName) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream is = classLoader.getResourceAsStream(fileName)) {
            if (is == null){
                throw new FileException(String.format("Missing %s file in classpath", fileName));
            }
            try (InputStreamReader isr = new InputStreamReader(is);
                 BufferedReader reader = new BufferedReader(isr)) {
                List<String> lines = reader.lines().collect(Collectors.toList());
                return lines;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getFileNameFromDate(LocalDate day) {
        String dayOfMonth= day.getDayOfMonth()<10?"0".concat(String.valueOf(day.getDayOfMonth())): String.valueOf(day.getDayOfMonth());
        String monthAlpha= day.getMonth().toString().substring(0, 3);
        String yearStr= String.valueOf(day.getYear());
        return "cm".concat(dayOfMonth).concat(monthAlpha).concat(yearStr).concat("bhav.csv");
    }

    public static List<TradeEntry> readJournalEntriesFromFile(String filePath, String traderName){
        List<String> fileContent = getResourceFileAsStringList(filePath);
        fileContent= fileContent.subList(1, fileContent.size());

        List<TradeEntry> entries= new LinkedList<>();
        for(String line: fileContent){
            String[] entryArr = line.split(",");
            if(entryArr.length>=14){
                TradeEntry entryRequest = TradeEntry.builder()
                        .traderName(traderName)
                        .entryDate(LocalDate.parse(entryArr[1], dateFormatter2))
                        .exitDate(LocalDate.parse(entryArr[1], dateFormatter2))
                        .symbol(entryArr[2])
                        .position(entryArr[3])
                        .quantity(Integer.valueOf(entryArr[4]))
                        .entryPrice(Double.valueOf(entryArr[5]))
                        .exitPrice(Double.valueOf(entryArr[6]))
                        .SL(Double.valueOf(entryArr[7]))
                        .T1(Double.valueOf(entryArr[8]))
                        .entryComments(entryArr[10])
                        .profit(Double.valueOf(entryArr[11]))
                        .exitComments(entryArr[13])
                        .remarks(entryArr[14])
                        .capital(Double.valueOf(entryArr[15]))
                        .build();
                if(entryRequest.getEntryDate()!= null && entryRequest.getExitDate()!= null)
                    entryRequest.setProduct("INTRADAY");
                else
                    entryRequest.setProduct("DELIVERY");
                computeDerivedValues(entryRequest);
                entries.add(entryRequest);
            }
        }
        return entries;
    }

    public static ByteArrayInputStream generateReport(List<TradeEntryResponseDTO> entries, TradeStats stats) {
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
            CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format);) {
            csvPrinter.printRecord(Arrays.asList("SL. NO.", "ENTRY DATE", "EXIT DATE", "SYMBOL", "QUANTITY", "POSITION", "PRODUCT",
                    "ENTRY PRICE", "STOP LOSS", "TARGET", "RISK% (CAPITAL)", "REWARD:RISK", "EXIT PRICE", "PROFIT", "PROFIT %",
                    "ENTRY COMMENTS", "EXIT COMMENTS", "REMARKS"));
            Collections.sort(entries, Comparator.comparing(TradeEntryResponseDTO::getEntryDate));
            int sl= 1;
            for (TradeEntryResponseDTO entry : entries) {
                List<String> data = Arrays.asList(
                        String.valueOf(sl++),
                        entry.getEntryDate().toString(), entry.getExitDate().toString(), entry.getSymbol(), String.valueOf(entry.getQuantity()),
                        entry.getPosition(), entry.getProduct(), entry.getEntryPrice().toString(),
                        entry.getSL().toString(), entry.getT1().toString(), String.valueOf(entry.getRiskPercent()),
                        String.valueOf(entry.getRewardRiskRatio()), entry.getExitPrice().toString(), entry.getProfit().toString(),
                        entry.getProfitPercent().toString(), entry.getEntryComments(), entry.getExitComments(), entry.getRemarks()
                );
                csvPrinter.printRecord(data);
            }

            csvPrinter.printRecord("\n");
            csvPrinter.printRecord("\nSTATS\n");
            csvPrinter.printRecord(Arrays.asList("FROM", "TO", "TOTAL TRADES", "OPEN TRADE COUNT", "WIN COUNT", "LOSS COUNT", "TOTAL POINTS",
                    "WIN PROBABILITY", "TOTAL PROFIT", "TOTAL CAPITAL GAIN %"
            ));
            List<String> statsData = Arrays.asList(
                    stats.getFromDate().toString(), stats.getToDate().toString(), String.valueOf(stats.getTotalTrades()), String.valueOf(stats.getOpenTrades()),
                    String.valueOf(stats.getWinCount()), String.valueOf(stats.getLossCount()), String.valueOf(stats.getTotalPoints()), String.valueOf(stats.getWinProbability()),
                    String.valueOf(stats.getTotalProfit()), String.valueOf(stats.getTotalCapitalGain())
            );
            csvPrinter.printRecord(statsData);


            csvPrinter.printRecord("\n");
            if(stats.getOpenTradeCount()> 0) {
                csvPrinter.printRecord("\nOPEN TRADES\n");
                csvPrinter.printRecord(Arrays.asList("SL. NO.", "ENTRY DATE", "EXIT DATE", "SYMBOL", "QUANTITY", "POSITION", "PRODUCT",
                        "ENTRY PRICE", "STOP LOSS", "TARGET", "RISK% (CAPITAL)", "REWARD:RISK", "EXIT PRICE", "PROFIT", "PROFIT %",
                        "ENTRY COMMENTS", "EXIT COMMENTS", "REMARKS"));
                List<TradeEntryResponseDTO> openTrades = stats.getOpenTrades();
                Collections.sort(openTrades, Comparator.comparing(TradeEntryResponseDTO::getEntryDate));
                sl = 1;
                for (TradeEntryResponseDTO entry : openTrades) {
                    List<String> data = Arrays.asList(
                            String.valueOf(sl++),
                            entry.getEntryDate().toString(), entry.getExitDate().toString(), entry.getSymbol(), String.valueOf(entry.getQuantity()),
                            entry.getPosition().toString(), entry.getProduct().toString(), entry.getEntryPrice().toString(),
                            entry.getSL().toString(), entry.getT1().toString(), String.valueOf(entry.getRiskPercent()),
                            String.valueOf(entry.getRewardRiskRatio()), entry.getExitPrice().toString(), entry.getProfit().toString(),
                            entry.getProfitPercent().toString(), entry.getEntryComments(), entry.getExitComments(), entry.getRemarks()
                    );
                    csvPrinter.printRecord(data);
                }
                csvPrinter.printRecord("\n");
            }

            csvPrinter.printRecord("\nCAUTION COMMENTS\n");
            for(String caution: stats.getCautionComments())
                csvPrinter.printRecord(caution);

            csvPrinter.printRecord("\n");
            csvPrinter.printRecord("\nSUCCESS COMMENTS\n");
            for(String successMsg: stats.getSuccessComments())
                csvPrinter.printRecord(successMsg);

            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("failed to import data to CSV file: " + e.getMessage());
        }
    }
}
