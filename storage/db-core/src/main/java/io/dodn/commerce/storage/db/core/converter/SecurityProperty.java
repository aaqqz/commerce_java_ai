package io.dodn.commerce.storage.db.core.converter;

import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "dodn.storage.core.security")
@Getter
@NoArgsConstructor
@AllArgsConstructor
class SecurityProperty {
    private String key;
    private String iv;
}
