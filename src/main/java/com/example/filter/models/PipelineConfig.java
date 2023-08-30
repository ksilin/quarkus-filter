package com.example.filter.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PipelineConfig {

    private String name;
    private String sourceTopic;
    private String filterCriteria;
    private String destinationTopic;
    private String messageId;
}
