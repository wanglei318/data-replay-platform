package com.example.vo;

import lombok.Data;
import java.util.Map;

@Data
public class DataResponse {
    private Map<String, Object> data;
    private Map<String, Object> schema;
}