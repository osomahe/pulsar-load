package net.osomahe.pl;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import picocli.CommandLine;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@QuarkusMain
@CommandLine.Command(name = "pulsar-load", mixinStandardHelpOptions = true)
public class PulsarLoadMain implements QuarkusApplication, Runnable {

    @CommandLine.Option(names = {"-u", "--url"}, description = "Pulsar url [default pulsar://localhost:6650]", defaultValue = "pulsar://localhost:6650")
    String pulsarUrl;

    @CommandLine.Option(names = {"-c", "--cert"}, description = "Path to authorization certificate")
    String certPath;

    @CommandLine.Option(names = {"-k", "--key"}, description = "Path to authorization key")
    String keyPath;

    @CommandLine.Option(names = {"-p", "--producer"}, description = "Producer name [default pulsar-load]", defaultValue = "pulsar-load")
    String producerName;

    @CommandLine.Option(names = {"-n", "--namespace"}, description = "namespace [default persistent://public/default] ", defaultValue = "persistent://public/default")
    String namespace;

    @CommandLine.Option(names = {"-t", "--topics"}, description = "Number of topics [default 16]", defaultValue = "16")
    String topicsCount;

    @CommandLine.Option(names = {"-m", "--messages"}, description = "Number of messages per topic [default 64] ", defaultValue = "64")
    String messagesPerTopicCount;

    @CommandLine.Option(names = {"-s", "--size"}, description = "Size of single message in bytes [default 1024] ", defaultValue = "1024")
    String messageSizeBytes;

    @CommandLine.Option(names = {"-x", "--threads"}, description = "Number of processing threads [default 4]", defaultValue = "4")
    String threadCount;

    @Inject
    GeneratorService serviceGenerator;

    @Inject
    WriterService serviceWriter;

    @Inject
    CommandLine.IFactory factory;

    @Override
    public int run(String... args) throws Exception {
        return new CommandLine(this, factory).execute(args);
    }

    @Override
    public void run() {
        Map<String, List<String>> messages = serviceGenerator.generateMessages();
        try {
            Thread.sleep(10_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        serviceWriter.writeMessages(messages);
    }
}
