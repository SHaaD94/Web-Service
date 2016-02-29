package com.shaad.testtask;

import co.paralleluniverse.fibers.Suspendable;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;


public class Searcher extends AbstractVerticle {
    @Override
    @Suspendable
    public void start() {
        vertx.eventBus().consumer("searchInfo", this::search);
    }

    private void search(Message<JsonObject> message) {
        JsonObject searchRequest = message.body();
        JsonObject profileInfo = SearchUtil.searchFirmInTown(searchRequest.getString("firmType"), searchRequest.getString("cityName"));
        vertx.eventBus().publish("profileInfo", profileInfo);
    }
}
