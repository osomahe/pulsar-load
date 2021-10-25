package net.osomahe.pl;


import org.apache.pulsar.client.api.*;
import org.jboss.logging.Logger;
import picocli.CommandLine;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@ApplicationScoped
public class WriterService {

    @Inject
    Logger log;

    @Inject
    PulsarClient pulsarClient;

    @Inject
    CommandLine.ParseResult parseResult;

    ExecutorService executor;
    String producerName;

    @PostConstruct
    private void init() {
        int threads = Integer.valueOf(parseResult.matchedOptionValue("threads", "4"));
        executor = Executors.newFixedThreadPool(threads);
        producerName = parseResult.matchedOptionValue("producer", "pulsar-load");
    }

    public void writeMessages(Map<String, List<String>> messages) {
        long start = System.currentTimeMillis();
        var results = new ArrayList<Future<?>>();
        int msgCount = 0;
        for (Map.Entry<String, List<String>> entry : messages.entrySet()) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> writeTopic(entry.getKey(), entry.getValue()), executor);
            results.add(future);
            msgCount += entry.getValue().size();
        }
        try {
            CompletableFuture.allOf(results.toArray(new CompletableFuture[results.size()])).get();
        } catch (Exception e) {
            log.error("Error in wait", e);
        }
        long timeMs = System.currentTimeMillis() - start;
        log.infof("Writing speed %.2f msg/sec => %s messages in %s ms", msgCount / (timeMs / 1000.0), msgCount, timeMs);
    }

    private void writeTopic(String topicName, List<String> messages) {
        try (Producer<String> producer = pulsarClient.newProducer(Schema.STRING)
                .compressionType(CompressionType.LZ4)
                .sendTimeout(10, TimeUnit.SECONDS)
                .producerName(producerName)
                .enableBatching(true)
                .topic(topicName)
                .create()) {
            for (String msg : messages) {
                producer.send(msg);
            }
        } catch (PulsarClientException e) {
            log.error("Writing of messages failed topic: " + topicName, e);
        }
    }
}
