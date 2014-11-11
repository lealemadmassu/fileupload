package com.ucando.exercise.fileupload.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.core.io.FileSystemResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.ucando.exercise.fileupload.domain.FileMetadata;
import com.ucando.exercise.fileupload.domain.UploadedFile;

public class Client
{
    private FileMetadata doSave(UploadedFile document) throws IOException, FileNotFoundException
    {
        String path = writeDocumentToTempFile(document);
        MultiValueMap<String, Object> file = createMultipartFileParam(path);
        FileMetadata documentMetadata = new RestTemplate().postForObject("http://localhost:8080/archive/upload?person={name}&date={date}",
                file,
                FileMetadata.class,
                document.getMetadata().getUploaderName(),
                document.getMetadata().getUploadingDate().toString());
        return documentMetadata;
    }

    private MultiValueMap<String, Object> createMultipartFileParam(String tempFilePath)
    {
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
        parts.add("file", new FileSystemResource(tempFilePath));
        return parts;
    }

    private String writeDocumentToTempFile(UploadedFile document) throws IOException, FileNotFoundException
    {
        Path path;
        path = Files.createTempDirectory(document.getMetadata().getFileName());
        String tempDirPath = path.toString();
        File file = new File(tempDirPath, document.getMetadata().getFileName());
        FileOutputStream fo = new FileOutputStream(file);
        fo.write(document.getFileData());
        fo.close();
        return file.getPath();
    }
}
