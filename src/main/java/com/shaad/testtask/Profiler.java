package com.shaad.testtask;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonObject;

public class Profiler extends AbstractVerticle {
    @Override
    public void start() {
        vertx.eventBus().consumer("profileInfo", this::getProfile);
    }

    private void getProfile(Message<JsonObject> objectMessage) {
        JsonObject profileInfo = objectMessage.body();
        HttpClient httpClient = vertx.createHttpClient();
        String query;
        query = SearchConstants.searchProfileURL +
                "?key=" + SearchConstants.userApiKey
                + "&version=" + SearchConstants.gisApiVersion
                + "&id=" + profileInfo.getString("id")
                + "&hash=" + profileInfo.getString("hash");
        httpClient.getNow(80, SearchConstants.catalogURL, query, httpClientResponse ->
                httpClientResponse.bodyHandler(buffer -> {
                            JsonObject response = new JsonObject(buffer.toString());
                            JsonObject result = new JsonObject();
                            result.put("name", response.getValue("name"));
                            result.put("address", response.getValue("city_name") + ", " + response.getValue("address"));
                            result.put("rating", response.getValue("rating"));
                            vertx.eventBus().publish("searchResult", result);
                        }
                ));
    }
}
