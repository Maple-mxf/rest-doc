package restdoc.web.core.schedule;

import io.netty.channel.Channel;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import restdoc.remoting.annotation.Nullable;
import restdoc.remoting.common.ApplicationClientInfo;
import restdoc.remoting.common.ApplicationType;
import restdoc.remoting.common.ApiDescriptor;
import restdoc.web.util.MD5Util;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * The {@link ClientRegistryCenter} class provided client container and client's api container. Cache the temp data
 * <p>
 * If client disconnect from server. The ClientRegistryCenter class will be remove client data(contain client's remote
 * information and client's api)
 * <p>
 * The {@link ClientRegistryCenter} class must be threadsafe and single instance(manager by spring container)
 *
 * @author Maple
 * @since 2020/10/30
 */
@Component
public class ClientRegistryCenter implements CommandLineRunner {

    /**
     * Key is client id is encode string {@link restdoc.web.util.MD5Util#MD5Encode(String, String)}
     */
    private final Map<String, ApplicationClientInfo> clients = new ConcurrentHashMap<>();

    /**
     * Key is client id is encode string {@link restdoc.web.util.MD5Util#MD5Encode(String, String)}
     */
    private final Map<String, Set<Api>> apiTable = new ConcurrentHashMap<>();


    /**
     * registry client
     *
     * @param remote remote address {@link Channel#remoteAddress()}
     * @param aci    client application info
     */
    public void registryClient(String remote, ApplicationClientInfo aci) {
        synchronized (this) {
            clients.put(MD5Util.MD5Encode(remote, StandardCharsets.UTF_8.name()), aci);
        }
    }

    /**
     * unregistry client
     *
     * @param remote remote address {@link Channel#remoteAddress()}
     */
    public void unregistryClient(String remote) {
        String key = MD5Util.MD5Encode(remote, StandardCharsets.UTF_8.name());
        synchronized (this) {
            clients.remove(key);
            apiTable.remove(key);
        }
    }


    /**
     * @param remote remote remote address {@link Channel#remoteAddress()}
     */
    public void registryAPI(String remote, ApplicationType at, String service, List<ApiDescriptor> apiList) {
        String key = MD5Util.MD5Encode(remote, StandardCharsets.UTF_8.name());

        synchronized (this) {
            // 0 Check
            if (!clients.containsKey(key)) throw new RuntimeException(String.format("Client %s not exist", remote));

            // 1 Add
            Set<Api> apiSet = apiTable.getOrDefault(key, new HashSet<>());

            Api api = new Api();
            api.setAt(at);
            api.setApiList(apiList);
            api.setService(service);

            apiSet.add(api);

            // 2 Registry
            apiTable.putIfAbsent(key, apiSet);
        }
    }

    public void unregistryAPI(String remote) {
        String key = MD5Util.MD5Encode(remote, StandardCharsets.UTF_8.name());
        synchronized (this) {
            apiTable.remove(key);
        }
    }

    @Nullable
    public ApplicationClientInfo getByRemote(String remote) {
        String key = MD5Util.MD5Encode(remote, StandardCharsets.UTF_8.name());
        return this.clients.get(key);
    }

    @Nullable
    public ApplicationClientInfo get(String key) {
        return this.clients.get(key);
    }

    @NonNull
    public List<ApplicationClientInfo> getMulti(Collection<String> keys) {
        return keys.stream()
                .map(clients::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * @return client key collection
     */
    public Set<String> getClientKeysFilterApplicationType(ApplicationType at) {
        return apiTable.entrySet()
                .stream()
                .filter(t -> t.getValue().stream().anyMatch(p -> at.equals(p.getAt())))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public int getClientNum() {
        return this.clients.size();
    }

    /**
     * @return exposedAPI collection
     */
    public Collection<ApiDescriptor> getExposedAPIFilterApplicationTypeByRemote(String remote, ApplicationType at) {
        String key = MD5Util.MD5Encode(remote, StandardCharsets.UTF_8.name());
        return getExposedAPIFilterApplicationType(key, at);
    }

    /**
     * @return exposedAPI collection
     */
    public Collection<ApiDescriptor> getExposedAPIFilterApplicationType(String clientId, ApplicationType at) {
        Set<Api> apiSet = this.apiTable.get(clientId);
        return apiSet
                .stream()
                .filter(t -> t.getAt().equals(at))
                .flatMap(t -> t.getApiList().stream())
                .collect(Collectors.toList());
    }

    /**
     * Deprecated 存在侵入性
     *
     * @return exposedAPI collection
     */
    @Deprecated
    public Collection<ApiDescriptor> getExposedAPIFilterService(String service) {
        return this.apiTable.values().stream()
                .flatMap(Collection::stream)
                .filter(t -> t.getService().equals(service))
                .flatMap(t -> t.getApiList().stream())
                .collect(Collectors.toList());
    }

    @Override
    public void run(String... args) throws Exception {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                System.err.println(clients);
                System.err.println(apiTable);
            }
        }, 1000L, 5000L);
    }

}
