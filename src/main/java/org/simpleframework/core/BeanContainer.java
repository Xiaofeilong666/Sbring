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
public class BeanContainer {
    /**
     * 存放所有被配置标记的目标对象Map
     */
    private final Map<Class<?>, Object> beanMap = new ConcurrentHashMap<>();

    /**
     * 加载bean的注解列表
     */
    private static final List<Class<? extends Annotation>> BEAN_ANNOTATION
            = Arrays.asList(Component.class, Controller.class, Service.class, Repository.class);

    /**
     * 获取Bean容器实例
     *
     * @return BeanContainer
     */
    public static BeanContainer getInstance() {
        return ContainerHolder.HOLDER.instance;
    }

    private enum ContainerHolder {
        HOLDER;
        private BeanContainer instance;

        ContainerHolder() {
            instance = new BeanContainer();
        }
    }

    /**
     * 容器是否已经加载过bean
     */
    private boolean loaded = false;

    public boolean isLoaded() {
        return loaded;
    }


    /**
     * 扫描加载所有Bean
     *
     * @param packageName 包名
     */
    public synchronized void loadBeans(String packageName) {
        //判断bean容器是否已经加载过
        if (isLoaded()) {
            log.warn("BeanContainer已经被加载过！");
            return;
        }

        Set<Class<?>> classSet = ClassUtil.extractPackageClass(packageName);
        if (ValidationUtil.isEmpty(classSet)) {
            log.warn("从包下获取不到任何资源: " + packageName);
            return;
        }
        for (Class<?> cls : classSet) {
            for (Class<? extends Annotation> annotation : BEAN_ANNOTATION) {
                //如果类上标记了自定义注解
                if (cls.isAnnotationPresent(annotation)) {
                    //将目标类本身作为key,目标类的实例作为value,存放到beanMap中
                    beanMap.put(cls, ClassUtil.newInstance(cls, true));
                }
            }
        }
        loaded = true;
    }

    public Object addBean(Class<?> cls, Object bean) {
        return beanMap.put(cls, bean);
    }

    public Object removeBean(Class<?> cls) {
        return beanMap.remove(cls);
    }

    public Object getBean(Class<?> cls){
        return beanMap.get(cls);
    }

    public Set<Class<?>> getClasses(){
        return beanMap.keySet();
    }

    public Set<Object> getBeans(){
        return new HashSet<>(beanMap.values());
    }

    public Set<Class<?>> getClassesByAnnotation(Class<? extends Annotation> annotation){
        //获取beanMap所有Class对象
        Set<Class<?>> keySet = getClasses();
        if (ValidationUtil.isEmpty(keySet)){
            log.warn("容器中没有bean实例");
            return null;
        }
        //通过注解筛选被注解|标记的class对象，并添加到classSet里
        Set<Class<?>> classSet = new HashSet<>();
        for (Class<?> cls:keySet){
            //类是否被相关的注解标记
            if (cls.isAnnotationPresent(annotation)){
                classSet.add(cls);
            }
        }
        return classSet.size() > 0 ? classSet : null;
    }

    public Set<Class<?>> getClassesBySupper(Class<?> interfaceOrClass){
        //获取beanMap所有Class对象
        Set<Class<?>> keySet = getClasses();
        if (ValidationUtil.isEmpty(keySet)){
            log.warn("容器中没有bean实例");
            return null;
        }
        //判断keySet里的元素是否是传入的接口或者该类的子类，如果是，就添加到classSet里
        Set<Class<?>> classSet = new HashSet<>();
        for (Class<?> cls:keySet){
            //判断keySet里的元素是否是传入的接口或者类的子类
            if (interfaceOrClass.isAssignableFrom(cls) && !cls.equals(interfaceOrClass)){
                classSet.add(cls);
            }
        }
        return classSet.size() > 0 ? classSet : null;
    }

    public int size(){
        return beanMap.size();
    }



}
