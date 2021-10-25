package net.osomahe.pl;

import io.quarkus.runtime.ShutdownEvent;
import org.apache.pulsar.client.api.ClientBuilder;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.jboss.logging.Logger;
import picocli.CommandLine;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.Optional;

@ApplicationScoped
public class PulsarClientFactory {

    @Inject
    Logger log;


    @Inject
    CommandLine.ParseResult parseResult;

    PulsarClient pulsarClient;

    private void init() {
        String serviceUrl = parseResult.matchedOptionValue("url", "pulsar://localhost:6650");

        Optional<String> tlsCertFile = Optional.ofNullable(parseResult.matchedOptionValue("cert", null));

        Optional<String> tlsKeyFile = Optional.ofNullable(parseResult.matchedOptionValue("key", null));
        try {
            ClientBuilder clientBuilder = PulsarClient.builder().serviceUrl(serviceUrl).allowTlsInsecureConnection(true);
            if (tlsCertFile.isPresent() && tlsKeyFile.isPresent()) {
                clientBuilder = clientBuilder.authentication(
                        "org.apache.pulsar.client.impl.auth.AuthenticationTls",
                        "tlsCertFile:" + tlsCertFile.get() + ",tlsKeyFile:" + tlsKeyFile.get()
                );
            }

            this.pulsarClient = clientBuilder.build();
        } catch (PulsarClientException e) {
            log.error("Cannot create PulsarClient instance for service url: " + serviceUrl);
        }
    }

    @Produces
    public PulsarClient getPulsarClient() {
        if (this.pulsarClient == null) {
            init();
        }
        return this.pulsarClient;
    }

    void shutdown(@Observes ShutdownEvent event) {
        try {
            if (this.pulsarClient != null) {
                this.pulsarClient.close();
            }
        } catch (PulsarClientException e) {
            log.error("Error closing pulsar client", e);
        }
    }

}

