package org.simpleframework.aop;

import org.simpleframework.aop.annotation.Aspect;
import org.simpleframework.aop.annotation.Order;
import org.simpleframework.aop.aspect.AspectInfo;
import org.simpleframework.aop.aspect.DefaultAspect;
import org.simpleframework.core.BeanContainer;
import org.simpleframework.util.ValidationUtil;

import java.lang.annotation.Annotation;
import java.util.*;

public class AspectWeaver {

    private BeanContainer beanContainer;

    public AspectWeaver() {
        this.beanContainer = BeanContainer.getInstance();
    }

    public void doAop() {
        //1.获取所有的切面类
        Set<Class<?>> aspectSet = beanContainer.getClassesByAnnotation(Aspect.class);

        //2.将切面类按照不同的织入目标进行切分
        Map<Class<? extends Annotation>, List<AspectInfo>> categorizedMap = new HashMap<>();
        //3.按照不同的织入目标分别去按顺序织入Aspect的逻辑
        if (ValidationUtil.isEmpty(aspectSet)) return;
        for (Class<?> aspectClass : aspectSet) {
            if (verifyAspect(aspectClass)) {
                categorizeAspect(categorizedMap, aspectClass);
            } else {
                throw new RuntimeException("@Aspect and @Order ");
            }
        }

    }

    private void categorizeAspect(Map<Class<? extends Annotation>, List<AspectInfo>> categorizedMap, Class<?> aspectClass) {
        Order orderTag = aspectClass.getAnnotation(Order.class);
        Aspect aspectTag = aspectClass.getAnnotation(Aspect.class);
        //这个是被@Aspect标记的类
        DefaultAspect aspect = (DefaultAspect) beanContainer.getBean(aspectClass);
        //Order和Aspect的组合
        AspectInfo aspectInfo = new AspectInfo(orderTag.value(),aspect);
        //aspectTag.value()就是@Controller\@Service这些东西
        //因为这个方法是在外层的一个for循环中的，就是获取到所有的被@Aspect标注的类进行一一循环
        if(!categorizedMap.containsKey(aspectTag.value())){
            //走到这里代表，往被@Controller\@Service标记的，以@Controller\@Service为key，切面类作为集合给添加到集合中去
            List<AspectInfo> aspectInfoList = new ArrayList<>();
            aspectInfoList.add(aspectInfo);
            categorizedMap.put(aspectTag.value(),aspectInfoList);
        }else{
            //到这就说明之前创建过@Controller\@Service的注解了，而且有新的添加进来了，那么就获取到之前的key,再添加就好了。
            List<AspectInfo> aspectInfoList = categorizedMap.get(aspectTag.value());
            aspectInfoList.add(aspectInfo);
        }
        //3.按照不同的织入目标分别去按序织入Aspect的逻辑
        if(ValidationUtil.isEmpty(categorizedMap))return;
        for (Class<? extends Annotation> category: categorizedMap.keySet()) {
            //category就是@Controller，categorizedMap.get(category)就是before,after之类的Aspect集合，因为可能有好多的Aspect
            weaveByCategory(category,categorizedMap.get(category));
            
        }
    }

    private void weaveByCategory(Class<? extends Annotation> category, List<AspectInfo> aspectInfos) {
        //1.获取被代理类的集合,就是被@Controller标记的Class对象
        Set<Class<?>> classSet = beanContainer.getClassesByAnnotation(category);

        if (ValidationUtil.isEmpty(classSet))return;
        //2.遍历代理类，为每个代理类生成动态代理实例
        for (Class<?> targetClass : classSet) {
            //创建动态代理对象，这个AspectListExecutor就是MethodInterceptor的实例对象
            AspectListExecutor aspectListExecutor = new AspectListExecutor(targetClass,aspectInfos);
            Object proxyBean = ProxyCreator.createProxy(targetClass, aspectListExecutor);
            //3.把动态代理对象实例添加到容器里面去，取代之前的真实对象
            beanContainer.addBean(targetClass,proxyBean);
        }

    }

    //框架一定要遵循Aspect类添加@Aspect和Order标签的规范，同时，必须继承自DefaultAspect.class
    //此外，@Aspect的属性值不能是它本身
    private boolean verifyAspect(Class<?> aspectClass) {
        return aspectClass.isAnnotationPresent(Aspect.class) &&
                aspectClass.isAnnotationPresent(Order.class) &&
                DefaultAspect.class.isAssignableFrom(aspectClass) &&
                aspectClass.getAnnotation(Aspect.class).value() != Aspect.class;
    }
}
