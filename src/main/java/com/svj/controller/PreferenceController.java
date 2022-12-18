package com.svj.controller;

import com.svj.dto.PreferenceRequestDTO;
import com.svj.dto.PreferenceResponseDTO;
import com.svj.dto.ServiceResponse;
import com.svj.service.PreferenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.svj.utilities.JsonParser.jsonToString;

@RestController
@RequestMapping("/preference")
@Slf4j
public class PreferenceController {

    private PreferenceService preferenceService;

    @Autowired
    public PreferenceController(PreferenceService service){
        preferenceService = service;
    }

    @PostMapping
    public ServiceResponse addEntry(@RequestBody @Valid PreferenceRequestDTO requestDTO){
        log.info("JournalController: addEntry Starting method with payload {}", jsonToString(requestDTO));
        PreferenceResponseDTO responseDTO = preferenceService.addEntry(requestDTO);
        log.debug("JournalController: addEntry Response from service is {}", jsonToString(responseDTO));
        ServiceResponse response= new ServiceResponse(HttpStatus.CREATED, responseDTO, null);
        log.info("JournalController: addEntry Method returning with {} ", response);
        return response;
    }

    @PutMapping("/{id}")
    public ServiceResponse updateEntry(@PathVariable String id, @RequestBody @Valid PreferenceRequestDTO requestDTO){
        log.info("JournalController: updateEntry Starting method with id- {} and payload- {}", id, jsonToString(requestDTO));
        PreferenceResponseDTO responseDTO = preferenceService.updateEntry(id, requestDTO);
        log.debug("JournalController: updateEntry Response from service is {}", jsonToString(responseDTO));
        ServiceResponse response= new ServiceResponse(HttpStatus.OK, responseDTO, null);
        log.info("JournalController: updateEntry Method returning with {} ", response);
        return response;
    }

    @GetMapping("/{id}")
    public ServiceResponse getEntryWithID(@PathVariable String id){
        log.info("JournalController: getEntryWithID Starting method with id- {} ", id);
        PreferenceResponseDTO responseDTO = preferenceService.getEntryByID(id);
        log.debug("JournalController: getEntryWithID Response from service is {}", jsonToString(responseDTO));
        ServiceResponse response= new ServiceResponse(HttpStatus.OK, responseDTO, null);
        log.info("JournalController: getEntryWithID Method returning with {}", response);
        return response;
    }


    @GetMapping()
    public ServiceResponse getAllEntries(){
        log.info("JournalController: getAllEntries Starting method");
        List<PreferenceResponseDTO> allEntries = preferenceService.getAllEntries();
        log.debug("JournalController: getEntryWithID Response from service is {}", jsonToString(allEntries));
        ServiceResponse response= new ServiceResponse(HttpStatus.OK, allEntries, null);
        log.info("JournalController: getEntryWithID Method returning with {}", response);
        return response;
    }

    @DeleteMapping("/{id}")
    public ServiceResponse deleteByID(@PathVariable String id){
        log.info("JournalController: deleteByID Starting method with id- {}", id);
        String responseStr= preferenceService.deleteEntry(id);
        log.debug("JournalController: deleteByID Response from service is {}", responseStr);
        ServiceResponse response= new ServiceResponse(HttpStatus.NO_CONTENT, responseStr, null);
        log.info("JournalController: deleteByID Method returning with {}", response);
        return response;
    }

    @GetMapping("/date/{name}")
    public ServiceResponse getEntriesByName(@PathVariable String name){
        log.info("JournalController: getEntriesByName Starting method with date- {}", name);
        PreferenceResponseDTO preference = preferenceService.getEntriesByName(name);
        log.debug("JournalController: getEntriesByName Response from service is {}", jsonToString(preference));
        ServiceResponse response= new ServiceResponse(HttpStatus.OK, preference, null);
        log.info("JournalController: getEntriesByName Method returning with {}", response);
        return response;
    }
}
