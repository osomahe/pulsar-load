package net.osomahe.pl;

import org.jboss.logging.Logger;
import picocli.CommandLine;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class GeneratorService {

    @Inject
    Logger log;

    @Inject
    CommandLine.ParseResult parseResult;

    /**
     * topic name - messages
     *
     * @return
     */
    public Map<String, List<String>> generateMessages() {
        long start = System.currentTimeMillis();
        int size = Integer.valueOf(parseResult.matchedOptionValue("size", "1024"));
        int topicsCount = Integer.valueOf(parseResult.matchedOptionValue("topics", "16"));
        int messagesPerTopicCount = Integer.valueOf(parseResult.matchedOptionValue("messages", "64"));

        String namespace = parseResult.matchedOptionValue("namespace", "persistent://public/default");
        if (!namespace.endsWith("/")) {
            namespace += "/";
        }

        Map<String, List<String>> results = new HashMap<>(topicsCount);

        String prefix = getRandomString(8, 1).get(0) + "-";
        for (int i = 0; i < topicsCount; i++) {
            String topicName = prefix + getRandomString(16, 1).get(0);
            results.put(namespace + topicName, getRandomString(size, messagesPerTopicCount));
        }
        log.infof("Generation of %s messages in %s ms", topicsCount * messagesPerTopicCount, System.currentTimeMillis() - start);

        return results;
    }

    private List<String> getRandomString(int size, int count) {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        SecureRandom random = new SecureRandom();

        List<String> strings = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            strings.add(random.ints(leftLimit, rightLimit + 1)
                    .limit(size)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString());

        }
        return strings;
    }
}
