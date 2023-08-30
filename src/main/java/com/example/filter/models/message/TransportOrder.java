package com.example.filter.models.message;

import lombok.Value;
import lombok.With;

import java.util.Date;
import java.util.List;

@Value
@With
public class TransportOrder{
    public String borderoInternalItemID;
    public String itemCategoryCode;
    public Supplier supplier;
    public String borderoItemID;
    public String incoterm;
    public String incotermPlace;
    public String consigmentID;
    public GrossWeightTransportation grossWeightTransportation;
    public String loadingMeter;
    public NetWeight netWeight;
    public Volume volume;
    public Date deliveryDate;
    public Date pickupDate;
    public String originatorReference;
    public String generalInformation;
    public String specialTransportNumber;
    public String freightForwarderReference;
    public String meansOfTransport;
    public String aWBNo;
    public String trailerLicensePlate;
    public String goodsTypeOrig;
    public Receiver receiver;
    public Sender sender;
    public String unloadingpoint;
    public List<String> packages;
    public List<DeliveryNote> deliveryNotes;
}