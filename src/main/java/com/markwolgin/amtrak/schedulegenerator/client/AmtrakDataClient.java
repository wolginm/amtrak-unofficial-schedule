package com.markwolgin.amtrak.schedulegenerator.client;

import com.markwolgin.amtrak.schedulegenerator.exception.NonRetryableException;
import com.markwolgin.amtrak.schedulegenerator.exception.RetryableException;
import com.markwolgin.amtrak.schedulegenerator.models.ConsolidatedResponseObject;
import com.markwolgin.amtrak.schedulegenerator.properties.AmtrakClientProperties;
import com.markwolgin.amtrak.schedulegenerator.util.ObjectsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.retry.RetryContext;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

@Slf4j
@Component
public class AmtrakDataClient extends ClientBase {

    private final AmtrakClientProperties amtrakClientProperties;
    private final ObjectsUtil objectsUtil;
    private final WebClient webClient;

    public AmtrakDataClient(final AmtrakClientProperties amtrakClientProperties,
                            final ObjectsUtil objectsUtil,
                            @Qualifier("AmtrakDataWebClient") final WebClient webClient) {
        this.amtrakClientProperties = amtrakClientProperties;
        this.objectsUtil = objectsUtil;
        this.webClient = webClient;
        log.info("AMTK-3100: Created the Amtrak Data Client");
    }

    @Retryable(label = "retrieveRoute.retry",
            maxAttemptsExpression = "${amtrak.data.retry.maxRetryCount}",
                backoff = @Backoff(
                        delayExpression = "${amtrak.data.retry.delay}",
                        maxDelayExpression = "${amtrak.data.retry.maxDelay}",
                        multiplierExpression = "${amtrak.data.retry.multiplier}"
                ))
    public ConsolidatedResponseObject retrieveRoute(final Integer route) {
        RetryContext retryContext = RetrySynchronizationManager.getContext();
        int retry = retryContext.getRetryCount() + 1;
        ConsolidatedResponseObject consolidatedResponseObject;
        log.info("AMTK-3100: [{}/{}] In AmtrakDataClient.retrieveRoute",
                retry, amtrakClientProperties.getRetry().getMaxRetryCount());

        try {
            Flux<String> downloadedMono = webClient
                    .get()
                    .uri(this.amtrakClientProperties.getPath().formatted(route))
                    .retrieve()
                    .bodyToFlux(String.class);
            log.info("AMTK-3101: [{}/{}] The requested route at [{}:{}{}] has been queried and returned response non 4xx/5xx",
                    retry,
                    this.amtrakClientProperties.getRetry().getMaxRetryCount(),
                    this.amtrakClientProperties.getSchema(),
                    this.amtrakClientProperties.getHost(),
                    this.amtrakClientProperties.getPath().formatted(route));
            consolidatedResponseObject = objectsUtil.loadObject(downloadedMono.blockLast(), ConsolidatedResponseObject.class);
        } catch (WebClientResponseException exception) {
            int statusCode = exception.getStatusCode().value();
            log.error("AMTK-3190: [{}/{}] Received status code [{}] from [{}:{}{}]",
                    retry,
                    this.amtrakClientProperties.getRetry().getMaxRetryCount(),
                    statusCode,
                    this.amtrakClientProperties.getSchema(),
                    this.amtrakClientProperties.getHost(),
                    this.amtrakClientProperties.getPath().formatted(route));
            if (HttpStatus.TOO_MANY_REQUESTS.value() == statusCode
                    || HttpStatus.GATEWAY_TIMEOUT.value() == statusCode
                    || HttpStatus.INTERNAL_SERVER_ERROR.value() == statusCode) {
                log.error("AMTK-3198: [{}/{}] Retryable exception",
                        retry,
                        this.amtrakClientProperties.getRetry().getMaxRetryCount());
                throw new RetryableException(exception.getStatusText(), exception.getStatusCode(), exception);
            } else {
                log.error("AMTK-3199: [{}/{}] Non retryable exception",
                        retry,
                        this.amtrakClientProperties.getRetry().getMaxRetryCount());
                throw new NonRetryableException(exception);
            }
        } catch (IOException e) {
            log.error("AMTK-3199: [{}/{}] Non retryable exception : Unable to de-serialize ConsolidatedRouteObject",
                    retry,
                    this.amtrakClientProperties.getRetry().getMaxRetryCount());
            throw new NonRetryableException(e);
        }

        log.info("AMTK-3100: [{}/{}] Exiting AmtrakDataClient.retrieveRoute",
                retry,
                this.amtrakClientProperties.getRetry().getMaxRetryCount());
        return consolidatedResponseObject;
    }

    @Recover
    public Path retrieveRoute(final RetryableException retryableException) {
        int retry = RetrySynchronizationManager.getContext().getRetryCount();
        log.info("AMTK-3199: [{}/{}] All retries have failed to get the Route Payload.  Source [{}, {}]",
                retry,
                this.amtrakClientProperties.getRetry().getMaxRetryCount(),
                retryableException.getReason(), retryableException.getHttpStatusCode());
        return null;
    }

    @Recover
    public Path retrieveRoute(final NonRetryableException nonRetryableException) {
        int retry = RetrySynchronizationManager.getContext().getRetryCount();
        log.info("AMTK-3199: [{}/{}] All retries have failed to get the Route Payload.  Source [{}]",
                retry,
                this.amtrakClientProperties.getRetry().getMaxRetryCount(),
                nonRetryableException.getMessage());
        return null;
    }
}
