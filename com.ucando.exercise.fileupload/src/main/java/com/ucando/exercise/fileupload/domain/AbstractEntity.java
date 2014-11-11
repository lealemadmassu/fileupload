package com.ucando.exercise.fileupload.domain;

import lombok.Data;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
public class AbstractEntity
{
    @Id
    @JsonIgnore
    private String id;
}
