package com.svj.service;

import com.svj.dto.TradeEntryRequestDTO;
import com.svj.dto.TradeEntryResponseDTO;
import com.svj.entity.BlackList;
import com.svj.entity.TradeEntry;
import com.svj.entity.TradeStats;
import com.svj.entity.TraderPreference;
import com.svj.exceptionHandling.TradeProcessException;
import com.svj.repository.JournalRepository;
import com.svj.repository.PreferenceRepository;
import com.svj.utilities.Constants;
import com.svj.utilities.EntityDTOConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.svj.utilities.EntityDTOConverter.*;
import static com.svj.utilities.JsonParser.jsonToString;

@Service
@Slf4j
public class JournalService {
    private JournalRepository journalRepository;
    private PreferenceRepository preferenceRepository;
    private BlackListService blackListService;

    @Autowired
    public JournalService(JournalRepository repository,
                          PreferenceRepository preferenceRepository,
                          BlackListService blackListService){
        journalRepository = repository;
        this.preferenceRepository= preferenceRepository;
        this.blackListService= blackListService;
    }

    public TradeEntryResponseDTO addEntry(TradeEntryRequestDTO requestDTO){
        try{
            log.info("TradeService: addEntry Starting method.");
            TraderPreference preference = preferenceRepository.findByTraderName(requestDTO.getTraderName());
            populateDefaultValues(preference, requestDTO);
            TradeEntry tradeEntry = convertDTOToEntity(requestDTO);
            TradeEntry savedEntry;
            if(arePercentsPostivie(tradeEntry)) {
                BlackList blackList = blackListService.blackListStock(tradeEntry);
                log.debug("TradeService: addEntry Updated blackList db for Trader: {}. Current blackList: {}", tradeEntry.getTraderName(), jsonToString(blackList));
                savedEntry = journalRepository.save(tradeEntry);
            }else
                throw new TradeProcessException("Percent calculation can not lead to -ve result");
            log.debug("TradeService: addEntry Response from db is {}", jsonToString(savedEntry));
            // update balance in preference if exit prices is present
            if(savedEntry.getProfit()!= null){
                log.info("TradeService: addEntry Updating capital in trader preference based on profit from newly added entry");
                preference.setCapital( preference.getCapital() + savedEntry.getProfit() );
                TraderPreference savedPreference = preferenceRepository.save(preference);
                log.debug("TradeService: addEntry Updated capital in trader preference table: {}", jsonToString(savedPreference));
            }
            log.info("TradeService: addEntry method ended.");
            return entityToDTO(savedEntry);
        }catch (Exception ex){
            ex.printStackTrace();
            log.error("TradeService: addEntry Exception occured: {}", ex.getMessage());
            throw new TradeProcessException(ex.getMessage());
        }
    }

    private void populateDefaultValues(TraderPreference preference, TradeEntryRequestDTO requestDTO) {
        if(requestDTO.getCapital()== null)
            requestDTO.setCapital(preference.getCapital());
        if(requestDTO.getPosition()== null)
            requestDTO.setPosition(Constants.POSITION.valueOf(preference.getPosition()));
        if(requestDTO.getProduct()== null)
            requestDTO.setProduct(Constants.PRODUCT.valueOf(preference.getProduct()));
    }

    public TradeEntryResponseDTO updateEntry(String id, TradeEntryRequestDTO requestDTO){
        try{
            log.info("TradeService: updateEntry Starting method.");
            TradeEntry dbEntry= journalRepository.findById(id).orElseThrow(()->  new TradeProcessException("Unable to find the requested trade entry"));
            log.debug("Retrieved entry from db is {}", dbEntry);
            TraderPreference preference = preferenceRepository.findByTraderName(requestDTO.getTraderName());
            populateDefaultValues(preference, requestDTO);
            // if we have a difference in entry and exit prices between new and existing entry, update the new profit after removing older profit
            if((requestDTO.getEntryPrice()!=null && requestDTO.getExitPrice()!=null) && (requestDTO.getEntryPrice()!= dbEntry.getEntryPrice() || requestDTO.getExitPrice()!= dbEntry.getExitPrice()) ){
                log.info("TradeService: updateEntry There is a change in profit compared to previous entry. Updating capital in trader preference based on change");
                preference.setCapital( preference.getCapital() - dbEntry.getProfit() + (requestDTO.getExitPrice()- requestDTO.getEntryPrice()));
                TraderPreference savedPreference = preferenceRepository.save(preference);
                log.debug("TradeService: updateEntry Updated capital in trader preference table: {}", jsonToString(preference));
            }
            copyReqToEntity(requestDTO, dbEntry);
            TradeEntry savedEntry;
            if(arePercentsPostivie(dbEntry)) {
                BlackList blackList = blackListService.blackListStock(dbEntry);
                log.debug("TradeService: addEntry Updated blackList db for Trader: {}. Current blackList: {}", dbEntry.getTraderName(), jsonToString(blackList));
                savedEntry = journalRepository.save(dbEntry);
            }else
                throw new TradeProcessException("Percent calculation can not lead to -ve result");
            log.debug("TradeService: updateEntry Updated entry from db is {}", jsonToString(savedEntry));
            log.info("TradeService: updateEntry method ended.");
            return entityToDTO(savedEntry);
        }catch (Exception ex){
            log.error("TradeService: updateEntry Exception occured: {}", ex.getMessage());
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
            TraderPreference preference = preferenceRepository.findByTraderName(dbEntry.getTraderName());
            // remove the profit from total capital due to this entry
            if((dbEntry.getEntryPrice()!=null && dbEntry.getExitPrice()!=null) ){
                log.info("TradeService: deleteEntry Removing profit in preference table due to this entry");
                preference.setCapital( preference.getCapital() - dbEntry.getProfit());
                TraderPreference savedPreference = preferenceRepository.save(preference);
                log.debug("TradeService: deleteEntry Updated capital in trader preference table: {}", jsonToString(preference));
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

    public TradeStats computeStats(String traderName, LocalDate fromDate, LocalDate toDate){
        List<TradeEntryResponseDTO> entriesBetweenDates = getEntriesBetweenDates(traderName, fromDate, toDate);
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
            winProbability = winCount / totalTrades;
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

    public boolean arePercentsPostivie(TradeEntry entry){
        if(entry.getSLPercent()>0 && entry.getT1Percent()>0 && (entry.getT2()== null || (entry.getT2()!= null && entry.getT2Percent()> 0)))
            return true;
        else
            return false;
    }
}
