package com.dhyasani.libraryeventkafkaapp.service;

import com.dhyasani.libraryeventkafkaapp.model.LibraryEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@Slf4j
public class LibraryEventProducer {

    @Autowired
    private KafkaTemplate<Integer,String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper ;

    public void sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {
        Integer key = libraryEvent.getLibraryEventId();
        String value = objectMapper.writeValueAsString(libraryEvent);
        ListenableFuture<SendResult<Integer,String>> future = kafkaTemplate.sendDefault(key, value);
        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable throwable) {
                handleFailure(key,value,throwable);
            }

            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                handleSuccess(key,value, result);
            }
        });

    }

    public SendResult<Integer, String> sendLibraryEventSynchronous(LibraryEvent libraryEvent) throws JsonProcessingException {
        Integer key = libraryEvent.getLibraryEventId();
        String value = objectMapper.writeValueAsString(libraryEvent);
        SendResult<Integer,String> result = null;
        try {
           result = kafkaTemplate.sendDefault(key, value).get(1, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("InterruptedException/ExecutionException/TimeoutException message : {} " , e.getMessage());
        } catch (Exception e) {
            log.error("Error sending message : {} " , e.getMessage());
            throw e;
        }
        return result;
    }

    public void sendLibraryEvent_Approach2(LibraryEvent libraryEvent) throws JsonProcessingException {
        Integer key = libraryEvent.getLibraryEventId();
        String value = objectMapper.writeValueAsString(libraryEvent);
        ProducerRecord<Integer,String> producerRecord = buildProducerRecord("library-events", key, value);
        ListenableFuture<SendResult<Integer,String>> future  = kafkaTemplate.send(producerRecord);
        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable throwable) {
                handleFailure(key,value,throwable);
            }

            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                handleSuccess(key,value, result);
            }
        });
    }

    private ProducerRecord<Integer,String> buildProducerRecord(String topic, Integer key, String value) {
        List<Header> recordHeaders = new ArrayList<>();
        recordHeaders.add(new RecordHeader("event-source","scanner".getBytes()));
        return new ProducerRecord<>(topic,null,null,key,value,recordHeaders);
    }

    private void handleFailure(Integer key, String value, Throwable ex) {
        log.error("Error sending message : {} " , ex.getMessage());

        try {
            throw ex;
        } catch (Throwable throwable) {
           log.error("Error in OnFailure: {} ", throwable.getMessage());
        }
    }

    private void handleSuccess(Integer key, String value, SendResult<Integer, String> result) {
        log.info("Message Sent successfully for the key : {} and value : {}, partition is {}",key,value,result.getRecordMetadata().partition());
    }
}
