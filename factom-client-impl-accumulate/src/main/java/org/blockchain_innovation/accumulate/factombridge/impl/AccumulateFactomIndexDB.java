package org.blockchain_innovation.accumulate.factombridge.impl;

import org.blockchain_innovation.accumulate.factombridge.model.DbKey;
import org.blockchain_innovation.accumulate.factombridge.model.IndexDB;
import org.blockchain_innovation.factom.client.api.json.JsonConverter;
import org.blockchain_innovation.factom.client.api.log.LogFactory;
import org.blockchain_innovation.factom.client.api.log.Logger;
import org.blockchain_innovation.factom.client.api.settings.RpcSettings;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AccumulateFactomIndexDB {

    private static final Duration TIME_OUT = Duration.of(10, ChronoUnit.SECONDS);

    private static final Logger logger = LogFactory.getLogger(AccumulateFactomIndexDB.class);

    private final HttpClient client = HttpClient.newHttpClient();
    private final HttpResponse.BodyHandler<byte[]> responseBodyHandler = HttpResponse.BodyHandlers.ofByteArray();
    private URI uri;
    private final JsonConverter jsonConverter = JsonConverter.Provider.newInstance();

    byte[] get(final IndexDB db, final DbKey dbKey) throws KeyNotFoundException {
        final URI requestUri = uri.resolve(String.format("/%s?key=base64(%s)", db.getValue(), dbKey.getBase64Key()));
        final var request = HttpRequest.newBuilder()
                .uri(requestUri)
                .timeout(TIME_OUT)
                .GET()
                .build();
        try {
            final HttpResponse<byte[]> response = client.send(request, responseBodyHandler);
            if (response.statusCode() != 200) {
                final String message = new String(response.body(), StandardCharsets.UTF_8);
                if (message.equals("leveldb: not found")) {
                    throw new KeyNotFoundException(message);
                }
                throw new RuntimeException(message);
            }
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    List<byte[]> getAll(final IndexDB db, final DbKey dbKey, final Integer cutPosition) throws KeyNotFoundException {
        if (uri == null) {
            throw new RuntimeException("IndexDBSettings configured!");
        }

        final String base64Key = dbKey.getBase64Key();
        final String base64ToKey = dbKey.getBase64ToKey();
        final URI requestUri = uri.resolve(String.format("/%s/range?fromKey=base64(%s)&toKey=base64(%s)&cutPosition=%d",
                db.getValue(), base64Key, base64ToKey, cutPosition));
        logger.info("getAll request URI: " + requestUri);
        final var request = HttpRequest.newBuilder()
                .uri(requestUri)
                .header("Accept", "application/json")
                .timeout(TIME_OUT)
                .GET()
                .build();
        try {
            final HttpResponse<byte[]> response = client.send(request, responseBodyHandler);
            if (response.statusCode() != 200) {
                final String message = new String(response.body(), StandardCharsets.UTF_8);
                if (message.equals("leveldb: not found")) {
                    throw new KeyNotFoundException(message);
                }
                throw new RuntimeException(message);
            }
            final Map<String, String> stringMap = jsonConverter.getStringMap(new String(response.body(), StandardCharsets.UTF_8));
            return stringMap.keySet().stream()
                    .map(base64Value -> Base64.getDecoder().decode(base64Value))
                    .collect(Collectors.toList());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    void put(final IndexDB db, final DbKey dbKey, final byte[] value) {
        final URI requestUri = uri.resolve(String.format("/%s?key=base64(%s)", db.getValue(), dbKey.getBase64Key()));
        final var request = HttpRequest.newBuilder()
                .uri(requestUri)
                .timeout(TIME_OUT)
                .PUT(HttpRequest.BodyPublishers.ofByteArray(value))
                .build();
        try {
            final HttpResponse<byte[]> response = client.send(request, responseBodyHandler);
            if (response.statusCode() != 200) {
                final String message = new String(response.body(), StandardCharsets.UTF_8);
                throw new RuntimeException(message);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }


    void configure(final RpcSettings settings) {
        switch (settings.getSubSystem()) {
            case LEVELDBSERVER:
                try {
                    this.uri = settings.getServer().getURL().toURI();
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            case FACTOMD:
            case WALLETD:
                break;
        }
    }
}
