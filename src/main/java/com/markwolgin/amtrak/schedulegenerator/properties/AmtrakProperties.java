package com.markwolgin.amtrak.schedulegenerator.properties;

import com.markwolgin.amtrak.schedulegenerator.client.AmtrakDataClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Amtrak Data Properties.
 */
@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("amtrak")
public class AmtrakProperties {

    /**
     * Amtrak Gtfs Properties, for indicating where to get and put the 
     *  Gtfs files.
     */
    private AmtrakDataClient data;
}
