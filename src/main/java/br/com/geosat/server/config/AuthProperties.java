package br.com.geosat.server.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "geosat.auth")
@Getter @Setter
public class AuthProperties {
    private int accessTokenExpirationMinutes = 30;
    private int refreshTokenExpirationDays = 7;
}
