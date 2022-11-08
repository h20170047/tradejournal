package com.svj.controller;

import com.svj.dto.TradeEntryRequestDTO;
import com.svj.dto.TradeEntryResponseDTO;
import com.svj.dto.ServiceResponse;
import com.svj.service.TradeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.svj.utilities.JsonParser.dateFormatter;
import static com.svj.utilities.JsonParser.jsonToString;

@RestController
@RequestMapping("/trade-journal")
@Slf4j
public class JournalController {

    private TradeService tradeService;

    @Autowired
    public JournalController(TradeService service){
        tradeService= service;
    }

    @PostMapping
    public ServiceResponse addEntry(@RequestBody @Valid TradeEntryRequestDTO requestDTO){
        log.info("JournalController: addEntry Starting method with payload {}", jsonToString(requestDTO));
        TradeEntryResponseDTO responseDTO = tradeService.addEntry(requestDTO);
        log.debug("JournalController: addEntry Response from service is {}", jsonToString(responseDTO));
        ServiceResponse response= new ServiceResponse(HttpStatus.CREATED, responseDTO, null);
        log.info("JournalController: addEntry Method returning with {} ", response);
        return response;
    }

    @PutMapping("/{id}")
    public ServiceResponse updateEntry(@PathVariable String id, @RequestBody @Valid TradeEntryRequestDTO requestDTO){
        log.info("JournalController: updateEntry Starting method with id- {} and payload- {}", id, jsonToString(requestDTO));
        TradeEntryResponseDTO responseDTO = tradeService.updateEntry(id, requestDTO);
        log.debug("JournalController: updateEntry Response from service is {}", jsonToString(responseDTO));
        ServiceResponse response= new ServiceResponse(HttpStatus.OK, responseDTO, null);
        log.info("JournalController: updateEntry Method returning with {} ", response);
        return response;
    }

    @GetMapping("/{id}")
    public ServiceResponse getEntryWithID(@PathVariable String id){
        log.info("JournalController: getEntryWithID Starting method with id- {} ", id);
        TradeEntryResponseDTO responseDTO = tradeService.getEntryByID(id);
        log.debug("JournalController: getEntryWithID Response from service is {}", jsonToString(responseDTO));
        ServiceResponse response= new ServiceResponse(HttpStatus.OK, responseDTO, null);
        log.info("JournalController: getEntryWithID Method returning with {}", response);
        return response;
    }


    @GetMapping()
    public ServiceResponse getAllEntries(){
        log.info("JournalController: getAllEntries Starting method");
        List<TradeEntryResponseDTO> allEntries = tradeService.getAllEntries();
        log.debug("JournalController: getEntryWithID Response from service is {}", jsonToString(allEntries));
        ServiceResponse response= new ServiceResponse(HttpStatus.OK, allEntries, null);
        log.info("JournalController: getEntryWithID Method returning with {}", response);
        return response;
    }

    @DeleteMapping("/{id}")
    public ServiceResponse deleteByID(@PathVariable String id){
        log.info("JournalController: deleteByID Starting method with id- {}", id);
        String responseStr= tradeService.deleteEntry(id);
        log.debug("JournalController: deleteByID Response from service is {}", responseStr);
        ServiceResponse response= new ServiceResponse(HttpStatus.NO_CONTENT, responseStr, null);
        log.info("JournalController: deleteByID Method returning with {}", response);
        return response;
    }

    @GetMapping("/date/{date}")
    public ServiceResponse getEntriesByDate(@PathVariable String date){
        log.info("JournalController: getEntriesByDate Starting method with date- {}", date);
        List<TradeEntryResponseDTO> allEntries = tradeService.getEntriesByDate(LocalDateTime.parse(date,dateFormatter));
        log.debug("JournalController: getEntriesByDate Response from service is {}", jsonToString(allEntries));
        ServiceResponse response= new ServiceResponse(HttpStatus.OK, allEntries, null);
        log.info("JournalController: getEntriesByDate Method returning with {}", response);
        return response;
    }
}
