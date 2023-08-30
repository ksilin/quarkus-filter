package com.example.filter.models.message;

import lombok.Value;
import lombok.With;

@Value
@With
public class Receiver {
    public String code;
    public int number;
    public String name;
    public String street;
    public String postalCode;
    public String location;
    public String countryCode;
}
