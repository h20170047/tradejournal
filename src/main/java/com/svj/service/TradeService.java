package com.svj.service;

import com.svj.dto.TradeEntryRequestDTO;
import com.svj.dto.TradeEntryResponseDTO;
import com.svj.entity.TradeEntry;
import com.svj.exceptionHandling.TradeProcessException;
import com.svj.repository.TradeRepository;
import com.svj.utilities.EntityDTOConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.svj.utilities.EntityDTOConverter.*;

@Service
@Slf4j
public class TradeService {
    private TradeRepository tradeRepository;

    @Autowired
    public TradeService(TradeRepository repository){
        tradeRepository= repository;
    }

    public TradeEntryResponseDTO addEntry(TradeEntryRequestDTO requestDTO){
        try{
            log.info("TradeService: addEntry Starting method.");
            TradeEntry tradeEntry = convertDTOToEntity(requestDTO);
            calculateProfit(tradeEntry);
            TradeEntry savedEntry = tradeRepository.save(tradeEntry);
            log.debug("TradeService: addEntry Response from db is {}", savedEntry);
            log.info("TradeService: addEntry method ended.");
            return entityToDTO(savedEntry);
        }catch (Exception ex){
            log.error("TradeService: addEntry Exception occured: {}", ex.getMessage());
            throw new TradeProcessException(ex.getMessage());
        }
    }

    private void calculateProfit(TradeEntry tradeEntry) {
        if(tradeEntry.getSellPrice()>0 && tradeEntry.getBuyPrice()> 0){
            tradeEntry.setProfit( (tradeEntry.getSellPrice()- tradeEntry.getBuyPrice())/ tradeEntry.getBuyPrice()*100 );
        }
    }

    public TradeEntryResponseDTO updateEntry(String id, TradeEntryRequestDTO requestDTO){
        try{
            log.info("TradeService: updateEntry Starting method.");
            TradeEntry dbEntry= tradeRepository.findById(id).orElseThrow(()->  new TradeProcessException("Unable to find the requested trade entry"));
            log.debug("Retrieved entry from db is {}", dbEntry);
            copyReqToEntity(requestDTO, dbEntry);
            calculateProfit(dbEntry);
            TradeEntry savedEntry = tradeRepository.save(dbEntry);
            log.debug("TradeService: updateEntry Updated entry from db is {}", savedEntry);
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
            TradeEntry dbEntry= tradeRepository.findById(id).orElseThrow(()->  new TradeProcessException("Unable to find the requested trade entry"));
            log.debug("TradeService: getEntryByID Retrieved entry from db is {}", dbEntry);
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
            List<TradeEntry> dbEntries= tradeRepository.findAll();
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
            tradeRepository.deleteById(id);
            log.info("TradeService: deleteEntry method ended.");
            return String.format("Deleted entry with id %s", id);
        }catch (Exception ex){
            log.error("TradeService: deleteEntry Exception occured: {}", ex.getMessage());
            throw new TradeProcessException(ex.getMessage());
        }
    }

    public List<TradeEntryResponseDTO> getEntriesByDate(LocalDateTime date) {
        try{
            log.info("TradeService: getEntriesByDate Starting method.");
            List<TradeEntry> entries = tradeRepository.findEntriesByDate(date, date.plusDays(1));
            log.debug("TradeService: getEntriesByDate Retrieved entries from db is {}", entries.toString());
            List<TradeEntryResponseDTO> response= StreamSupport.stream(entries.spliterator(), false)
                    .map(EntityDTOConverter:: entityToDTO)
                    .collect(Collectors.toList());
            log.info("TradeService: getEntriesByDate method ended.");
            return response;
        }catch (Exception ex){
            log.error("TradeService: getEntriesByDate Exception occured: {}", ex.getMessage());
            throw new TradeProcessException(ex.getMessage());
        }
    }
}
