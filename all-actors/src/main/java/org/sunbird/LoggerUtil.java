package org.sunbird;

import net.logstash.logback.marker.Markers;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunbird.request.RequestContext;

public class LoggerUtil {

    private Logger logger;

    public  LoggerUtil(Class c) {
        logger = LoggerFactory.getLogger(c);
    }

    public void info(RequestContext requestContext, String message, Object data) {
        if(null != requestContext) {
            logger.info(Markers.appendEntries(requestContext.getContextMap()), message, data);
        } else {
            logger.info(message, data);
        }
    }

    public void info(RequestContext requestContext, String message) {
        info(requestContext, message, null);
    }

    public void error(RequestContext requestContext, String message, Throwable e) {
        if(null != requestContext) {
            logger.error(Markers.appendEntries(requestContext.getContextMap()) ,message, e);
        } else {
            logger.error(message, e);
        }
    }

    public void warn(RequestContext requestContext, String message, Throwable e) {
        if(null != requestContext) {
            logger.warn(Markers.appendEntries(requestContext.getContextMap()), message, e);
        } else {
            logger.warn(message, e);
        }
    }

    public void debug(RequestContext requestContext, String message, Object data) {
        if(isDebugEnabled(requestContext)) {
            logger.info(Markers.appendEntries(requestContext.getContextMap()), message, data);
        } else {
            logger.debug(message, data);
        }
    }

    public void debug(RequestContext requestContext, String message) {debug(requestContext, message, null);}

    private static boolean isDebugEnabled(RequestContext requestContext) {
        return (null != requestContext && StringUtils.equalsIgnoreCase("true", requestContext.getDebugEnabled()));
    }

}
