package xyz.ebidding.common.auth;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.util.StringUtils;

/**
 * Feign interceptor，for passing auth info to backend
 *
 * @author yifei
 */
public class FeignRequestHeaderInterceptor implements RequestInterceptor {

    private final String serviceName;
    public FeignRequestHeaderInterceptor(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String userId = AuthContext.getUserId();
        if (!StringUtils.isEmpty(userId)) {
            requestTemplate.header(AuthConstant.FEIGN_CURRENT_USER_HEADER, userId);
        }
        requestTemplate.header(AuthConstant.INTERNAL_AUTHORIZATION_HEADER, serviceName);
    }
}
