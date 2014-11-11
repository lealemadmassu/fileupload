package com.ucando.exercise.fileupload.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.ucando.exercise.fileupload.domain.DownloadFile;
import com.ucando.exercise.fileupload.domain.FileMetadata;
import com.ucando.exercise.fileupload.domain.UploadedFile;
import com.ucando.exercise.fileupload.service.FileUploadService;

@Controller
@RequestMapping(value = "/fileupload")
@Slf4j
public class FileUploadController
{
    @Autowired
    private FileUploadService fileUploadService;

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public @ResponseBody FileMetadata handleFileUpload(@RequestParam(value = "file", required = true) MultipartFile file, @RequestParam(value = "name", required = true) String name,
            @RequestParam(value = "date", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date)
    {
        try
        {
            UploadedFile uploadedFile = new UploadedFile(file.getBytes(), new FileMetadata(null, file.getOriginalFilename(), name, date));
            fileUploadService.save(uploadedFile);

            return uploadedFile.getMetadata();
        }
        catch (Exception e)
        {
            log.error("Error found while uploading.", e);
        }
        return null;
    }

    @RequestMapping(value = "/file/{id}", method = RequestMethod.GET)
    public @ResponseBody HttpEntity<byte[]> getDocument(@PathVariable String id) throws IOException
    {
        DownloadFile downloadFile = fileUploadService.getFileContent(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set("content-disposition", "attachment; filename='" + downloadFile.getFileName() + "'");

        return new ResponseEntity<byte[]>(downloadFile.getFileData(), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/files", method = RequestMethod.GET)
    public @ResponseBody List<FileMetadata> filterByUploadDateAndUploaderName(
            @RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ") Date date, @RequestParam(value = "name", required = false) String name)
    {
        return fileUploadService.findFileMetadata(date, name);
    }
}
