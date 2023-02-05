package io.extact.msa.rms.platform.core.jaxrs.mapper;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;

import org.eclipse.microprofile.config.Config;

import lombok.extern.slf4j.Slf4j;

/**
 * Handle {@link NotFoundException} that occurs when status code is 404.
 * The handle of unknown RuntimeException logs stacktrace, but I don't want to
 * output stacktrace with 404, so NotFoundException is handled individually.
 */
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class PageNotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

    private static final String IGNORE_404_PATHS_PROP = "rms.jaxrs.mapper.ignore.404.paths";

    @Context
    private UriInfo uriInfo;
    private Ignore404 ignore404;

    @Inject
    public PageNotFoundExceptionMapper(Config config) {
        this.ignore404 =new Ignore404(config.getOptionalValue(IGNORE_404_PATHS_PROP, String[].class));
    }

    /**
     * Static content registered in the configuration file does not handle anything.
     *
     * @param exception Occurrence exception
     * @return {@link Response}
     */
    @Override
    public Response toResponse(NotFoundException exception) {
        if (!ignore404.test("/" + uriInfo.getPath())) {
            log.warn(exception.getMessage() + " (path=>{})", "/" + uriInfo.getPath());
        }
        return exception.getResponse();
    }


    // ----------------------------------------------------- inner class def

    static class Ignore404 implements Predicate<String> {
        private String[] unhandlePaths;
        public Ignore404(Optional<String[]> paths) {
            this.unhandlePaths = paths.orElse(new String[0]);
        }
        @Override
        public boolean test(String requestPath) {
            return Stream.of(unhandlePaths).anyMatch(requestPath::startsWith);
        }
    }
}
