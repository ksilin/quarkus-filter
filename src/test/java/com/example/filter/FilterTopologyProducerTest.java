package com.example.filter;

import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.test.TestRecord;
import org.apache.kafka.common.header.Headers;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class FilterTopologyProducerTest {

    Logger log = Logger.getLogger(FilterTopologyProducerTest.class);

    FilterTopologyProducer topologyProducer = new FilterTopologyProducer();

    String inputTopicName = "unfiltered-source";
    String outputTopicName = "filtered-target";
    static final String filterString = "{\"transportOrder.receiver.code\":[\"MU807747\",\"MU807732\"], \"billingGroup\":[\"BAA\",\"BAM\",\"GVA\"]}";

    static final Properties props = new Properties();

    static String serializedValidMessage;
    static String serializedInvalidMessage;

        @BeforeAll
      static void init(){
            props.put(StreamsConfig.APPLICATION_ID_CONFIG, FilterTopologyProducerTest.class.getSimpleName());
            props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "test:1234");

            serializedValidMessage = MockPojoHelper.serializeToJsonString(MockPojoHelper.makeValidMessage());
            serializedInvalidMessage = MockPojoHelper.serializeToJsonString(MockPojoHelper.makeInvalidMessage());
        }

    @Test
    void testFilterMessage() {

        var topo = topologyProducer.createTopology(inputTopicName, outputTopicName, filterString);
        var testDriver = new TopologyTestDriver(topo, props);

        var inputTopic = testDriver.createInputTopic(inputTopicName, Serdes.String().serializer(), Serdes.String().serializer());
        var outputTopic = testDriver.createOutputTopic(outputTopicName, Serdes.String().deserializer(), Serdes.String().deserializer());
        inputTopic.pipeInput(serializedValidMessage);
        inputTopic.pipeInput(serializedInvalidMessage);

        var result = outputTopic.readValuesToList();

        assertEquals(result.size(), 1);

        assertEquals(result.get(0), serializedValidMessage);
    }

    @Test
    void testRetainHeaders() {

        var topo = topologyProducer.createTopology(inputTopicName, outputTopicName, filterString);
        var testDriver = new TopologyTestDriver(topo, props);

        var inputTopic = testDriver.createInputTopic(inputTopicName, Serdes.String().serializer(), Serdes.String().serializer());
        var outputTopic = testDriver.createOutputTopic(outputTopicName, Serdes.String().deserializer(), Serdes.String().deserializer());

        Headers headers = new RecordHeaders();
        headers.add("testHeaderKey", "testHeaderValue".getBytes(StandardCharsets.UTF_8));

        var testRecord = new TestRecord<>("1", serializedValidMessage, headers);

        inputTopic.pipeInput(testRecord);

        var resultRecord = outputTopic.readRecord();
        var resultHeaders = resultRecord.getHeaders();

        assertEquals(headers, resultHeaders);
    }
}
