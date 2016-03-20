package com.shaad.testtask;

import co.paralleluniverse.fibers.Suspendable;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sync.HandlerReceiverAdaptor;
import io.vertx.ext.sync.SyncVerticle;

import java.util.ArrayList;
import java.util.Collections;

import static io.vertx.ext.sync.Sync.fiberHandler;
import static io.vertx.ext.sync.Sync.streamAdaptor;

public class HttpServer extends SyncVerticle {

    public HttpServer() {
    }

    @Suspendable
    public void start() {
        System.out.println("started");
        vertx.createHttpServer()
                .requestHandler(fiberHandler(this::handleRequests)
                ).listen(8080);
    }

    @Suspendable
    private void handleRequests(HttpServerRequest httpServerRequest) {
        JsonObject resultJsonObject = new JsonObject();
        String firmType = httpServerRequest.getParam("firmtype");
        if (null != firmType) {
            for (City city : City.values()) {
                JsonObject searchRequest = new JsonObject();
                searchRequest.put("cityName", city.getCityName());
                searchRequest.put("firmType", firmType);
                vertx.eventBus().publish("searchInfo", searchRequest);
            }

            ArrayList<JsonObject> resultList = new ArrayList<>();

            HandlerReceiverAdaptor<Message<JsonObject>> adaptor = streamAdaptor();
            vertx.eventBus().<JsonObject>consumer("searchResult").handler(adaptor);
            for (int i = 0; i < 5; i++) {
                Message<JsonObject> received = adaptor.receive();
                JsonObject searchedFirm = received.body();
                if (searchedFirm.getValue("name") != null && searchedFirm.getString("rating") != null
                        && !searchedFirm.getString("rating").equals("0")) {
                    resultList.add(received.body());
                }
            }

            if (!resultList.isEmpty()) {
                Collections.sort(resultList, (firstSearchObject, secondSearchObject) ->
                        -1 * firstSearchObject.getString("rating").compareTo(secondSearchObject.getString("rating")));
                JsonArray array = new JsonArray();
                resultList.forEach(array::add);

                resultJsonObject.put("result", array);
            } else {
                resultJsonObject.put("error", "nothing found");
            }
        } else {
            resultJsonObject.put("error", "firmType is not specified");
        }
        httpServerRequest.response().end(resultJsonObject.toString());
    }
}
