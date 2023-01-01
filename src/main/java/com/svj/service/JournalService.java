package com.svj.service;

import com.svj.dto.PreferenceRequestDTO;
import com.svj.dto.PreferenceResponseDTO;
import com.svj.dto.TradeEntryRequestDTO;
import com.svj.dto.TradeEntryResponseDTO;
import com.svj.entity.BlackList;
import com.svj.entity.TradeEntry;
import com.svj.entity.TradeStats;
import com.svj.exceptionHandling.TradeProcessException;
import com.svj.repository.JournalRepository;
import com.svj.utilities.EntityDTOConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.svj.utilities.AppUtils.readJournalEntriesFromFile;
import static com.svj.utilities.EntityDTOConverter.*;
import static com.svj.utilities.JsonParser.jsonToString;

@Service
@Slf4j
public class JournalService {
    private JournalRepository journalRepository;
    private PreferenceService preferenceService;
    private BlackListService blackListService;

    @Autowired
    public JournalService(JournalRepository repository,
                          PreferenceService preferenceService,
                          BlackListService blackListService){
        journalRepository = repository;
        this.preferenceService= preferenceService;
        this.blackListService= blackListService;
    }

    public TradeEntryResponseDTO addEntry(TradeEntryRequestDTO requestDTO){
        try{
            log.info("TradeService: addEntry Starting method.");
            PreferenceResponseDTO preference= preferenceService.getEntriesByName(requestDTO.getTraderName());
            populateDefaultValues(requestDTO, preference);
            TradeEntry tradeEntry = convertDTOToEntity(requestDTO);
            TradeEntry savedEntry= updateBlackListAndSaveJournalEntry(tradeEntry);
            log.debug("TradeService: addEntry Response from db is {}", jsonToString(savedEntry));
            // update balance in preference if exit prices is present
            updateTradersBalance(savedEntry, preference);
            log.info("TradeService: addEntry method ended.");
            return entityToDTO(savedEntry);
        }catch (Exception ex){
            ex.printStackTrace();
            log.error("TradeService: addEntry Exception occured: {}", ex.getMessage());
            throw new TradeProcessException(ex.getMessage());
        }
    }

    private void updateTradersBalance(TradeEntry savedEntry, PreferenceResponseDTO preference) {
        PreferenceResponseDTO savedPreference = null;
        if(savedEntry.getProfit()!= null){
            log.info("TradeService: updateTradersBalance Updating capital in trader preference based on profit from newly added entry");
            preference.setCapital( preference.getCapital() + savedEntry.getProfit() );
            savedPreference = preferenceService.updateEntry(preference.getId(), PreferenceRequestDTO.builder().traderName(preference.getTraderName()).capital(preference.getCapital())
                    .position(preference.getPosition()).product(preference.getProduct()).build());
            log.debug("TradeService: updateTradersBalance Updated capital in trader preference table: {}", jsonToString(savedPreference));
        }
        log.info("TradeService: updateTradersBalance Profit is not set for trade: {}", jsonToString(savedEntry));
    }

    private TradeEntry updateBlackListAndSaveJournalEntry(TradeEntry tradeEntry) {
        TradeEntry savedEntry;
        if (arePercentsPositive(tradeEntry)) {
            BlackList blackList = blackListService.blackListStock(tradeEntry);
            log.debug("TradeService: saveJournalEntryToDB Updated blackList db for Trader: {}. Current blackList: {}", tradeEntry.getTraderName(), jsonToString(blackList));
            savedEntry = journalRepository.save(tradeEntry);
        } else
            throw new TradeProcessException("Percent calculation can not lead to -ve result");
        return savedEntry;
    }

    private void populateDefaultValues(TradeEntryRequestDTO requestDTO, PreferenceResponseDTO preference) {
        if(requestDTO.getCapital()== null)
            requestDTO.setCapital(preference.getCapital());
        if(requestDTO.getPosition()== null)
            requestDTO.setPosition(preference.getPosition());
        if(requestDTO.getProduct()== null)
            requestDTO.setProduct(preference.getProduct());
    }

