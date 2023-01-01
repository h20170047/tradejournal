package com.svj.controller;

import com.svj.dto.ServiceResponse;
import com.svj.dto.TradeEntryRequestDTO;
import com.svj.dto.TradeEntryResponseDTO;
import com.svj.entity.TradeEntry;
import com.svj.entity.TradeStats;
import com.svj.service.JournalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

import static com.svj.utilities.AppUtils.*;
import static com.svj.utilities.JsonParser.jsonToString;

@RestController
@RequestMapping("/trade-journal")
@Slf4j
public class JournalController {

    private JournalService journalService;
    private final String defaultInputFilePath;

    @Autowired
    public JournalController(JournalService service, @Value("${input.filePath}") String defaultInputFilePath){
        journalService = service;
        this.defaultInputFilePath = defaultInputFilePath;
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

    @PostMapping("/bulk-add")
    public ServiceResponse bulkAddEntries(@RequestParam(required = false) String filePath, @RequestParam(required = false, defaultValue = "Swaraj") String traderName){
        if(filePath== null)
            filePath= defaultInputFilePath;
        log.info("JournalController: bulkAddEntries Starting method with payload {}", filePath);
        List<TradeEntryResponseDTO> responseDTOS = journalService.bulkAddEntries(filePath, traderName);
        log.debug("JournalController: bulkAddEntries Response from service is {}", jsonToString(responseDTOS));
        ServiceResponse response= new ServiceResponse(HttpStatus.CREATED, responseDTOS, null);
        log.info("JournalController: bulkAddEntries Method returning with {} ", response);
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
    public ResponseEntity<Resource> getEntriesBetweenDates(@RequestParam(required = false, defaultValue = "Swaraj") String traderName, @PathVariable String fromDate, @PathVariable String toDate){
        log.info("JournalController: getEntriesBetweenDates for trader: {},  Starting method between dates- {}, {}", traderName, fromDate, toDate);
        LocalDate from = LocalDate.parse(fromDate, dateFormatter);
        LocalDate to = LocalDate.parse(toDate, dateFormatter);
        List<TradeEntryResponseDTO> allEntries = journalService.getEntriesBetweenDates(traderName, from, to);
        TradeStats tradeStats = journalService.computeStats(allEntries, from, to);
        log.debug("JournalController: getEntriesBetweenDates Response from service is entries-{}, stats-{}", jsonToString(allEntries), jsonToString(tradeStats));
        InputStreamResource file = new InputStreamResource(generateReport(allEntries, tradeStats));
        log.info("JournalController: getEntriesBetweenDates Method returning with file");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=REPORT_".concat(fromDate).concat("-").concat(toDate) + ".csv")
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(file);
    }

    @GetMapping("/entries/report/week")
    public ResponseEntity<Resource> getEntriesForWeek(@RequestParam(required = false, defaultValue = "Swaraj") String traderName){
        log.info("JournalController: getEntriesForWeek Starting method for week {}", LocalDate.now());
        List<TradeEntryResponseDTO> allEntries = journalService.getEntriesBetweenDates(traderName, LocalDate.parse(LocalDate.now().plusDays(-(7)).toString(),dateFormatter3), LocalDate.parse(LocalDate.now().toString(),dateFormatter3));
        TradeStats tradeStats = journalService.computeStats(allEntries, LocalDate.parse(LocalDate.now().plusDays(-(7+2)).toString(),dateFormatter3), LocalDate.parse(LocalDate.now().toString(),dateFormatter3));
        log.debug("JournalController: getEntriesForWeek Response from service is entries-{}, stats-{}", jsonToString(allEntries), jsonToString(tradeStats));
        InputStreamResource file = new InputStreamResource(generateReport(allEntries, tradeStats));
        log.info("JournalController: getEntriesForWeek Method returning with file");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=REPORT_WEEKLY".concat(LocalDate.parse(LocalDate.now().plusDays(-(7)).toString(),dateFormatter3).toString()) + ".csv")
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(file);
    }

    @GetMapping("/entries/report/month")
    public ResponseEntity<Resource> getEntriesForMonth(@RequestParam(required = false, defaultValue = "Swaraj") String traderName){
        log.info("JournalController: getEntriesForMonth Starting method for week {}", LocalDate.now());
        List<TradeEntryResponseDTO> allEntries = journalService.getEntriesBetweenDates(traderName, LocalDate.parse(LocalDate.now().plusDays(-(30)).toString(),dateFormatter3), LocalDate.parse(LocalDate.now().toString(),dateFormatter3));
        TradeStats tradeStats = journalService.computeStats(allEntries, LocalDate.parse(LocalDate.now().plusDays(-(7+2)).toString(),dateFormatter3), LocalDate.parse(LocalDate.now().toString(),dateFormatter3));
        log.debug("JournalController: getEntriesForWeek Response from service is entries-{}, stats-{}", jsonToString(allEntries), jsonToString(tradeStats));
        InputStreamResource file = new InputStreamResource(generateReport(allEntries, tradeStats));
        log.info("JournalController: getEntriesForWeek Method returning with file");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=REPORT_MONTHLY".concat(LocalDate.parse(LocalDate.now().plusDays(-(30)).toString(),dateFormatter3).toString()) + ".csv")
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(file);
    }
}
