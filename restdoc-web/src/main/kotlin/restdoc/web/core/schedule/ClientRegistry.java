package restdoc.web.core.schedule;


import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Maple
 * @since 2020/10/30
 */
@Component
public class ClientRegistry {

    /**
     * HOST info
     * API  info
     */
    private final Map<String, Object> clients = new ConcurrentHashMap<>(10);
}
