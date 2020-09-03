package restdoc.web.core.schedule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import restdoc.remoting.ClientChannelInfo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Deprecated
public class ClientManager implements CommandLineRunner {

    private final Map<String, ClientChannelInfo> clients = new ConcurrentHashMap<>();

    @Autowired
    public ClientManager(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public void registerClient(String id, ClientChannelInfo clientChannelInfo) {
        clients.putIfAbsent(id, clientChannelInfo);
    }

    public void unregisterClient(String id) {
        clients.remove(id);
    }

    public ClientChannelInfo findClient(String id) {
        return clients.get(id);
    }

    public List<ClientChannelInfo> list() {
        return new ArrayList<>(this.clients.values());
    }

    final
    ObjectMapper mapper;

    @Override
    public void run(String... args) throws Exception {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    System.err.println(String.format("clients: %s", mapper.writeValueAsString(clients)));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }, 1000L,5000L);
    }
}
