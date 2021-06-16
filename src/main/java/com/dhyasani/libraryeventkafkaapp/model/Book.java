package com.dhyasani.libraryeventkafkaapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @NotNull
    private Integer bookId;
    @NotBlank
    private String bookName;
    private String author;
}
