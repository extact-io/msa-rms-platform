package io.extact.msa.rms.platform.core.jwt;

import static io.extact.msa.rms.platform.core.jwt.JwtConfig.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigValue;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.Converter;
import org.jboss.weld.exceptions.UnsupportedOperationException;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class MapConfig implements Config {

    private Map<String, Object> configMap = new HashMap<>();

    // -------------------------------------------------------- implements Config.

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getValue(String propertyName, Class<T> propertyType) {
        if (!configMap.containsKey(propertyName)) {
            return null;
        }
        return (T) configMap.get(propertyName);
    }

    @Override
    public <T> Optional<T> getOptionalValue(String propertyName, Class<T> propertyType) {
        return Optional.ofNullable(getValue(propertyName, propertyType));
    }

    @Override
    public Iterable<String> getPropertyNames() {
        return configMap.keySet();
    }
    @Override
    public Iterable<ConfigSource> getConfigSources() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ConfigValue getConfigValue(String propertyName) {
        return new ConfigValueImpl(propertyName, configMap.get(propertyName).toString(),
                configMap.get(propertyName).toString(), this.toString(), 0);
    }

    @Override
    public <T> Optional<Converter<T>> getConverter(Class<T> forType) {
        return Optional.empty();
    }

    @Override
    public <T> T unwrap(Class<T> type) {
        if (type.equals(MapConfig.class)) {
            return type.cast(this);
        }
        if (type.equals(Config.class)) {
            return type.cast(this);
        }
        throw new UnsupportedOperationException("Cannot unwrap config into " + type.getName());
    }

    // -------------------------------------------------------- service methods.

    public void setPrivateKeyPath(String path) {
        configMap.put(CONFIG_PREFIX + "privatekey.path", path);
    }
    public void setIssuer(String issuer) {
        configMap.put(CONFIG_PREFIX + "claim.issuer", issuer);
    }
    public void setIssuedAt(long val) {
        configMap.put(CONFIG_PREFIX + "claim.issuedAt", val);
    }
    public void setExpirationTime(int minutes) {
        configMap.put(CONFIG_PREFIX + "claim.exp", minutes);
    }

    // -------------------------------------------------------- inner class.

    @AllArgsConstructor
    @Getter
    static class ConfigValueImpl implements ConfigValue {
        private String name;
        private String value;
        private String rawValue;
        private String sourceName;
        private int sourceOrdinal;
    }
}
