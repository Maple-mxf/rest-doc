package restdoc.core.schedule;

import com.fasterxml.jackson.databind.JsonNode;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The Scheduler class provided invoke handler
 *
 * @author ubuntu-m
 */
@Component
public class Scheduler implements CommandLineRunner {

    private static Logger log = LoggerFactory.getLogger(Scheduler.class);

    /**
     * The messageQueue receive message from remote client
     * <p>
     * BlockingQueue is thread block
     */
    private BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();

    /**
     * The consumers store consumer
     */
    private Set<Consumer<Message>> consumers = new CopyOnWriteArraySet<>();

    public void publish(byte[] message) {
        messageQueue.add(new String(message, CharsetUtil.UTF_8));
    }

    public void publish(String message) {
        messageQueue.add(message);
    }

    public void subscribe(Consumer<Message> consumer) {
        this.consumers.add(consumer);
    }

    public void unsubscribe(Consumer<Message> consumer) {
        this.consumers.remove(consumer);
    }


    @Override
    public void run(String... args) {
        new Thread(
                () -> {
                    while (true) {
                        try {
                            // Block the main thread
                            String message = messageQueue.take();
                            String identify = this.getMessageIdentify(null);

                            Consumer<Message> consumer = consumers.stream()
                                    .filter(c -> c.identify().equals(identify))
                                    .findFirst().orElse(null);

                            if (consumer == null) {
                                log.error("Not found consumer by identify in consumers collection");
                                continue;
                            }

                            // Callback the consumer registry
                            consumer.consume(consumer.convert(message));

                            // Auto unregistry consumer
                            this.unsubscribe(consumer);

                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                            log.error("Consume message occur an error , {} ", throwable.getMessage());
                        }
                    }

                }).start();
    }

    private String getMessageIdentify(JsonNode node) {
        return "";
    }
}
