package xyz.ebidding.common.auth;

public class AuthConstant {
    public static final String BID_SERVICE = "bid-service";

    public static final String BWIC_SERVICE = "bwic-service";
    public static final String CLIENT = "CLIENT";
    public static final String TRADER = "TRADER";

    public static final String COOKIE_NAME = "ebidding-faraday";
    // header set for internal user id
    public static final String FEIGN_CURRENT_USER_HEADER = "feign-current-user-id";
    // AUTHORIZATION_HEADER is the http request header
    // key used for accessing the internal authorization.
    public static final String INTERNAL_AUTHORIZATION_HEADER = "X-Inter-Service-Feign";

    public static final String CLAIM_USER_ID = "userId";
    public static final String CLAIM_USER_NAME = "name";
    public static final String CLAIM_ROLE = "role";

    public static final String X_JWT_ID_HEADER = "X-jwt-id";
    public static final String X_JWT_NAME_HEADER = "X-jwt-name";
    public static final String X_JWT_ROLE_HEADER = "X-jwt-role";

}
