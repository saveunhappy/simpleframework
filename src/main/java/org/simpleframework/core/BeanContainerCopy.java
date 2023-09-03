package org.simpleframework.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.simpleframework.core.annotation.Component;
import org.simpleframework.core.annotation.Controller;
import org.simpleframework.core.annotation.Repository;
import org.simpleframework.core.annotation.Service;
import org.simpleframework.util.ClassUtil;
import org.simpleframework.util.ValidationUtil;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BeanContainerCopy {
       /*
    存放所有被配置标记的目标对象的Map，比如@Controller，@Service之类的
     */
    private static final Map<Class<?>,Object> beanMap = new ConcurrentHashMap<>();
        /*
    加载bean的注解列表
     */
    private static final List<Class<?extends Annotation>> BEAN_ANNOTATION = Arrays.asList(Component.class,
                Controller.class, Service.class, Repository.class);
    public int size(){
        return beanMap.size();
    }

    public static BeanContainerCopy getInstance(){
        return ContainerHolder.HOLDER.instance;
    }
    public enum ContainerHolder{
        HOLDER;
        private final BeanContainerCopy instance;

        ContainerHolder(){
            instance = new BeanContainerCopy();
        }
    }

    private boolean loaded = false;

    public boolean isLoaded() {
        return loaded;
    }
    public synchronized void loadBeans(String packageName) {
        if (isLoaded()){
            log.warn("已经加载过了");
            return;
        }
        Set<Class<?>> classes = ClassUtil.extractPackageClass(packageName);
        for (Class<?> clazz : classes) {
            for(Class<?extends Annotation> annotation: BEAN_ANNOTATION){
                if (clazz.isAnnotationPresent(annotation)){
                    beanMap.put(clazz,ClassUtil.newInstance(clazz,true));
                }
            }
        }
        loaded = true;
    }
    /**
     * 添加一个class对象及其Bean实例
     *
     * @param clazz Class对象
     * @param bean  Bean实例
     * @return 原有的Bean实例, 没有则返回null
     */
    public Object addBean(Class<?> clazz,Object bean){
         return beanMap.put(clazz,bean);
    }
    /**
     * 移除一个IOC容器管理的对象
     *
     * @param clazz Class对象
     * @return 删除的Bean实例, 没有则返回null
     */
    public Object removeBean(Class<?> clazz){
        return beanMap.remove(clazz);
    }
    /**
     * 根据Class对象获取Bean实例
     *
     * @param clazz Class对象
     * @return Bean实例
     */
    public Object getBean(Class<?> clazz) {
        return beanMap.get(clazz);
    }
    public Set<Class<?>> getClasses() {
        //就是所有的key,
        // value就是对应的Bean的实例了就是下边的方法
        return beanMap.keySet();
    }
    /**
     * 获取所有Bean集合
     *
     * @return Bean集合
     */
    public Set<Object> getBeans() {
        return new HashSet<>(beanMap.values());
    }

    /**
     * 根据注解筛选出Bean的Class集合
     *
     * @param annotation 注解
     * @return Class集合
     */
    public Set<Class<?>> getClassesByAnnotation(Class<? extends Annotation> annotation) {
        Set<Class<?>> keySet = getClasses();

        if(ValidationUtil.isEmpty(keySet)){
            log.warn("nothing in beanMap");
            return null;
        }
        //2.通过注解筛选被注解标记的class对象，并添加到classSet中
        Set<Class<?>> classSet = new HashSet<>();
        for (Class<?> clazz : keySet) {
           if(clazz.isAnnotationPresent(annotation)) {
               classSet.add(clazz);
           }
        }
        return classSet.size() > 0 ? classSet : null;
    }
    /**
     * 通过接口或者父类获取实现类或者子类的Class集合，不包括其本身
     *
     * @param interfaceOrClass 接口Class或者父类Class
     * @return Class集合
     */
    public Set<Class<?>> getClassesBySuper(Class<?> interfaceOrClass) {
        Set<Class<?>> keySet = getClasses();

        if(ValidationUtil.isEmpty(keySet)){
            log.warn("nothing in beanMap");
            return null;
        }
        //2.通过注解筛选被注解标记的class对象，并添加到classSet中
        Set<Class<?>> classSet = new HashSet<>();
        for (Class<?> clazz : keySet) {
            if(clazz.isAssignableFrom(interfaceOrClass)&&!clazz.equals(interfaceOrClass)) {
                classSet.add(clazz);
            }
        }
        return classSet.size() > 0 ? classSet : null;
    }
}