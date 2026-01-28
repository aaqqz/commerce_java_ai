package io.dodn.commerce.storage.db.core.converter;

import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "dodn.storage.core.security")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class SecurityProperty {
    private String key;
    private String iv;
}
