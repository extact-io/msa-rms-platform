package io.extact.msa.rms.platform.core.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.Config;

import lombok.extern.slf4j.Slf4j;

/**
 * Dump out Config at startup.
 * Filter the output by setting configdump.filters as below.
 * <pre>
 * rms.debug:
 *   configdump:
 *     enable: false
 *     filter:
 *       enable: true
 *       pattern:
 *         - security
 *         - env.rms
 * </pre>
 */
@ApplicationScoped
@Slf4j(topic = "ConfigDump")
public class ConfigDump {

    private static final String CONFIG_PREFIX = "rms.debug.configdump";
    private Config config;

    @Inject
    public ConfigDump(Config config) {
        this.config = config;
    }
    void onInialized(@Observes @Initialized(ApplicationScoped.class) Object event) {

        if (!log.isDebugEnabled()) {
            return;
        }
        if (!config.getOptionalValue(CONFIG_PREFIX + ".enable", Boolean.class).orElse(false)) {
            return;
        }

        List<String> filters = new ArrayList<>();
        if (config.getOptionalValue(CONFIG_PREFIX + ".filter.enable", Boolean.class).orElse(true)) {
            filters = StreamSupport.stream(config.getPropertyNames().spliterator(), false)
                    .filter(s -> s.startsWith(CONFIG_PREFIX + ".filter.pattern"))
                    .filter(s -> config.getOptionalValue(s, String.class).isPresent())
                    .map(s -> config.getValue(s, String.class))
                    .toList();
        }

        Predicate<String> containsKeyword = new ContainsKeyworkWithForwardMatch(filters);
        String configDump = StreamSupport.stream(config.getPropertyNames().spliterator(), false)
            .filter(containsKeyword)
            .map(name -> name + "=" + config.getOptionalValue(name, String.class).orElse(""))
            .sorted()
            .collect(Collectors.joining(System.lineSeparator()));

        log.debug(System.lineSeparator() + configDump);
    }

    static class ContainsKeyworkWithForwardMatch implements Predicate<String> {

        private List<String> filters;
        ContainsKeyworkWithForwardMatch(List<String> filters) {
            this.filters = filters;
        }

        @Override
        public boolean test(String name) {
            return filters.isEmpty() || filters.stream().anyMatch(name::startsWith);
        }
    }
}
