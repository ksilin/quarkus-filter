package com.example.filter.models.message;

import lombok.Value;
import lombok.With;

@Value
@With
public class Supplier{
    public String code;
    public String name;
    public String street;
    public String postalCode;
    public String location;
    public String countryCode;
}
