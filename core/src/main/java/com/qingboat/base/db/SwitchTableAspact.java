package com.qingboat.base.db;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class SwitchTableAspact {

    private static ThreadLocal< Map<String,Object>> switchMetaThreadLocal = new ThreadLocal<>();

    public static void setSwitchMeta(String tableName,Object meta){
        Map<String,Object> map = switchMetaThreadLocal.get();
        if (map == null){
            map = new HashMap<>();
            switchMetaThreadLocal.set(map);
        }
        map.put(tableName,meta);
    }
    public static Map<String,Object> getSwitchMeta(){
        return switchMetaThreadLocal.get();
    }

    public static void removeSwitchMeta(){
        switchMetaThreadLocal.remove();
    }


    @Pointcut("@annotation(com.qingboat.base.db.SwitchTable)")
    public void switchTable(){}

    @Before("switchTable()")
    public void aspectBefore(JoinPoint joinPoint) {
        MethodSignature signature= (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        Map<String,Object>  switchTableMap = new HashMap<>(); // Key：表名称，value：分表因子

        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();

        for(int i =0;i<parameters.length;i++){
            Parameter p = parameters[i];
            if (p.isAnnotationPresent(SwitchParam.class)){
                switchTableMap.put(p.getAnnotation(SwitchParam.class).value(),String.valueOf(args[i]));
            }
        }
        if (!switchTableMap.isEmpty()){
            switchMetaThreadLocal.set(switchTableMap);
            System.err.println(" ChangeTableAspact aspectBefore :" +switchTableMap);
        }
    }
    @After("switchTable()")
    public void aspectAfter(JoinPoint joinPoint) {
        System.err.println(" ChangeTableAspact aspectAfter " );
        switchMetaThreadLocal.remove();
    }
    @AfterThrowing(value = "switchTable()",throwing="e")
    public void aspectAfterThrowing(JoinPoint joinPoint,Exception e) {
        System.err.println(" ChangeTableAspact aspectAfterThrowing " );
        switchMetaThreadLocal.remove();
    }


}
