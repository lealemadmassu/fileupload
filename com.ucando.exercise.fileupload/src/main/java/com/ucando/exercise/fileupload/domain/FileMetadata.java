package com.ucando.exercise.fileupload.domain;

import java.util.Date;

import lombok.Data;

@Data
public class FileMetadata
{
    private final String id;
    private final String fileName;
    private final String uploaderName;
    private final Date   uploadingDate;
}
