package xyz.ebidding.common.services;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * ServiceDirectory allows access to a backend service using its subdomain
 *
 * EbiddingServices ia a map of subdomains -> specs
 * Subdomain is <string> + Env["rootDomain"]
 * e.g. "login" service on prod is "login" + "ebidding.xyz"
 *
 * KEEP THIS LIST IN ALPHABETICAL ORDER PLEASE
 */
public class ServiceDirectory {

    private static Map<String, Service> serviceMap;

    static {

        Map<String, Service> map = new TreeMap<>();

        Service service = Service.builder()
                .security(SecurityConstant.SEC_PUBLIC)
                .restrictDev(false)
                .backendDomain("ui-service")
                .noCacheHtml(true)
                .build();
        map.put("ebidding", service);

        service = Service.builder()
                // Debug site for faraday proxy
                .security(SecurityConstant.SEC_PUBLIC)
                .restrictDev(true)
                .backendDomain("httpbin.org")
                .build();
        map.put("faraday", service);

        service = Service.builder()
                .security(SecurityConstant.SEC_PUBLIC)
                .restrictDev(false)
                .backendDomain("account-service")
                .build();
        map.put("account", service);

        service = Service.builder()
                .security(SecurityConstant.SEC_AUTHENTICATED)
                .restrictDev(false)
                .backendDomain("bid-service")
                .build();
        map.put("bid-svc", service);

        service = Service.builder()
                .security(SecurityConstant.SEC_AUTHENTICATED)
                .restrictDev(true)
                .backendDomain("bwic-service")
                .build();
        map.put("bwic-svc", service);

        serviceMap = Collections.unmodifiableMap(map);
    }

    public static Map<String, Service> getMapping() {
        return serviceMap;
    }
}
