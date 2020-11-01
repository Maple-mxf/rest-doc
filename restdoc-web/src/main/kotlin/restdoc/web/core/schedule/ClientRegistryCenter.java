package restdoc.web.core.schedule;


import io.netty.channel.Channel;
import org.springframework.stereotype.Component;
import restdoc.remoting.common.ApplicationClientInfo;
import restdoc.remoting.common.ApplicationType;
import restdoc.remoting.common.ExposedAPI;
import restdoc.web.util.MD5Util;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Maple
 * @since 2020/10/30
 */
@Component
public class ClientRegistryCenter {

    /**
     * Key is client id is encode string {@link restdoc.web.util.MD5Util#MD5Encode(String, String)}
     */
    private final Map<String, ApplicationClientInfo> clients = new ConcurrentHashMap<>();

    /**
     * Key is client id is encode string {@link restdoc.web.util.MD5Util#MD5Encode(String, String)}
     */
    private final Map<String, ConcurrentHashMap<ApplicationType, List<ExposedAPI>>> apiTable = new ConcurrentHashMap<>();


    /**
     * registry client
     *
     * @param remote remote address {@link Channel#remoteAddress()}
     * @param aci    client application info
     */
    public void registryClient(String remote, ApplicationClientInfo aci) {
        clients.put(MD5Util.MD5Encode(remote, StandardCharsets.UTF_8.name()), aci);
    }

    /**
     * unregistry client
     *
     * @param remote remote address {@link Channel#remoteAddress()}
     */
    public void unregistryClient(String remote) {
        String key = MD5Util.MD5Encode(remote, StandardCharsets.UTF_8.name());
        clients.remove(key);
        apiTable.remove(key);
    }
}
