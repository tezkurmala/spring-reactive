package tez.reactive.spring.lernreactive.functionalweb.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import tez.reactive.spring.lernreactive.functionalweb.handler.SampleHandlerFunction;

@Configuration
public class SampleRouterFunction {
    @Bean
    public RouterFunction<ServerResponse> route(SampleHandlerFunction handlerFunction) {
        return RouterFunctions
                .route(GET("/functional/flux")
                                        .and(accept(MediaType.APPLICATION_JSON)),
                        handlerFunction::flux)
                .andRoute(GET("/functional/mono")
                                .and(accept(MediaType.APPLICATION_JSON)),
                        handlerFunction::mono);
    }
}
