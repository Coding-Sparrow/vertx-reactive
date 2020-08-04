package com.dell.demo;

import io.vertx.core.file.OpenOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.parsetools.RecordParser;
import io.vertx.reactivex.ext.web.Router;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {


        Router router = Router.router(vertx);

        router.get("/test").handler(getHandler -> {
            getHandler.response().end("Request Accepted!");
            vertx.fileSystem()
                    .rxOpen("data.txt", new OpenOptions())
                    .flatMapObservable(af -> RecordParser.newDelimited("/n/r", af).toObservable())
                    .map(Buffer::toString)
                    .filter(name -> name.startsWith("R"))
                    .take(2)
                    .subscribe(data -> System.out.println("Data : " + data), err -> System.out.println(err), () -> System.out.println("Done"));

        });

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(8080, httpListener -> {
                    if (httpListener.succeeded()) {
                        System.out.println("Server started");

                    } else {
                        System.out.println("server startup failed " + httpListener.cause());
                    }
                });
    }

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(new MainVerticle());
    }

}