package xyz.ebidding.common.auth;

import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * A context holder class for holding the current userId and authz info
 *
 * @author yifei.yang
 */
public class AuthContext {

    private static String getRequestHeader(String headerName) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
            return request.getHeader(headerName);
        }
        return null;
    }

    public static String getUserId() {
        String jwtUserId = getRequestHeader(AuthConstant.X_JWT_ID_HEADER);
        String feignClientUserId = getRequestHeader(AuthConstant.FEIGN_CURRENT_USER_HEADER);
        return StringUtils.hasText(jwtUserId) ? jwtUserId : feignClientUserId;
    }

    public static String getUsername() {
        return getRequestHeader(AuthConstant.X_JWT_NAME_HEADER);
    }

    public static String getUserRole() {
        return getRequestHeader(AuthConstant.X_JWT_ROLE_HEADER);
    }
}
