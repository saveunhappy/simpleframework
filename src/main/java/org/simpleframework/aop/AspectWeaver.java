package org.simpleframework.aop;

import org.simpleframework.aop.annotation.Aspect;
import org.simpleframework.aop.annotation.Order;
import org.simpleframework.aop.aspect.AspectInfo;
import org.simpleframework.aop.aspect.DefaultAspect;
import org.simpleframework.core.BeanContainer;
import org.simpleframework.util.ValidationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AspectWeaver {
    private BeanContainer beanContainer;

    public AspectWeaver() {
        this.beanContainer = BeanContainer.getInstance();
    }

    public void doAop() {
        //1、获取所有的切面类
        Set<Class<?>> aspectSet = beanContainer.getClassesByAnnotation(Aspect.class);
        if (ValidationUtil.isEmpty(aspectSet)) {
            return;
        }
        //2、拼接AspectInfoList，这个里面是所有的切面类，里面有order,before,after,
        //还有表达式，就是把aspect类的内容提取出来弄一个新的对象，可以称之为转换
        List<AspectInfo> aspectInfoList = packAspectInfoList(aspectSet);
        //3、遍历容器中的类
        Set<Class<?>> classSet = beanContainer.getClasses();
        for (Class<?> targetClass : classSet) {
            //排除AspectClass自身
            if (targetClass.isAnnotationPresent(Aspect.class)) {
                continue;
            }
            //4、粗筛符合条件的Aspect，每次循环去把所有的aspect去切那一个类，
            List<AspectInfo> roughMatchedAspectList = collectRoughMatchedAspectListForSpecificClass(aspectInfoList, targetClass);
            //5、尝试进行Aspect的织入
            wrapIfNecessary(roughMatchedAspectList, targetClass);
        }

    }


    private void wrapIfNecessary(List<AspectInfo> roughMatchedAspectList, Class<?> targetClass) {
        if (ValidationUtil.isEmpty(roughMatchedAspectList)) {
            return;
        }
        // 创建动态代理对象，这里是把粗筛列表给传到这个对象里面去了，在执行的时候会调用invoke方法，
        // invoke之前会有一次精细的筛选，就是去解析execution，之前是解析的within，然后过滤
        // 下来的execution执行before,after,之类的
        AspectListExecutor aspectListExecutor = new AspectListExecutor(targetClass, roughMatchedAspectList);
        Object proxy = ProxyCreator.createProxy(targetClass, aspectListExecutor);
        beanContainer.addBean(targetClass, proxy);
    }
    //筛选出所有的aspect类
    private List<AspectInfo> collectRoughMatchedAspectListForSpecificClass(List<AspectInfo> aspectInfoList, Class<?> targetClass) {
        List<AspectInfo> roughMatchedAspectList = new ArrayList<>();
        for (AspectInfo aspectInfo : aspectInfoList) {
            //粗筛,让所有的Aspect去看能不能切到对应的类，这个主要是within，
            // 因为execution他解析不了，直接就返回true了
            if (aspectInfo.getPointcutLocator().roughMatches(targetClass)) {
                roughMatchedAspectList.add(aspectInfo);
            }
        }
        return roughMatchedAspectList;
    }
    //这一步就是往原来的AspectInfo里面添加了表达式解析
    private List<AspectInfo> packAspectInfoList(Set<Class<?>> aspectSet) {
        List<AspectInfo> aspectInfoList = new ArrayList<>();
        for (Class<?> aspectClass : aspectSet) {
            if (verifyAspect(aspectClass)) {
                Aspect aspectTag = aspectClass.getAnnotation(Aspect.class);
                Order orderTag = aspectClass.getAnnotation(Order.class);
                DefaultAspect defaultAspect = (DefaultAspect) beanContainer.getBean(aspectClass);
                //初始化表达式定位器，现在传的就是表达式的字符串
                PointcutLocator pointcutLocator = new PointcutLocator(aspectTag.pointcut());
                AspectInfo aspectInfo = new AspectInfo(orderTag.value(), defaultAspect, pointcutLocator);
                aspectInfoList.add(aspectInfo);
            } else {
                //不遵守规范则直接抛出异常
                throw new RuntimeException("@Aspect and @Order must be added to the Aspect class, and Aspect class must extend from DefaultAspect");
            }
        }
        return aspectInfoList;
    }

    //框架中一定要遵守给Aspect类添加@Aspect和@Order的规范，同时，必须继承值DefaultAspect.class
    //此外，@Aspect的属性不能是他本身
    private boolean verifyAspect(Class<?> aspectClass) {
        return aspectClass.isAnnotationPresent(Aspect.class) &&
                aspectClass.isAnnotationPresent(Order.class) &&
                DefaultAspect.class.isAssignableFrom(aspectClass);
    }
}
