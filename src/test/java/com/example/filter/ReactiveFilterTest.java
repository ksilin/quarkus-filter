package com.example.filter;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySink;
import io.smallrye.reactive.messaging.memory.InMemorySource;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@QuarkusTestResource(KafkaTestResourceLifecycleManager.class)
public class ReactiveFilterTest {

    Logger log = Logger.getLogger(ReactiveFilterTest.class);

    @Inject
    @Any
    InMemoryConnector connector;

    static final String incomingChannelName = "unfiltered-incoming";
    static final String outgoingChannelName = "filtered-outgoing";

    static final String serializedValidMessage = MockPojoHelper.serializeToJsonString(MockPojoHelper.makeValidMessage());
    static final String serializedInvalidMessage = MockPojoHelper.serializeToJsonString(MockPojoHelper.makeInvalidMessage());

    @Test
    void test() {
        InMemorySource<String> source = connector.source(incomingChannelName);
        source.send(serializedValidMessage);
        source.send(serializedInvalidMessage);

        InMemorySink<String> sink = connector.sink(outgoingChannelName);
        List<? extends Message<String>> received = sink.received();
        assertEquals(1, received.size());
        var validMsgString = received.get(0).getPayload();
        assertEquals(serializedValidMessage, validMsgString);
    }

}
