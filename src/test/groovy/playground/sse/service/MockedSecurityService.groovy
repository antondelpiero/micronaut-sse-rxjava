package playground.sse.service

import com.nimbusds.jwt.JWTClaimsSet
import io.micronaut.context.annotation.Requires
import io.micronaut.context.env.Environment
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.token.jwt.validator.AuthenticationJWTClaimsSetAdapter
import io.micronaut.security.utils.SecurityService

import javax.inject.Singleton

@Singleton
@Requires(env = Environment.TEST)
class MockedSecurityService implements SecurityService{

    @Override
    Optional<String> username() {
        return null
    }

    @Override
    Optional<Authentication> getAuthentication() {
        def builder = new JWTClaimsSet.Builder()
        builder.claim('userId', "1")
        builder.claim('companyId', "2")

        return Optional.of(new AuthenticationJWTClaimsSetAdapter(builder.build()))
    }

    @Override
    boolean isAuthenticated() {
        return false
    }

    @Override
    boolean hasRole(String role) {
        return false
    }

    @Override
    boolean hasRole(String role, String rolesKey) {
        return false
    }
}
