package org.blockchain_innovation.factom.identiy.did.json;

import org.blockchain_innovation.factom.client.api.ops.StringUtils;

import javax.json.bind.adapter.JsonbAdapter;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import static java.util.Optional.ofNullable;

public class ValueEnumMapper<T extends Enum<?>> implements JsonbAdapter<T, String> {
    private final Map<String, T> jsonToJavaMapping = new HashMap<>();
    private final Map<T, String> javaToJsonMapping = new IdentityHashMap<>();
    private final Class<T> tClass;

    public ValueEnumMapper(Class<T> tClass) {
        this.tClass = tClass;
        fill();
    }

    @Override
    public String adaptToJson(final T obj) {
        return javaToJsonMapping.get(obj);
    }

    @Override
    public T adaptFromJson(final String obj) {
        return ofNullable(jsonToJavaMapping.get(obj))
                .orElseThrow(() -> new IllegalArgumentException("Unknown enum value: '" + obj + "'"));
    }

    private void fill() {
        for (T constant : getEnumType().getEnumConstants()) {
            String asString;
            try {
                asString = (String) constant.getClass().getDeclaredMethod("getValue").invoke(constant);
            } catch (final NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                asString = constant.name();
            }
            if (StringUtils.isEmpty(asString)) {
                asString = constant.name();
            }
            jsonToJavaMapping.put(asString, constant);
            javaToJsonMapping.put(constant, asString);
        }
    }

    private Class<T> getEnumType() {
        return tClass;
    }




}