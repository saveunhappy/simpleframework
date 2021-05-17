package org.simpleframework.core.inject.annotation;

import lombok.extern.slf4j.Slf4j;
import org.simpleframework.core.BeanContainer;
import org.simpleframework.util.ClassUtil;
import org.simpleframework.util.ValidationUtil;

import java.lang.reflect.Field;
import java.util.Set;

@Slf4j
public class DependencyInjector {
    /*
    Bean 容器,这个是单例的
     */
    private BeanContainer beanContainer;

    public DependencyInjector() {
        beanContainer = BeanContainer.getInstance();
    }

    /*
    执行ioc
     */
    public void doIoc() {
        if (ValidationUtil.isEmpty(beanContainer.getClasses())) {
            log.warn("empty classset in BeanContainer");
            return;
        }
        //1.遍历Bean容器中所有的Class对象
        for (Class<?> clazz : beanContainer.getClasses()) {
            //2.遍历Class对象的所有成员变量
            Field[] fields = clazz.getDeclaredFields();
            if (ValidationUtil.isEmpty(fields)) {
                continue;
            }

            for (Field field : fields) {
                //3.找出所有被Autowired标记的成员变量
                if (field.isAnnotationPresent(Autowired.class)) {
                    Autowired autowired = field.getAnnotation(Autowired.class);
                    String autowiredValue = autowired.value();

                    //4.获取这些成员变量的类型
                    Class<?> fieldClass = field.getType();
                    //5.获取这些成员变量的类型在容器里对应的实例
                    Object fieldValue = getFieldInstance(fieldClass,autowiredValue);
                    if (fieldValue == null) {
                        throw new RuntimeException("unable to inject relevant type,target fieldClass is:" + fieldClass.getName());
                    } else {
                        //6.通过反射将对应的成员变量注入到成员变量所在类的实例
                        Object targetBean = beanContainer.getBean(clazz);
                        ClassUtil.setField(field, targetBean, fieldValue, true);

                    }
                }
            }
        }

    }

    /*
    根据Class在beanContainer里获取其实例或者实现类
     */
    private Object getFieldInstance(Class<?> fieldClass,String autowiredValue) {
        //先直接获取，虽然说都是面向接口编程，传的都是service 但是实际上都是得到的实现类，但是，万一他就是传送
        //的实现类呢。
        Object fieldValue = beanContainer.getBean(fieldClass);
        if (fieldValue != null) {
            return fieldValue;
        } else {
            //这就是接口了，获取实现类
            Class<?> implementedClass = getImplementClass(fieldClass,autowiredValue);
            if (implementedClass != null){
                return beanContainer.getBean(implementedClass);
            }else {
                return null;
            }
        }
    }

    private Class<?> getImplementClass(Class<?> fieldClass,String autowiredValue) {
        Set<Class<?>> classSet = beanContainer.getClassesBySuper(fieldClass);
        if(!ValidationUtil.isEmpty(classSet)){
            //这里可能是空的字符串，就是默认值，如果是一个，可以返回回去，
            if(ValidationUtil.isEmpty(autowiredValue)){
                if(classSet.size() == 1){
                    return classSet.iterator().next();
                }else {
                    //如果多于两个实现类，你也没有说具体哪个，我怎么知道给你哪个？抛出异常
                    throw new RuntimeException("multiple implemented classes for" + fieldClass.getName() +
                            "please set @Autowired's value to pick one");
                }
            }else{
                for(Class<?> clazz: classSet){
                    //simpleName就是不带包名，就应该是这样，谁写包名啊，有毛病啊，
                    if(autowiredValue.equals(clazz.getSimpleName())){
                        return clazz;
                    }
                }
            }
        }
        return null;

    }
}
