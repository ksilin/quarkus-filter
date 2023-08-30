package com.example.filter.models.message;

import lombok.Value;
import lombok.With;

@Value
@With
public class Volume {
    public double value;
    public String unitCode;
}
