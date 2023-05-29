package cn.mrcsh.mcquery.Proxy;

import cn.mrcsh.mcquery.Annotation.API;
import cn.mrcsh.mcquery.Cache.Cunt;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class APIProxy {

    @Pointcut("@annotation(cn.mrcsh.mcquery.Annotation.API)")
    public void pt(){}

    @Around("pt()")
    public Object Around(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String name = signature.getMethod().getAnnotation(API.class).name();
        Cunt.invokeCounts.merge(name, 1, Integer::sum);
        return pjp.proceed();
    }
}
