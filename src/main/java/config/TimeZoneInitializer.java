package config;

import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimeZone;

@ApplicationScoped
@Startup
public class TimeZoneInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(TimeZoneInitializer.class);

    @PostConstruct
    public void initializeDefaultTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Lima"));
        LOG.info("Default timezone set to: {}", TimeZone.getDefault().getID());
    }
}