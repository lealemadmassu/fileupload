package com.ucando.exercise.fileupload.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UploadedFile extends AbstractEntity
{
    private final byte[]       fileData;
    private final FileMetadata metadata;
}
