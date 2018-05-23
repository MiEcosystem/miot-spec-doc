package com.github.jxfengzi.xiot.provider.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class XiotProviderVerticle extends AbstractVerticle {

    private HttpServer server ;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Router router = Router.router(vertx);

        OperationHandler operation = new OperationHandler();
        router.route().handler(BodyHandler.create());
        router.route("/miot/operation").handler(operation::authorizeHandler);
        router.post("/miot/operation").handler(operation::operationHandler);

        server = vertx.createHttpServer();
        server.requestHandler(router::accept)
                .listen(9090,
                        result -> {
                            if (result.succeeded()) {
                                startFuture.complete();
                            } else {
                                startFuture.fail(result.cause());
                            }
                        });
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        server.close(stopFuture);
    }
}
