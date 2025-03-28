package com.example.util;

import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.*;
import org.apache.arrow.vector.ipc.ArrowFileReader;
import org.apache.arrow.vector.ipc.ArrowFileWriter;
import org.apache.arrow.vector.types.pojo.ArrowType;
import org.apache.arrow.vector.types.pojo.Field;
import org.apache.arrow.vector.types.pojo.FieldType;
import org.apache.arrow.vector.types.pojo.Schema;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class ArrowConverter {
    private static final int BATCH_SIZE = 1000;

    public static void convertToArrow(File csvFile, File arrowFile) throws IOException {
        try (BufferAllocator allocator = new RootAllocator()) {
            // 读取CSV文件头
            CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader()
                    .parse(new FileReader(csvFile));
            
            List<Field> fields = new ArrayList<>();
            for (String header : parser.getHeaderNames()) {
                fields.add(new Field(header, FieldType.nullable(new ArrowType.Utf8()), null));
            }

            // 创建VectorSchemaRoot
            VectorSchemaRoot root = VectorSchemaRoot.create(
                    new Schema(fields), allocator);

            // 创建Arrow文件写入器
            FileChannel channel = FileChannel.open(
                    arrowFile.toPath(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );

            try (ArrowFileWriter writer = new ArrowFileWriter(root, null, channel)) {
                writer.start();
                List<VarCharVector> vectors = new ArrayList<>();

                // 为每个列创建向量
                for (Field field : fields) {
                    VarCharVector vector = (VarCharVector) root.getVector(field.getName());
                    vectors.add(vector);
                    vector.allocateNew();
                }

                // 分批处理CSV记录
                int rowCount = 0;
                for (CSVRecord record : parser) {
                    if (rowCount >= BATCH_SIZE) {
                        root.setRowCount(rowCount);
                        writer.writeBatch();
                        rowCount = 0;
                        for (VarCharVector vector : vectors) {
                            vector.reset();
                        }
                    }

                    for (int i = 0; i < record.size(); i++) {
                        String value = record.get(i);
                        vectors.get(i).setSafe(rowCount, value.getBytes());
                    }
                    rowCount++;
                }

                // 写入最后一批数据
                if (rowCount > 0) {
                    root.setRowCount(rowCount);
                    writer.writeBatch();
                }
            }
            root.close();
        }
    }

    public static VectorSchemaRoot readArrowFile(File arrowFile) throws IOException {
        BufferAllocator allocator = new RootAllocator();
        FileChannel channel = FileChannel.open(arrowFile.toPath(), StandardOpenOption.READ);
        ArrowFileReader reader = new ArrowFileReader(channel, allocator);
        
        reader.loadNextBatch();
        return reader.getVectorSchemaRoot();
    }

    public static Map<String, Object> extractDataWithSampling(VectorSchemaRoot root, int samplingRate) {
        Map<String, Object> result = new HashMap<>();
        List<String> fieldNames = new ArrayList<>();
        List<List<Object>> data = new ArrayList<>();

        for (Field field : root.getSchema().getFields()) {
            fieldNames.add(field.getName());
        }

        int rowCount = root.getRowCount();
        for (int i = 0; i < rowCount; i += samplingRate) {
            List<Object> row = new ArrayList<>();
            for (FieldVector vector : root.getFieldVectors()) {
                if (vector instanceof VarCharVector) {
                    byte[] value = ((VarCharVector) vector).get(i);
                    row.add(value != null ? new String(value) : null);
                }
            }
            data.add(row);
        }

        result.put("fields", fieldNames);
        result.put("data", data);
        return result;
    }

    public static Map<String, Object> extractSchema(VectorSchemaRoot root) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, String>> fields = new ArrayList<>();

        for (Field field : root.getSchema().getFields()) {
            Map<String, String> fieldInfo = new HashMap<>();
            fieldInfo.put("name", field.getName());
            fieldInfo.put("type", field.getType().toString());
            fields.add(fieldInfo);
        }

        result.put("fields", fields);
        result.put("rowCount", root.getRowCount());
        return result;
    }
}