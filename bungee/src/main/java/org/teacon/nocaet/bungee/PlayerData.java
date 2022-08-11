package org.teacon.nocaet.bungee;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.md_5.bungee.api.config.ServerInfo;
import org.teacon.nocaet.bungee.network.Registry;
import org.teacon.nocaet.bungee.network.packet.SyncProgress;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PlayerData {

    private static final int TARGET = 10000;

    private static final Gson GSON = new GsonBuilder()
        .registerTypeAdapter(Claim.class, new Claim.Serializer()).disableHtmlEscaping().setPrettyPrinting().create();

    private final Map<String, Map<UUID, Set<Claim>>> map = new ConcurrentHashMap<>();
    private final Map<String, Integer> progress = new ConcurrentHashMap<>();

    public Map<UUID, Set<Claim>> getGroup(String group) {
        var map = this.map.get(group);
        return map == null ? Map.of() : map;
    }

    public void add(ServerInfo info, UUID uuid, String name) {
        var group = ServerGroup.instance().getGroup(info);
        if (group != null) {
            var map = this.map.computeIfAbsent(group, k -> new ConcurrentHashMap<>());
            map.computeIfAbsent(uuid, k -> Collections.newSetFromMap(new ConcurrentHashMap<>())).add(new Claim(name, Instant.now()));
        }
    }

    public List<String> getAll(ServerInfo info, UUID uuid) {
        var group = ServerGroup.instance().getGroup(info);
        if (group != null) {
            var map = this.map.computeIfAbsent(group, k -> new ConcurrentHashMap<>());
            var set = map.get(uuid);
            return set == null ? null : set.stream().map(Claim::name).toList();
        } else return null;
    }

    @SuppressWarnings("UnstableApiUsage")
    public void load(Path path) throws IOException {
        this.map.clear();
        this.progress.clear();
        for (var group : ServerGroup.instance().getGroups()) {
            var data = path.resolve("data_" + group + ".json");
            if (!Files.exists(data)) continue;
            try (var reader = Files.newBufferedReader(data)) {
                var map = this.map.computeIfAbsent(group, k -> new ConcurrentHashMap<>());
                var obj = JsonParser.parseReader(reader).getAsJsonObject();
                for (var s : obj.keySet()) {
                    var set = Collections.<Claim>newSetFromMap(new ConcurrentHashMap<>());
                    map.put(UUID.fromString(s), set);
                    set.addAll(GSON.fromJson(obj.get(s), new TypeToken<List<Claim>>() {}.getType()));
                }
                this.progress.put(group, map.values().stream().mapToInt(Set::size).sum());
            }
        }
    }

    public void save(Path path) throws IOException {
        for (var group : this.map.keySet()) {
            var map = this.map.get(group);
            var s = GSON.toJson(map.entrySet().stream().collect(Collectors.toMap(it -> it.getKey().toString(), Map.Entry::getValue)));
            var oldData = path.resolve("data_" + group + "_old.json");
            var newData = path.resolve("data_" + group + ".json");
            if (Files.exists(newData)) {
                Files.move(newData, oldData, StandardCopyOption.REPLACE_EXISTING);
            }
            Files.writeString(newData, s, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        }
    }

    public void updateProgress(ServerInfo server) {
        var group = ServerGroup.instance().getGroup(server);
        if (group != null) {
            var result = this.progress.compute(group, (k, v) -> v == null ? 1 : v + 1);
            var progress = Math.min(result.floatValue() / TARGET, 1F);
            var packet = new SyncProgress(progress);
            for (ServerInfo info : ServerGroup.instance().getServers(server)) {
                Registry.instance().sendToClient(packet, info);
            }
        }
    }

    public Optional<Float> getProgress(ServerInfo server) {
        return Optional.ofNullable(ServerGroup.instance().getGroup(server))
            .map(this.progress::get)
            .map(it -> it.floatValue() / TARGET);
    }

    public record Claim(String name, Instant instant) {

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Claim claim = (Claim) o;
            return Objects.equals(name, claim.name);
        }

        @Override
        public int hashCode() {
            return name != null ? name.hashCode() : 0;
        }

        static class Serializer implements JsonSerializer<Claim>, JsonDeserializer<Claim> {

            @Override
            public JsonElement serialize(Claim src, Type typeOfSrc, JsonSerializationContext context) {
                var object = new JsonObject();
                object.addProperty("name", src.name);
                object.addProperty("time", src.instant.toString());
                return object;
            }

            @Override
            public Claim deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                var object = json.getAsJsonObject();
                var name = object.get("name").getAsString();
                var instant = Instant.parse(object.get("time").getAsString());
                return new Claim(name, instant);
            }
        }
    }
}
