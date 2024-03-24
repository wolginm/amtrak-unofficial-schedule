package com.markwolgin.amtrak.schedulegenerator.configuration;

import com.markwolgin.amtrak.schedulegenerator.properties.AmtrakClientProperties;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class WebClientConfiguration {

    @Bean("AmtrakDataWebClient")
    public WebClient amtrakDataWebClient(final AmtrakClientProperties clientProperties) {
        final String path = "%s:%s".formatted(clientProperties.getSchema(), clientProperties.getHost());
        final int size = 16 * 1024 * 1024;
        log.info("AMTK-3210: Initializing Amtrak Data Web Client with base url [{}]", path);
        log.info("AMTK-3210:    increasing default flux-size [{}]", size);
        log.info("AMTK-3210:    with ConnectionTimeout[{}ms] ResponseTimeout[{}ms]",
                clientProperties.getWebClient().getConnectionTimeoutInMilliseconds(),
                clientProperties.getWebClient().getResponseTimeoutInMilliseconds());
        log.info("AMTK-3210:    with ReadTimeout[{}ms] WriteTimeout[{}ms]",
                clientProperties.getWebClient().getReadTimeoutInMilliseconds(),
                clientProperties.getWebClient().getWriteTimeoutInMilliseconds());

        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .build();

        return WebClient.builder()
                .baseUrl(path)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, clientProperties.getWebClient().getConnectionTimeoutInMilliseconds())
                        .responseTimeout(Duration.ofMillis(clientProperties.getWebClient().getResponseTimeoutInMilliseconds()))
                        .doOnConnected(connection ->
                                connection.addHandlerLast(new ReadTimeoutHandler(clientProperties.getWebClient().getReadTimeoutInMilliseconds(), TimeUnit.MILLISECONDS))
                                        .addHandlerLast(new WriteTimeoutHandler(clientProperties.getWebClient().getWriteTimeoutInMilliseconds(), TimeUnit.MILLISECONDS)))))
                .defaultCookie("amtakDataVersion", this.getClass().getPackage().getImplementationVersion())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.ALL_VALUE)
                .defaultUriVariables(Collections.singletonMap("url", path))
                .exchangeStrategies(strategies)
                .build();
    }

}
