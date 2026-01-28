package io.dodn.commerce.core.api.config;

import io.dodn.commerce.core.support.error.CoreException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.logging.LogLevel;

import java.lang.reflect.Method;

@Slf4j
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(Throwable e, Method method, Object... params) {
        if (e instanceof CoreException coreException) {
            LogLevel logLevel = coreException.getErrorType().getLogLevel();
            switch (logLevel) {
                case ERROR -> log.error("CoreException : {}", e.getMessage(), e);
                case WARN -> log.warn("CoreException : {}", e.getMessage(), e);
                default -> log.info("CoreException : {}", e.getMessage(), e);
            }
        } else {
            log.error("Exception : {}", e.getMessage(), e);
        }
    }
}
