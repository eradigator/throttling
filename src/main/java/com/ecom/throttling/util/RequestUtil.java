package com.ecom.throttling.util;

import com.ecom.throttling.exception.NoXForwardedForHeaderPresentException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

public class RequestUtil {

    public static String getIp() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(
                RequestContextHolder.getRequestAttributes())).getRequest();
        String ips = request.getHeader("X-Forwarded-For");

        if (ips == null) throw new NoXForwardedForHeaderPresentException();
        return ips.split(",")[0];
    }
}
