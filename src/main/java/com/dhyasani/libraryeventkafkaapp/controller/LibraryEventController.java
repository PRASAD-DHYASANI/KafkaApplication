package com.dhyasani.libraryeventkafkaapp.controller;

import com.dhyasani.libraryeventkafkaapp.model.LibraryEvent;
import com.dhyasani.libraryeventkafkaapp.model.LibraryEventType;
import com.dhyasani.libraryeventkafkaapp.service.LibraryEventProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Slf4j
public class LibraryEventController {

    @Autowired
    private LibraryEventProducer libraryEventProducer;

    @PostMapping("/v1/library-event")
    public ResponseEntity<LibraryEvent> postLibraryEvent(@RequestBody @Valid  LibraryEvent libraryEvent) throws JsonProcessingException {

        log.info("Before invoking kafka producer");
        libraryEvent.setLibraryEventType(LibraryEventType.NEW);
        //libraryEventProducer.sendLibraryEvent(libraryEvent);
        //libraryEventProducer.sendLibraryEventSynchronous(libraryEvent);
        libraryEventProducer.sendLibraryEvent_Approach2(libraryEvent);
        log.info("After invoking kafka producer");
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

    @PutMapping("/v1/library-event")
    public ResponseEntity<?> updateLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent) throws JsonProcessingException {

        if(libraryEvent.getLibraryEventId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please provide Library Event Id");
        }
        log.info("Before invoking kafka producer");
        libraryEvent.setLibraryEventType(LibraryEventType.UPDATE);
        //libraryEventProducer.sendLibraryEvent(libraryEvent);
        //libraryEventProducer.sendLibraryEventSynchronous(libraryEvent);
        libraryEventProducer.sendLibraryEvent_Approach2(libraryEvent);
        log.info("After invoking kafka producer");
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }
}
