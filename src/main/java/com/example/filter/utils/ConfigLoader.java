package com.example.filter.utils;

import com.example.filter.models.PipelineConfig;
import com.example.filter.models.StreamConfig;
import com.typesafe.config.*;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
//import org.slf4j.impl.Slf4jLogger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Startup
@ApplicationScoped
public class ConfigLoader {

    @Inject
    Logger log;

    private static final String STREAMING_CONFIG = "streaming.conf";
    private static final String CONFIG_PIPELINES = "pipelines";
    private static final String CONFIG_SYSTEMS = "systems";
    private static final String GENERAL = "general";
    private static final String CONSUMER = "consumer";
    private static final String PRODUCER = "producer";
    private static final String GENERAL_SYSTEM_PATH = "%s.general.%s";
    private static final String CONSUMER_SYSTEM_PATH = "%s.consumer.%s";
    private static final String PRODUCER_SYSTEM_PATH = "%s.producer.%s";
    private static final String SOURCE_TOPIC = "%s.source.topic";
    private static final String SOURCE_SYSTEM = "%s.source.system";
    private static final String DESTINATION_SYSTEM = "%s.destination.system";
    private static final String DESTINATION_TOPIC = "%s.destination.topic";
    private static final String FILTER = "%s.filter_criteria";

    private Config config;
    private Config pipelines;

    @PostConstruct
    public void load() {
        this.config = ConfigFactory.load();
        log.infov("kafka streaming - configuration loaded: [{0}]", this.config.root()
                .render(ConfigRenderOptions.concise()));
        System.lineSeparator();
        log.info("---------------------------------------------------------------------------------------------------");
        System.lineSeparator();
        this.pipelines = config.getObject(CONFIG_PIPELINES).toConfig();
    }

    public List<StreamConfig> loadConfigs() {
        final List<String> pipelinesNames = pipelines.entrySet().stream().map(p -> p.getKey().split("\\.")[0])
                .distinct().collect(Collectors.toList());

        return pipelinesNames.stream().map(n -> {
            final PipelineConfig pipeline = getPipelineConfigInstance(n);
            final String sourceSystem = pipelines.getString(String.format(SOURCE_SYSTEM, n));
            final String destinationSystem = pipelines.getString(String.format(DESTINATION_SYSTEM, n));
            final Config systemsConfig = config.getObject(CONFIG_SYSTEMS).toConfig();

            final Map<String, Object> consumerConfig = new HashMap<>();
            consumerConfig.put("system", sourceSystem);
            final Map<String, Object> producerConfig = new HashMap<>();
            producerConfig.put("system", destinationSystem);

            systemsConfig.entrySet().stream().filter(s -> s.getKey().startsWith(sourceSystem)).forEach(s -> {
                final String key;
                final String path;
                if (s.getKey().contains(GENERAL)) {
                    key = getSystemKey(sourceSystem, GENERAL, s);
                    path = getSystemPath(GENERAL_SYSTEM_PATH, sourceSystem, key);
                    consumerConfig.put(key, systemsConfig.getString(path));
                } else if (s.getKey().contains(CONSUMER)) {
                    key = getSystemKey(sourceSystem, CONSUMER, s);
                    path = getSystemPath(CONSUMER_SYSTEM_PATH, sourceSystem, key);
                    consumerConfig.put(key, systemsConfig.getString(path));
                }
            });

            systemsConfig.entrySet().stream().filter(s -> s.getKey().startsWith(destinationSystem)).forEach(s -> {
                final String key;
                final String path;
                if (s.getKey().contains(GENERAL)) {
                    key = getSystemKey(destinationSystem, GENERAL, s);
                    path = getSystemPath(GENERAL_SYSTEM_PATH, destinationSystem, key);
                    producerConfig.put(key, systemsConfig.getString(path));
                } else if (s.getKey().contains(PRODUCER)) {
                    key = getSystemKey(destinationSystem, PRODUCER, s);
                    path = getSystemPath(PRODUCER_SYSTEM_PATH, destinationSystem, key);
                    producerConfig.put(key, systemsConfig.getString(path));
                }
            });

            return new StreamConfig(consumerConfig, producerConfig, List.of(pipeline));
        }).collect(Collectors.toList());
    }

    private static String getSystemPath(String format, String system, String key) {
        return String.format(format, system, key);
    }

    private PipelineConfig getPipelineConfigInstance(String name) {
        String sourceTopic = pipelines.getString(String.format(SOURCE_TOPIC, name));
        String destinationTopic = pipelines.getString(String.format(DESTINATION_TOPIC, name));
        String filter = pipelines.getString(String.format(FILTER, name));
        return new PipelineConfig(name, sourceTopic, filter, destinationTopic, "messageId");
    }

    private static String getSystemKey(String system, String key, Map.Entry<String, ConfigValue> s) {
        switch (key) {
            case GENERAL:
                return generateSystemKey(system, s, GENERAL);
            case CONSUMER:
                return generateSystemKey(system, s, CONSUMER);
            case PRODUCER:
                return generateSystemKey(system, s, PRODUCER);
            default:
                throw new IllegalArgumentException("Failed to load configs - Invalid system parent key: " + system);
        }
    }

    private static String generateSystemKey(String system, Map.Entry<String, ConfigValue> s, String format) {
        return Arrays.stream(s.getKey().split(String.format("%s.%s.", system, format)))
                .filter(k -> !k.isEmpty()).collect(Collectors.joining());
    }
}