    public TradeEntryResponseDTO updateEntry(String id, TradeEntryRequestDTO requestDTO){
        try{
            log.info("TradeService: updateEntry Starting method.");
            TradeEntry dbEntry= journalRepository.findById(id).orElseThrow(()->  new TradeProcessException("Unable to find the requested trade entry"));
            log.debug("Retrieved entry from db is {}", dbEntry);
            PreferenceResponseDTO preference = preferenceService.getEntriesByName(requestDTO.getTraderName());
            populateDefaultValues(requestDTO, preference);
            // if we have a difference in entry and exit prices between new and existing entry, update the new profit after removing older profit
            if((requestDTO.getEntryPrice()!=null && requestDTO.getExitPrice()!=null) && (requestDTO.getEntryPrice()!= dbEntry.getEntryPrice() || requestDTO.getExitPrice()!= dbEntry.getExitPrice()) ){
                log.info("TradeService: updateEntry There is a change in profit compared to previous entry. Updating capital in trader preference based on change");
                preference.setCapital( preference.getCapital() - dbEntry.getProfit() + (requestDTO.getExitPrice()- requestDTO.getEntryPrice()));
                PreferenceResponseDTO savedPreference = preferenceService.updateEntry(preference.getId(), PreferenceRequestDTO.builder().traderName(preference.getTraderName()).capital(preference.getCapital())
                        .position(preference.getPosition()).product(preference.getProduct()).build());
                log.debug("TradeService: updateEntry Updated capital in trader preference table: {}", jsonToString(savedPreference));
            }
            copyReqToEntity(requestDTO, dbEntry);
            TradeEntry savedEntry= updateBlackListAndSaveJournalEntry(dbEntry);
            log.debug("TradeService: updateEntry Updated entry from db is {}", jsonToString(savedEntry));
            log.info("TradeService: updateEntry method ended.");
            return entityToDTO(savedEntry);
        }catch (Exception ex){
            log.error("TradeService: updateEntry Exception occurred: {}", ex.getMessage());
            throw new TradeProcessException(ex.getMessage());
        }
    }
    public TradeEntryResponseDTO getEntryByID(String id){
        try{
            log.info("TradeService: getEntryByID Starting method.");
            TradeEntry dbEntry= journalRepository.findById(id).orElseThrow(()->  new TradeProcessException("Unable to find the requested trade entry"));
            log.debug("TradeService: getEntryByID Retrieved entry from db is {}", jsonToString(dbEntry));
            log.info("TradeService: getEntryByID method ended.");
            return entityToDTO(dbEntry);
        }catch (Exception ex){
            log.error("TradeService: getEntryByID Exception occured: {}", ex.getMessage());
            throw new TradeProcessException(ex.getMessage());
        }
    }

    public List<TradeEntryResponseDTO> getAllEntries(){
        try{
            log.info("TradeService: getAllEntries Starting method.");
            List<TradeEntry> dbEntries= journalRepository.findAll();
            log.debug("TradeService: getAllEntries Retrieved entries from db is {}", dbEntries.toString());
            List<TradeEntryResponseDTO> response= StreamSupport.stream(dbEntries.spliterator(), false)
                                                    .map(EntityDTOConverter:: entityToDTO)
                                                    .collect(Collectors.toList());
            log.info("TradeService: getAllEntries method ended.");
            return response;
        }catch (Exception ex){
            log.error("TradeService: getAllEntries Exception occured: {}", ex.getMessage());
            throw new TradeProcessException(ex.getMessage());
        }
    }

    public String deleteEntry(String id){
        try{
            log.info("TradeService: deleteEntry Starting method.");
            TradeEntry dbEntry= journalRepository.findById(id).orElseThrow(()->  new TradeProcessException("Unable to find the requested trade entry"));
            PreferenceResponseDTO preference = preferenceService.getEntriesByName(dbEntry.getTraderName());
            // remove the profit from total capital due to this entry
            if((dbEntry.getEntryPrice()!=null && dbEntry.getExitPrice()!=null) ){
                log.info("TradeService: deleteEntry Removing profit in preference table due to this entry");
                preference.setCapital( preference.getCapital() - dbEntry.getProfit());
                PreferenceResponseDTO savedPreference = preferenceService.updateEntry(preference.getId(), PreferenceRequestDTO.builder().traderName(preference.getTraderName()).capital(preference.getCapital())
                        .position(preference.getPosition()).product(preference.getProduct()).build());
                log.debug("TradeService: deleteEntry Updated capital in trader preference table: {}", jsonToString(savedPreference));
            }
            journalRepository.deleteById(id);
            log.info("TradeService: deleteEntry method ended.");
            return String.format("Deleted entry with id %s", id);
        }catch (Exception ex){
            log.error("TradeService: deleteEntry Exception occured: {}", ex.getMessage());
            throw new TradeProcessException(ex.getMessage());
        }
    }

