package com.shaad.testtask;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Searcher extends AbstractVerticle {
    private Logger logger = Logger.getLogger(Searcher.class.getName());

    @Override
    public void start() {
        vertx.eventBus().consumer("searchInfo", (this::search));
    }

    private void search(Message<JsonObject> message) {
        JsonObject searchRequest = message.body();
        HttpClient httpClient = vertx.createHttpClient();
        String query = null;
        try {
            query = SearchConstants.searchFirmURL +
                    "?key=" + SearchConstants.userApiKey
                    + "&version=" + SearchConstants.gisApiVersion
                    + "&where=" + searchRequest.getString("cityName")
                    + "&what=" + URLEncoder.encode(searchRequest.getString("firmType"), "UTF-8")
                    + "&sort=rating"
                    + "&pagesize=5";
        } catch (UnsupportedEncodingException e) {
            logger.log(Level.SEVERE, "Failed to urlEncode", e);
        }
        httpClient.getNow(80, SearchConstants.catalogURL, query, httpClientResponse -> httpClientResponse.bodyHandler(buffer -> {
            JsonObject object = new JsonObject(buffer.toString());
            Object objectArray = object.getValue("result");
            JsonObject profileInfo = new JsonObject();
            if (objectArray != null) {
                JsonArray resultArray = object.getJsonArray("result");
                if (resultArray.size() != 0) {
                    JsonObject maxRateInTownObject = resultArray.getJsonObject(0);
                    String hash = maxRateInTownObject.getString("hash");
                    String objID = maxRateInTownObject.getString("id");
                    profileInfo.put("hash", hash);
                    profileInfo.put("id", objID);
                }
                vertx.eventBus().publish("profileInfo", profileInfo);
            } else {
                vertx.eventBus().publish("searchResult", new JsonObject());
            }
        }));
    }
}
