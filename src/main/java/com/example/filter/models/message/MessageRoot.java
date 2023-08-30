package com.example.filter.models.message;

import lombok.Value;
import lombok.With;

import java.util.Date;
import java.util.List;

@Value
@With
public class MessageRoot {
    public String actionCode;
    public int testCode;
    public boolean testBoolean;
    public Date bookingDate;
    public String borderoNumber;
    public Date departureDate;
    public Date expectedArrivalDate;
    public String licensePlate;
    public String senderBusinessSystemID;
    public String carrierNumber;
    public String carrierName;
    public String transportationPlanningPoint;
    public String typeOfFreightOrder;
    public Date transportDate;
    public String relation;
    public String billingGroup;
    public String truckTypeOrig;
    public String forwarderOU;
    public List<TransportOrder> transportOrder;
}