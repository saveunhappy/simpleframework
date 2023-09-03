package org.simpleframework.aop.aspect;

import java.lang.reflect.Method;

public abstract class DefaultAspect {


    public void before(Class<?> targetClass, Method method,Object[] args) throws Throwable{

    }

    public void afterReturning(Class<?> targetClass, Method method,Object[] args,Object returnValue) throws Throwable{

    }

    public void afterReturning(Class<?> targetClass, Method method,Object[] args,Throwable e) throws Throwable{

    }
}
