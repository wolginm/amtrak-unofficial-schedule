package com.markwolgin.amtrak.schedulegenerator.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("amtrak.data")
public class AmtrakClientProperties {

    private String schema = "https";
    /**
     * Base url for Amtrak data.
     */
    private String host = "//markwolgin.com";
    /**
     * Context path for the GTFS url.  Incase Amtrak changes paths and I get
     *  lazy...
     */
    private String path = "/v1/route?ids=%d";

    private WebClientCustomProperties webClient;
    private RetryableProperties retry;

}
