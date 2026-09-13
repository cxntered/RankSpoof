package dev.cxntered.rankspoof.config;

import net.ornithemc.osl.config.api.config.Config;
import net.ornithemc.osl.config.api.config.option.Option;
import net.ornithemc.osl.config.api.config.option.group.OptionGroup;
import net.ornithemc.osl.config.api.serdes.SerializationSettings;
import net.ornithemc.osl.config.api.serdes.config.ConfigSerializer;
import net.ornithemc.osl.config.api.serdes.config.option.JsonOptionSerializer;
import net.ornithemc.osl.config.api.serdes.config.option.JsonOptionSerializers;
import net.ornithemc.osl.core.api.json.JsonFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FlatJsonConfigSerializer implements ConfigSerializer<JsonFile> {
    private static final Map<Class<?>, Map<Option, String>> FIELD_NAME_CACHE = new ConcurrentHashMap<>();

    @Override
    public void serialize(Config config, SerializationSettings settings, JsonFile json) throws IOException {
        Map<Option, String> fieldNames = getFieldNames(config);

        try (json) {
            json.write();

            for (OptionGroup group : config.getGroups()) {
                for (Option option : group.getOptions()) {
                    String key = fieldNames.getOrDefault(option, option.getName());
                    json.writeName(key);
                    serializeOption(option, settings, json);
                }
            }
        }
    }

    @Override
    public void deserialize(Config config, SerializationSettings settings, JsonFile json) throws IOException {
        Map<String, Option> optionsByKey = buildOptionsByKeyMap(config);

        try (json) {
            json.read();

            while (json.hasNext()) {
                String key = json.readName();
                Option option = optionsByKey.get(key);

                if (option == null) {
                    json.skipValue();
                } else {
                    deserializeOption(option, settings, json);
                }
            }
        }
    }

    private static Map<Option, String> getFieldNames(Config config) {
        return FIELD_NAME_CACHE.computeIfAbsent(config.getClass(), FlatJsonConfigSerializer::buildFieldNameMap);
    }

    private static Map<String, Option> buildOptionsByKeyMap(Config config) {
        Map<Option, String> fieldNames = getFieldNames(config);
        Map<String, Option> optionsByKey = new HashMap<>();

        for (OptionGroup group : config.getGroups()) {
            for (Option option : group.getOptions()) {
                String key = fieldNames.getOrDefault(option, option.getName());
                optionsByKey.put(key, option);
            }
        }
        return optionsByKey;
    }

    private static Map<Option, String> buildFieldNameMap(Class<?> clazz) {
        Map<Option, String> map = new IdentityHashMap<>();

        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers())) continue;
                if (!Option.class.isAssignableFrom(field.getType())) continue;

                try {
                    field.setAccessible(true);
                    Option option = (Option) field.get(null);

                    if (option != null) {
                        map.putIfAbsent(option, field.getName());
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to read option field: " + field.getName() + " in class " + clazz.getName(), e);
                }
            }
            clazz = clazz.getSuperclass();
        }

        return map;
    }

    private <O extends Option> void serializeOption(O option, SerializationSettings settings, JsonFile json) throws IOException {
        JsonOptionSerializer<O> serializer = JsonOptionSerializers.get(option.getClass());
        if (serializer == null) {
            throw new IOException("don't know how to serialize option " + option);
        } else {
            serializer.serialize(option, settings, json);
        }
    }

    private <O extends Option> void deserializeOption(O option, SerializationSettings settings, JsonFile json) throws IOException {
        JsonOptionSerializer<O> serializer = JsonOptionSerializers.get(option.getClass());
        if (serializer == null) {
            throw new IOException("don't know how to deserialize option " + option);
        } else {
            serializer.deserialize(option, settings, json);
        }
    }
}
