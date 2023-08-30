package com.example.filter;

import com.example.filter.models.message.MessageRoot;
import com.example.filter.models.message.Receiver;
import com.example.filter.models.message.TransportOrder;
import io.quarkus.kafka.client.serialization.ObjectMapperSerde;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class MockPojoHelper {

    private static final PodamFactory factory = new PodamFactoryImpl();
    static final ObjectMapperSerde<MessageRoot> serde = new ObjectMapperSerde<>(MessageRoot.class);

    static public String VALID_RECEIVER_STRING = "MU807747";
    static public String VALID_BILLING_GROUP_STRING = "BAA";

    public static MessageRoot makeInvalidMessage() {
        return factory.manufacturePojoWithFullData(MessageRoot.class);
    }

        public static MessageRoot makeValidMessage(){
        MessageRoot invalidMessage = makeInvalidMessage();

        var msgValidBillGroup = replaceBillingGroup( invalidMessage, VALID_BILLING_GROUP_STRING);
        return replaceTransportOrder(msgValidBillGroup, List.of(makeValidTransportOrder()));
    }

    public static MessageRoot replaceBillingGroup(MessageRoot message, String billingGroup){
        return message.withBillingGroup(billingGroup);
    }

    public static MessageRoot replaceTransportOrder(MessageRoot message, List<TransportOrder> transportOrder){
        return message.withTransportOrder(transportOrder);
    }

    public static TransportOrder makeValidTransportOrder(){
        var order = factory.manufacturePojo(TransportOrder.class);
        var receiver = makeValidReceiver();
        return order.withReceiver(receiver);
    }

    public static Receiver makeValidReceiver(){
        var receiver = factory.manufacturePojo(Receiver.class);
        return receiver.withCode(VALID_RECEIVER_STRING);
    }

    static String serializeToJsonString(MessageRoot msg){
        var serializedValid = serde.serializer().serialize("NO_TOPIC", msg);
        return new String(serializedValid, StandardCharsets.UTF_8);
    }

    static MessageRoot deserializeFromJsonString(String msgJsonString){
        return serde.deserializer().deserialize("NO_TOPIC", msgJsonString.getBytes(StandardCharsets.UTF_8));
    }

}
