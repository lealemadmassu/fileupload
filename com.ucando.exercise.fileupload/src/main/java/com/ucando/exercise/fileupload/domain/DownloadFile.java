package com.ucando.exercise.fileupload.domain;

import lombok.Data;

@Data
public class DownloadFile
{
    private final byte[] fileData;
    private final String fileName;
}
