package com.inventory.aspect;

import com.inventory.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Aspect
@Component
public class AuditAspect {

    @Autowired
    private AuditLogService auditLogService;

    @Pointcut("execution(* com.inventory.controller..*.*(..))")
    public void controllerPointcut() {
    }

    @Around("controllerPointcut() && @annotation(auditLog)")
    public Object around(ProceedingJoinPoint joinPoint, RequirePermission auditLog) throws Throwable {
        return proceedWithAudit(joinPoint, auditLog.value());
    }

    private Object proceedWithAudit(ProceedingJoinPoint joinPoint, String operation) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        String username = "anonymous";
        Long userId = null;

        try {
            Object result = joinPoint.proceed();

            if (request != null && operation != null) {
                String ip = request.getRemoteAddr();
                String userAgent = request.getHeader("User-Agent");
                String method = request.getMethod();
                String content = joinPoint.getSignature() + " - " + Arrays.toString(joinPoint.getArgs());

                if (userId != null) {
                    auditLogService.log(userId, username, operation, content, method, ip, userAgent);
                }
            }

            return result;
        } catch (Exception e) {
            throw e;
        }
    }
}
