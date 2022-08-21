package io.extact.msa.rms.platform.core.jwt;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import io.extact.msa.rms.platform.core.jwt.consumer.JwtValidateRequestFilter;
import io.extact.msa.rms.platform.core.jwt.provider.JwtProvideResponseFilter;

public class JwtSecurityFilterFeature implements Feature {

    @Override
    public boolean configure(FeatureContext context) {
        context.register(JwtProvideResponseFilter.class);
        context.register(JwtValidateRequestFilter.class);
        return true;
    }
}
