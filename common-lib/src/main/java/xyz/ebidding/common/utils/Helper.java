package xyz.ebidding.common.utils;

import java.time.Instant;

public class Helper {

    public static String generateGravatarUrl(String email) {
        String hash = MD5Util.md5Hex(email);
        return String.format("https://www.gravatar.com/avatar/%s.jpg?s=400&d=identicon", hash);
    }

    public static Instant MYSQL_MIN_INSTANT = Instant.parse("1970-01-01T00:00:01.000000Z");
    public static Instant MYSQL_MAX_INSTANT = Instant.parse("2038-01-19T03:14:07.999999Z");
}
