package com.example.filter.models.message;

import java.util.List;

import lombok.Value;
import lombok.With;

@Value
@With
public class DeliveryNote {
    public String deliveryNoteNo;
    public List<String> containerInfo;
}