    public List<TradeEntryResponseDTO> getEntriesBetweenDates(String traderName, LocalDate fromDate, LocalDate toDate) {
        try{
            log.info("TradeService: getEntriesBetweenDates Starting method.");
            List<TradeEntry> entries = journalRepository.findEntriesByDate(traderName, fromDate, toDate);
            log.debug("TradeService: getEntriesBetweenDates Retrieved entries from db is {}", entries.toString());
            List<TradeEntryResponseDTO> response= StreamSupport.stream(entries.spliterator(), false)
                    .map(EntityDTOConverter:: entityToDTO)
                    .collect(Collectors.toList());
            log.info("TradeService: getEntriesBetweenDates method ended.");
            return response;
        }catch (Exception ex){
            log.error("TradeService: getEntriesBetweenDates Exception occured: {}", ex.getMessage());
            throw new TradeProcessException(ex.getMessage());
        }
    }

    public TradeStats computeStats(List<TradeEntryResponseDTO> entriesBetweenDates, LocalDate fromDate, LocalDate toDate){
        TradeStats result= new TradeStats();
        result.setFromDate(fromDate); result.setToDate(toDate);
        int totalTrades= 0, lossCount= 0, openTradeCount= 0;
        double winProbability, totalProfit= 0, winCount= 0, totalCapitalGain= 0;
        List<String> successComments= new LinkedList<>();
        List<String> cautionComments= new LinkedList<>();
        List<TradeEntryResponseDTO> openTrades= new LinkedList<>();
        for(TradeEntryResponseDTO journal: entriesBetweenDates){
            if(journal.getExitPrice()!= null){
                // if trade is complete process it
                totalTrades++;
                totalProfit+= journal.getProfit();
                if(journal.getProfit()> 0){
                    // if profit, add corresponding elements to stats and for loss too
                    winCount++;
                    successComments.add(String.format("Entry: %s || Exit: %s",journal.getEntryComments(),journal.getExitComments()));
                }else{
                    lossCount++;
                    cautionComments.add(String.format("Entry: %s || Exit: %s",journal.getEntryComments(),journal.getExitComments()));
                }
                totalCapitalGain+= journal.getProfitPercent();
            }else{
                openTradeCount++;
                openTrades.add(journal);
            }
        }
        if(totalTrades> 0) {
            winProbability = winCount / totalTrades*100;
            result.setTotalTrades(totalTrades);
            result.setLossCount(lossCount);
            result.setWinCount((int)winCount);
            result.setWinProbability(winProbability);
            result.setTotalProfit(totalProfit);
            result.setSuccessComments(successComments);
            result.setCautionComments(cautionComments);
            result.setFromDate(fromDate);
            result.setToDate(toDate);
            result.setOpenTradeCount(openTradeCount);
            result.setTotalPoints((int) (winCount-lossCount));
            result.setTotalCapitalGain(totalCapitalGain);
            result.setOpenTrades(openTrades);
        }
        return result;
    }

    public boolean arePercentsPositive(TradeEntry entry){
        if(entry.getSLPercent()>=0 && entry.getT1Percent()>=0 )
            return true;
        else
            return false;
    }

    public List<TradeEntryResponseDTO> bulkAddEntries(String filePath, String traderName){
        try{
            log.info("TradeService: bulkAddEntries Starting method.");
            List<TradeEntry> tradeEntries = readJournalEntriesFromFile(filePath, traderName);
            log.debug("TradeService: bulkAddEntries Read info from file is {}", jsonToString(tradeEntries));
            List<TradeEntryResponseDTO> response = new LinkedList<>();
            PreferenceResponseDTO preference= preferenceService.getEntriesByName(traderName);
            for(TradeEntry journalEntry: tradeEntries) {
                if(journalEntry.getCapital()== null)
                    journalEntry.setCapital(preference.getCapital());
                if(journalEntry.getPosition()== null)
                    journalEntry.setPosition(preference.getPosition());
                if(journalEntry.getProduct()== null)
                    journalEntry.setProduct(preference.getProduct());
                log.debug("TradeService: bulkAddEntries Saving an entry to DB: {}", jsonToString(journalEntry));
                TradeEntry savedEntry= updateBlackListAndSaveJournalEntry(journalEntry);
                // update balance in preference if exit prices is present
                updateTradersBalance(savedEntry, preference);
                log.info("TradeService: bulkAddEntries method ended.");
                response.add(entityToDTO(savedEntry));
            }
            log.info("TradeService: bulkAddEntries Method ended");
            return response;
        }catch (Exception e){
            log.error("TradeService: bulkAddEntries Exception occurred while saving data {}", e.getMessage());
            return null;
        }
    }
}
