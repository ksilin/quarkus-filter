package com.example.filter.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@With
public class StreamConfig {

    private Map<String, Object> consumer;
    private Map<String, Object> producer;
    private List<PipelineConfig> pipelines;

}