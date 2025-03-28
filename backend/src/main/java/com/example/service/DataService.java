package com.example.service;

import com.example.util.ArrowConverter;
import com.example.vo.DataResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.arrow.vector.VectorSchemaRoot;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class DataService {
    private static final String UPLOAD_DIR = "uploads";
    private static final String ARROW_DIR = "arrow_files";

    public DataService() {
        createDirectories();
    }

    private void createDirectories() {
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
            Files.createDirectories(Paths.get(ARROW_DIR));
        } catch (IOException e) {
            log.error("创建目录失败", e);
            throw new RuntimeException("无法创建必要的目录", e);
        }
    }

    public String processAndStoreFile(MultipartFile file) {
        try {
            String fileId = UUID.randomUUID().toString();
            String originalFilename = file.getOriginalFilename();
            String extension = FilenameUtils.getExtension(originalFilename);
            
            // 保存原始文件
            Path uploadPath = Paths.get(UPLOAD_DIR, fileId + "." + extension);
            Files.copy(file.getInputStream(), uploadPath);

            // 转换为 Arrow 格式
            Path arrowPath = Paths.get(ARROW_DIR, fileId + ".arrow");
            ArrowConverter.convertToArrow(uploadPath.toFile(), arrowPath.toFile());

            return fileId;
        } catch (Exception e) {
            log.error("处理文件失败", e);
            throw new RuntimeException("文件处理失败", e);
        }
    }

    public DataResponse getDataWithSampling(String fileId, int samplingRate) {
        try {
            File arrowFile = Paths.get(ARROW_DIR, fileId + ".arrow").toFile();
            if (!arrowFile.exists()) {
                throw new RuntimeException("文件不存在");
            }

            VectorSchemaRoot root = ArrowConverter.readArrowFile(arrowFile);
            DataResponse response = new DataResponse();
            response.setData(ArrowConverter.extractDataWithSampling(root, samplingRate));
            root.close();

            return response;
        } catch (Exception e) {
            log.error("读取数据失败", e);
            throw new RuntimeException("数据读取失败", e);
        }
    }

    public DataResponse getDataInfo(String fileId) {
        try {
            File arrowFile = Paths.get(ARROW_DIR, fileId + ".arrow").toFile();
            if (!arrowFile.exists()) {
                throw new RuntimeException("文件不存在");
            }

            VectorSchemaRoot root = ArrowConverter.readArrowFile(arrowFile);
            DataResponse response = new DataResponse();
            response.setSchema(ArrowConverter.extractSchema(root));
            root.close();

            return response;
        } catch (Exception e) {
            log.error("读取数据信息失败", e);
            throw new RuntimeException("数据信息读取失败", e);
        }
    }
}