package org.simpleframework.aop;

import lombok.Getter;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.simpleframework.aop.aspect.AspectInfo;
import org.simpleframework.util.ValidationUtil;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class AspectListExecutor implements MethodInterceptor {

    private Class<?> targetClass;
    @Getter
    private List<AspectInfo> aspectInfoList;

    public AspectListExecutor(Class<?> targetClass, List<AspectInfo> aspectInfoList) {
        this.targetClass = targetClass;
        this.aspectInfoList = sortAspectInfoList(aspectInfoList);
    }

    private List<AspectInfo> sortAspectInfoList(List<AspectInfo> aspectInfoList) {
        aspectInfoList.sort(Comparator.comparingInt(AspectInfo::getOrderIndex));
        return aspectInfoList;
    }


    /**
     * 定义横切逻辑
     *
     * @param o
     * @param method
     * @param args
     * @param methodProxy
     * @return
     * @throws Throwable
     */
    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        Object returnValue = null;
        collectAccurateMatchedAspectList(method);
        if (ValidationUtil.isEmpty(aspectInfoList)) {
            returnValue = methodProxy.invokeSuper(o, args);
            return returnValue;
        }
        //1.按照Order的顺序升序执行完所有的Aspect的before方法，因为值小的优先级越高
        invokeBeforeAdvice(method, args);
        try {
            //2.执行被代理类的方法
            returnValue = methodProxy.invokeSuper(o, args);
            //3.如果被代理方法正常返回，则按照order的顺序降序执行完所有Aspect的afterReturning方法，就相当于你优先级高，然后
            //先执行了你的before，但是你的after在另一个aspect的before的后面执行，所以要执行优先级低的before，然后执行完优先级
            //低的那个before之后，那么就把所有的东西就执行完，就相当于你从一个方法进入到另一个方法，然后把这个方法都执行完。
            //再返回到调用处
            invokeAfterReturningAdvices(method, args, returnValue);
        } catch (Exception e) {
            //4.如果被代理方法抛出异常，则按照order的顺序降序执行完所有Aspect的afterThrowing方法
            invokeAfterThrowingAdvices(method, args, e);
        }
        return returnValue;
    }

    private void collectAccurateMatchedAspectList(Method method) {
        if (ValidationUtil.isEmpty(aspectInfoList)) return;
        //foreach不支持动态移除元素，改用迭代器
        Iterator<AspectInfo> it = aspectInfoList.iterator();
        while (it.hasNext()) {
            AspectInfo aspectInfo = it.next();
            if (!aspectInfo.getPointcutLocator().accurateMatches(method)) {
                it.remove();
            }
        }
    }
    //4.如果被代理方法抛出异常，则按照order的顺序降序执行完所有Aspect的afterThrowing方法

    private void invokeAfterThrowingAdvices(Method method, Object[] args, Exception e) throws Throwable {
        for (int i = aspectInfoList.size() - 1; i >= 0; i--) {
            aspectInfoList.get(i).getAspectObject().afterThrowing(targetClass, method, args, e);
        }
    }

    private Object invokeAfterReturningAdvices(Method method, Object[] args, Object returnValue) throws Throwable {
        Object result = null;
        for (int i = aspectInfoList.size() - 1; i >= 0; i--) {
            result = aspectInfoList.get(i).getAspectObject().afterReturning(targetClass, method, args, returnValue);
        }
        return result;
    }

    private void invokeBeforeAdvice(Method method, Object[] args) throws Throwable {
        for (AspectInfo aspectInfo : aspectInfoList) {
            aspectInfo.getAspectObject().before(targetClass, method, args);
        }
    }
}
