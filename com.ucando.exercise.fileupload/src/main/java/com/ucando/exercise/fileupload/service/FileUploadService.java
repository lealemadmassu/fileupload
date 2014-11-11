package com.ucando.exercise.fileupload.service;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.ucando.exercise.fileupload.domain.DownloadFile;
import com.ucando.exercise.fileupload.domain.FileMetadata;
import com.ucando.exercise.fileupload.domain.UploadedFile;

@Service
@Slf4j
public class FileUploadService implements UploadService
{
    @Autowired
    GridFsOperations gridFsOperations;

    public void save(UploadedFile uploadedFile) throws FileNotFoundException, IOException
    {
        DBObject metaData = new BasicDBObject();
        metaData.put("uploaderName", uploadedFile.getMetadata().getUploaderName());
        metaData.put("uploadDate", uploadedFile.getMetadata().getUploadingDate().toString());

        gridFsOperations.store(new ByteArrayInputStream(uploadedFile.getFileData()), uploadedFile.getMetadata().getFileName(), null, metaData);

        log.info("Saved file {} to database.", uploadedFile.getMetadata().getFileName());
    }

    public DownloadFile getFileContent(String id) throws IOException
    {
        GridFSDBFile gfsDbFile = gridFsOperations.findOne(Query.query(Criteria.where("_id").is(new ObjectId(id))));

        log.info("Found a file with name: {}", gfsDbFile.getFilename());

        return new DownloadFile(IOUtils.toByteArray(gfsDbFile.getInputStream()), gfsDbFile.getFilename());
    }

    public List<FileMetadata> findByFileName(String fileName)
    {
        List<FileMetadata> metadatas = new ArrayList<FileMetadata>();

        List<GridFSDBFile> gfs = gridFsOperations.find(Query.query(Criteria.where("filename").regex(fileName)));
        for (GridFSDBFile gridFSDBFile : gfs)
        {
            gridFSDBFile.getFilename();
            Date uploadingDate = gridFSDBFile.getUploadDate();
            String uploaderName = (String) gridFSDBFile.getMetaData().get("uploaderName");

            metadatas.add(new FileMetadata(gridFSDBFile.getId().toString(), fileName, uploaderName, uploadingDate));
        }

        return metadatas;
    }

    public List<FileMetadata> findFileMetadata(Date uploadDate, String uploaderName)
    {
        List<FileMetadata> metadatas = new ArrayList<FileMetadata>();

        if (uploadDate != null && StringUtils.isNotBlank(uploaderName))
        {
            List<GridFSDBFile> find = gridFsOperations.find(Query.query(Criteria.where("metadata.uploaderName").regex(uploaderName).and("uploadDate").gte(uploadDate)));
            for (GridFSDBFile gridFSDBFile : find)
            {
                String fileName = gridFSDBFile.getFilename();
                Date uploadingDate = gridFSDBFile.getUploadDate();

                metadatas.add(new FileMetadata(gridFSDBFile.getId().toString(), fileName, uploaderName, uploadingDate));
            }
        }
        else if (uploadDate == null)
        {
            metadatas = findMetadataByUploaderName(uploaderName);
        }
        else
        {
            metadatas = findMetadataByUploadDate(uploadDate);
        }

        return metadatas;
    }

    public List<FileMetadata> findMetadataByUploaderName(String uploaderName)
    {
        if (StringUtils.isBlank(uploaderName))
        {
            return Collections.emptyList();
        }

        List<FileMetadata> metadatas = new ArrayList<FileMetadata>();

        List<GridFSDBFile> find = gridFsOperations.find(Query.query(Criteria.where("metadata.uploaderName").regex(uploaderName)));
        for (GridFSDBFile gridFSDBFile : find)
        {
            String fileName = gridFSDBFile.getFilename();
            Date uploadingDate = gridFSDBFile.getUploadDate();

            metadatas.add(new FileMetadata(gridFSDBFile.getId().toString(), fileName, uploaderName, uploadingDate));
        }
        return metadatas;
    }

    public List<FileMetadata> findMetadataByUploadDate(Date uploadDate)
    {
        if (uploadDate == null)
        {
            return Collections.emptyList();
        }

        List<FileMetadata> metadatas = new ArrayList<FileMetadata>();
        List<GridFSDBFile> files = gridFsOperations.find(Query.query(Criteria.where("uploadDate").gte(uploadDate)));
        for (GridFSDBFile gridFSDBFile : files)
        {
            String fileName = gridFSDBFile.getFilename();
            Date uploadingDate = gridFSDBFile.getUploadDate();
            String uploaderName = (String) gridFSDBFile.getMetaData().get("uploaderName");

            metadatas.add(new FileMetadata(gridFSDBFile.getId().toString(), fileName, uploaderName, uploadingDate));
        }

        return metadatas;
    }
}
