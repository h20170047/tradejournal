package com.svj.controller;

import com.svj.dto.ServiceResponse;
import com.svj.dto.TradeEntryRequestDTO;
import com.svj.dto.TradeEntryResponseDTO;
import com.svj.service.JournalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

import static com.svj.utilities.AppUtils.dateFormatter;
import static com.svj.utilities.JsonParser.jsonToString;

@RestController
@RequestMapping("/trade-journal")
@Slf4j
public class JournalController {

    private JournalService journalService;

    @Autowired
    public JournalController(JournalService service){
        journalService = service;
    }

    @PostMapping
    public ServiceResponse addEntry(@RequestBody @Valid TradeEntryRequestDTO requestDTO){
        log.info("JournalController: addEntry Starting method with payload {}", jsonToString(requestDTO));
        TradeEntryResponseDTO responseDTO = journalService.addEntry(requestDTO);
        log.debug("JournalController: addEntry Response from service is {}", jsonToString(responseDTO));
        ServiceResponse response= new ServiceResponse(HttpStatus.CREATED, responseDTO, null);
        log.info("JournalController: addEntry Method returning with {} ", response);
        return response;
    }

    @PutMapping("/{id}")
    public ServiceResponse updateEntry(@PathVariable String id, @RequestBody @Valid TradeEntryRequestDTO requestDTO){
        log.info("JournalController: updateEntry Starting method with id- {} and payload- {}", id, jsonToString(requestDTO));
        TradeEntryResponseDTO responseDTO = journalService.updateEntry(id, requestDTO);
        log.debug("JournalController: updateEntry Response from service is {}", jsonToString(responseDTO));
        ServiceResponse response= new ServiceResponse(HttpStatus.OK, responseDTO, null);
        log.info("JournalController: updateEntry Method returning with {} ", response);
        return response;
    }

    @GetMapping("/{id}")
    public ServiceResponse getEntryWithID(@PathVariable String id){
        log.info("JournalController: getEntryWithID Starting method with id- {} ", id);
        TradeEntryResponseDTO responseDTO = journalService.getEntryByID(id);
        log.debug("JournalController: getEntryWithID Response from service is {}", jsonToString(responseDTO));
        ServiceResponse response= new ServiceResponse(HttpStatus.OK, responseDTO, null);
        log.info("JournalController: getEntryWithID Method returning with {}", response);
        return response;
    }


    @GetMapping()
    public ServiceResponse getAllEntries(){
        log.info("JournalController: getAllEntries Starting method");
        List<TradeEntryResponseDTO> allEntries = journalService.getAllEntries();
        log.debug("JournalController: getEntryWithID Response from service is {}", jsonToString(allEntries));
        ServiceResponse response= new ServiceResponse(HttpStatus.OK, allEntries, null);
        log.info("JournalController: getEntryWithID Method returning with {}", response);
        return response;
    }

    @DeleteMapping("/{id}")
    public ServiceResponse deleteByID(@PathVariable String id){
        log.info("JournalController: deleteByID Starting method with id- {}", id);
        String responseStr= journalService.deleteEntry(id);
        log.debug("JournalController: deleteByID Response from service is {}", responseStr);
        ServiceResponse response= new ServiceResponse(HttpStatus.NO_CONTENT, responseStr, null);
        log.info("JournalController: deleteByID Method returning with {}", response);
        return response;
    }

    @GetMapping("/entries/{fromDate}/{toDate}")
    public ServiceResponse getEntriesBetweenDates(@RequestParam String traderName,@PathVariable String fromDate, @PathVariable String toDate){
        log.info("JournalController: getEntriesBetweenDates for trader: {},  Starting method between dates- {}, {}", traderName, fromDate, toDate);
        List<TradeEntryResponseDTO> allEntries = journalService.getEntriesBetweenDates(traderName, LocalDate.parse(fromDate,dateFormatter), LocalDate.parse(toDate,dateFormatter));
        log.debug("JournalController: getEntriesBetweenDates Response from service is {}", jsonToString(allEntries));
        ServiceResponse response= new ServiceResponse(HttpStatus.OK, allEntries, null);
        log.info("JournalController: getEntriesBetweenDates Method returning with {}", response);
        return response;
    }

    @GetMapping("/entries/report/week")
    public ServiceResponse getEntriesForWeek(@RequestParam String traderName){
        log.info("JournalController: getEntriesForWeek Starting method for week {}", LocalDate.now());
        List<TradeEntryResponseDTO> allEntries = journalService.getEntriesBetweenDates(traderName, LocalDate.parse(LocalDate.now().plusDays(-(7+2)).toString(),dateFormatter), LocalDate.parse(LocalDate.now().toString(),dateFormatter));
        log.debug("JournalController: getEntriesForWeek Response from service is {}", jsonToString(allEntries));
        ServiceResponse response= new ServiceResponse(HttpStatus.OK, allEntries, null);
        log.info("JournalController: getEntriesForWeek Method returning with {}", response);
        return response;
    }

    @GetMapping("/entries/report/month")
    public ServiceResponse getEntriesForMonth(@RequestParam String traderName){
        log.info("JournalController: getEntriesForMonth Starting method for week {}", LocalDate.now());
        List<TradeEntryResponseDTO> allEntries = journalService.getEntriesBetweenDates(traderName, LocalDate.parse(LocalDate.now().plusDays(-(30+(2*4))).toString(),dateFormatter), LocalDate.parse(LocalDate.now().toString(),dateFormatter));
        log.debug("JournalController: getEntriesForMonth Response from service is {}", jsonToString(allEntries));
        ServiceResponse response= new ServiceResponse(HttpStatus.OK, allEntries, null);
        log.info("JournalController: getEntriesForMonth Method returning with {}", response);
        return response;
    }
}
