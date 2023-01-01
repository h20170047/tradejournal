package com.svj.service;

import com.svj.dto.PreferenceRequestDTO;
import com.svj.dto.PreferenceResponseDTO;
import com.svj.entity.TraderPreference;
import com.svj.exceptionHandling.TradeProcessException;
import com.svj.repository.PreferenceRepository;
import com.svj.utilities.EntityDTOConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.svj.utilities.EntityDTOConverter.*;

@Service
@Slf4j
public class PreferenceService {
    private PreferenceRepository preferenceRepository;

    @Value("${preference.capital}")
    private double capital;
    @Value("${preference.position}")
    private String position;
    @Value("${preference.product}")
    private String product;

    @Autowired
    public PreferenceService(PreferenceRepository preferenceRepository){
        this.preferenceRepository= preferenceRepository;
    }

    public PreferenceResponseDTO addEntry(PreferenceRequestDTO requestDTO){
        try{
            log.info("PreferenceService: addEntry Starting method.");
            TraderPreference TraderPreference = convertDTOToEntity(requestDTO);
            TraderPreference savedEntry = preferenceRepository.save(TraderPreference);
            log.debug("PreferenceService: addEntry Response from db is {}", savedEntry);
            log.info("PreferenceService: addEntry method ended.");
            return entityToDTO(savedEntry);
        }catch (Exception ex){
            log.error("PreferenceService: addEntry Exception occured: {}", ex.getMessage());
            throw new TradeProcessException(ex.getMessage());
        }
    }

    public PreferenceResponseDTO updateEntry(String id, PreferenceRequestDTO requestDTO){
        try{
            log.info("PreferenceService: updateEntry Starting method.");
            TraderPreference dbEntry= preferenceRepository.findById(id).orElseThrow(()->  new TradeProcessException("Unable to find the requested trade entry"));
            log.debug("Retrieved entry from db is {}", dbEntry);
            copyReqToEntity(requestDTO, dbEntry);
            TraderPreference savedEntry = preferenceRepository.save(dbEntry);
            log.debug("PreferenceService: updateEntry Updated entry from db is {}", savedEntry);
            log.info("PreferenceService: updateEntry method ended.");
            return entityToDTO(savedEntry);
        }catch (Exception ex){
            log.error("PreferenceService: updateEntry Exception occured: {}", ex.getMessage());
            throw new TradeProcessException(ex.getMessage());
        }
    }
    public PreferenceResponseDTO getEntryByID(String id){
        try{
            log.info("PreferenceService: getEntryByID Starting method.");
            TraderPreference dbEntry= preferenceRepository.findById(id).orElseThrow(()->  new TradeProcessException("Unable to find the requested trade entry"));
            log.debug("PreferenceService: getEntryByID Retrieved entry from db is {}", dbEntry);
            log.info("PreferenceService: getEntryByID method ended.");
            return entityToDTO(dbEntry);
        }catch (Exception ex){
            log.error("PreferenceService: getEntryByID Exception occured: {}", ex.getMessage());
            throw new TradeProcessException(ex.getMessage());
        }
    }

    public List<PreferenceResponseDTO> getAllEntries(){
        try{
            log.info("PreferenceService: getAllEntries Starting method.");
            List<TraderPreference> dbEntries= preferenceRepository.findAll();
            log.debug("PreferenceService: getAllEntries Retrieved entries from db is {}", dbEntries.toString());
            List<PreferenceResponseDTO> response= StreamSupport.stream(dbEntries.spliterator(), false)
                                                    .map(EntityDTOConverter:: entityToDTO)
                                                    .collect(Collectors.toList());
            log.info("PreferenceService: getAllEntries method ended.");
            return response;
        }catch (Exception ex){
            log.error("PreferenceService: getAllEntries Exception occured: {}", ex.getMessage());
            throw new TradeProcessException(ex.getMessage());
        }
    }

    public String deleteEntry(String id){
        try{
            log.info("PreferenceService: deleteEntry Starting method.");
            preferenceRepository.deleteById(id);
            log.info("PreferenceService: deleteEntry method ended.");
            return String.format("Deleted entry with id %s", id);
        }catch (Exception ex){
            log.error("PreferenceService: deleteEntry Exception occured: {}", ex.getMessage());
            throw new TradeProcessException(ex.getMessage());
        }
    }

    public PreferenceResponseDTO getEntriesByName(String name) {
        try{
            log.info("PreferenceService: getEntriesByName Starting method.");
            TraderPreference preference = preferenceRepository.findByTraderName(name);
            if(preference== null)
                preference= preferenceRepository.save(TraderPreference.builder().traderName(name).capital(capital).position(position).product(product).build());
            log.debug("PreferenceService: getEntriesByName Retrieved entries from db is {}", preference.toString());
            log.info("PreferenceService: getEntriesByName method ended.");
            return entityToDTO(preference);
        }catch (Exception ex){
            log.error("PreferenceService: getEntriesByName Exception occured: {}", ex.getMessage());
            throw new TradeProcessException(ex.getMessage());
        }
    }

}
