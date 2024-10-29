package org.tpjava.AuthService.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.tpjava.AuthService.annotation.PermissionRequired;
import org.tpjava.AuthService.util.AuthenticatedUserUtil;

import java.util.List;

@Aspect
@Component
public class PermissionAspect {

    @Around("@annotation(permissionRequired)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, PermissionRequired permissionRequired) throws Throwable {
        String requiredPermission = permissionRequired.value();
        List<String> userPermissions = AuthenticatedUserUtil.getAuthenticatedUserPermissions();

        if (userPermissions.contains(requiredPermission)) {
            System.out.println("User has required permission: " + requiredPermission);
            return joinPoint.proceed();
        } else {
            // User lacks the required permission, return a 403 Forbidden response
            System.out.println("User odes not have the required permission: " + requiredPermission);
            return new ResponseEntity<>("You do not have permission to access this resource.", HttpStatus.FORBIDDEN);
        }
    }
}
