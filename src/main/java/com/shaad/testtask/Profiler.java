package com.shaad.testtask;

import co.paralleluniverse.fibers.Suspendable;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public class Profiler extends AbstractVerticle {
    @Override
    @Suspendable
    public void start() {
        vertx.eventBus().consumer("profileInfo", this::getProfile);
    }

    private void getProfile(Message<JsonObject> objectMessage) {
        JsonObject profileInfo = objectMessage.body();
        JsonObject searchResult = SearchUtil.getFirmRating(profileInfo.getString("id"), profileInfo.getString("hash"));
        vertx.eventBus().publish("searchResult", searchResult);
    }

}
