package com.shaad.testtask;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

public class Runner {
    public static void main(String[] args) {
        final Vertx vertx = Vertx.vertx();

        vertx.deployVerticle(new HttpServer(), new DeploymentOptions().setHa(true));
        vertx.deployVerticle(new Searcher(), new DeploymentOptions().setHa(true));
        vertx.deployVerticle(new Profiler(), new DeploymentOptions().setHa(true));
    }
}
