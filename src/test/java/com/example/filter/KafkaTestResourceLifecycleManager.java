package com.example.filter;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;

import java.util.HashMap;
import java.util.Map;

public class KafkaTestResourceLifecycleManager implements QuarkusTestResourceLifecycleManager {

    static final String incomingChannelName = "unfiltered-incoming";
    static final String outgoingChannelName = "filtered-outgoing";

    @Override
    public Map<String, String> start() {
        Map<String, String> env = new HashMap<>();
        Map<String, String> props1 = InMemoryConnector.switchIncomingChannelsToInMemory(incomingChannelName);
        Map<String, String> props2 = InMemoryConnector.switchOutgoingChannelsToInMemory(outgoingChannelName);
        env.putAll(props1);
        env.putAll(props2);
        return env;
    }

    @Override
    public void stop() {
        InMemoryConnector.clear();
    }
}
