package xyz.ebidding.common.auth;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public class AuthorizeInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        Authorize authorize = handlerMethod.getMethod().getAnnotation(Authorize.class);
        if (authorize == null) {
            return true;
        }

        String[] allowedHeaders = authorize.value();
        String serviceName = request.getHeader(AuthConstant.INTERNAL_AUTHORIZATION_HEADER);
        String role = request.getHeader(AuthConstant.X_JWT_ROLE_HEADER);

        return Stream.of(serviceName, role).filter(Objects::nonNull)
                .anyMatch(auth -> Arrays.asList(allowedHeaders).contains(auth));
    }
}
