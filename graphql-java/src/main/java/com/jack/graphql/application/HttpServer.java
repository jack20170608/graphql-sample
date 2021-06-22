package com.jack.graphql.application;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.jack.graphql.App;
import com.jack.graphql.cache.OrderCacheImpl;
import com.jack.graphql.interfaces.api.OperationHandler;
import com.jack.graphql.service.OrderService;
import io.muserver.HttpsConfigBuilder;
import io.muserver.MuServer;
import io.muserver.MuServerBuilder;
import io.muserver.handlers.ResourceHandlerBuilder;
import io.muserver.openapi.OpenAPIObjectBuilder;
import io.muserver.rest.RestHandlerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import static io.muserver.ContextHandlerBuilder.context;
import static io.muserver.openapi.ExternalDocumentationObjectBuilder.externalDocumentationObject;
import static io.muserver.openapi.InfoObjectBuilder.infoObject;

public class HttpServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);

    public static MuServer startMuServer() throws Exception {
        AppContext appContext = App.APP_CONTEXT;
        String appName = appContext.getConfig().getString("server.app-name");

        HttpsConfigBuilder sslContext = HttpsConfigBuilder.httpsConfig()
            .withKeystoreType("JKS")
            .withKeystorePassword("123456")
            .withKeyPassword("123456")
            .withKeystore(new File("C:\\Users\\27528\\Desktop\\tool\\jack007-top-tomcat-0613204832.jks"))
            .withCipherFilter((s, d) -> d);

        int port = appContext.getConfig().getInt("server.port");
        LOGGER.info("Starting the server on port {}..", port);
        long start = System.currentTimeMillis();

        MuServer muServer = MuServerBuilder.httpServer()
            .withHttpsPort(port)
            .withIdleTimeout(2, TimeUnit.HOURS)
            .withHttpsConfig(sslContext)
            .addHandler(context(appName)
                .addHandler(createRestHandler(appContext))
                .addHandler(ResourceHandlerBuilder.classpathHandler("/web/swagger-ui"))
            )
            .start();
        LOGGER.info("Mu Server stared in {} ms.", System.currentTimeMillis() - start);
        return muServer;
    }

    private static RestHandlerBuilder createRestHandler(AppContext appContext) {
        OrderService orderService = appContext.getOrderService();
        OrderCacheImpl orderCache = appContext.getOrderCache();
        OperationHandler operationHandler = new OperationHandler(orderService, orderCache);
        return RestHandlerBuilder
            .restHandler(operationHandler)
            .addCustomWriter(new JacksonJaxbJsonProvider())
            .addCustomReader(new JacksonJaxbJsonProvider())
            .withOpenApiHtmlUrl("/api.html")
            .withOpenApiJsonUrl("/openapi.json")
            .withOpenApiDocument(
                OpenAPIObjectBuilder.openAPIObject()
                    .withInfo(
                        infoObject()
                            .withTitle("User API document")
                            .withDescription("This is a sample api for the graphql sample")
                            .withVersion("1.0")
                            .build())
                    .withExternalDocs(
                        externalDocumentationObject()
                            .withDescription("Documentation docs")
                            .withUrl(URI.create("https//muserver.io/jaxrs"))
                            .build()
                    )
            );
    }

}
