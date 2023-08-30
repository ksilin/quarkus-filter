package com.example.filter;

import com.example.filter.models.PipelineConfig;
import com.example.filter.models.StreamConfig;
import com.example.filter.utils.ConfigLoader;
import com.example.filter.utils.MessageFilterHelper;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.jboss.logging.Logger;
//import org.slf4j.impl.Slf4jLogger;

import java.util.List;

@Startup
@ApplicationScoped
public class FilterTopologyProducer {

    Logger log = Logger.getLogger(FilterTopologyProducer.class);

    @Inject
    @Dependent
    ConfigLoader loader;
    private List<StreamConfig> streamConfigs;

    @PostConstruct
    public void init() {
        this.streamConfigs = loader.loadConfigs();
    }

    @Produces
    public Topology produceFilterTopology() {

        log.info("stream configs");
        streamConfigs.forEach(System.out::println);

        // PipelineConfig pipelineConfig = streamConfigs.stream().filter(c -> c.getConsumer().get("system").equals("test")).findFirst().get().getPipelines().stream().filter(p -> p.getName() == "test").findFirst().get();
        List<PipelineConfig> pipelines = streamConfigs.stream().map(StreamConfig::getPipelines).flatMap(List::stream).toList();

        log.info("pipelines configs");
        pipelines.forEach(System.out::println);

        PipelineConfig pipelineConfig = pipelines.stream().filter(p -> p.getName().equals("test")).findFirst().get();

        log.infov("pipeline config: {0}", pipelineConfig);

        return createTopology(pipelineConfig.getSourceTopic(), pipelineConfig.getDestinationTopic(), pipelineConfig.getFilterCriteria());
    }

    public Topology createTopology(String sourceTopic, String destinationTopic, String filterCriteria){
        StreamsBuilder builder = new StreamsBuilder();

        builder.stream(sourceTopic, Consumed.with(Serdes.String(), Serdes.String()))
                .peek((k, v) -> log.infov("validating message {0}:{1}", k, v))
                .filter((k, v) -> MessageFilterHelper.isMessageValid(v, filterCriteria))
                .peek((k, v) -> log.infov("retained valid message {0}:{1}", k, v))
                .to(destinationTopic, Produced.with(Serdes.String(), Serdes.String()));

        return builder.build();
    }

}
