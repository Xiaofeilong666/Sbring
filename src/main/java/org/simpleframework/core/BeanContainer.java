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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BeanContainer {
    /**
     * 存放所有被配置标记的目标对象Map
     */
    private final Map<Class<?>,Object> beanMap =  new ConcurrentHashMap<>();

    /**
     * 加载bean的注解列表
     */
    private static final List<Class<? extends Annotation>> BEAN_ANNOTATION
            = Arrays.asList(Component.class, Controller.class, Service.class, Repository.class);

    /**
     * 获取Bean容器实例
     * @return BeanContainer
     */
    public static BeanContainer getInstance(){
        return ContainerHolder.HOLDER.instance;
    }

    private enum ContainerHolder{
        HOLDER;
        private BeanContainer instance;
        ContainerHolder(){
            instance = new BeanContainer();
        }
    }

    /**
     * 容器是否已经加载过bean
     */
    private boolean loaded = false;
    public boolean isLoaded(){
        return loaded;
    }



    /**
     * 扫描加载所有Bean
     *
     * @param packageName 包名
     */
    public synchronized void loadBeans(String packageName){
        //判断bean容器是否已经加载过
        if(isLoaded()){
            log.warn("BeanContainer已经被加载过！");
            return;
        }

        Set<Class<?>> classSet = ClassUtil.extractPackageClass(packageName);
        if (ValidationUtil.isEmpty(classSet)){
            log.warn("从包下获取不到任何资源: "+packageName);
            return;
        }
        for (Class<?> cls : classSet){
            for (Class<? extends Annotation> annotation:BEAN_ANNOTATION){
                //如果类上标记了自定义注解
                if (cls.isAnnotationPresent(annotation)){
                    //将目标类本身作为key,目标类的实例作为value,存放到beanMap中
                    beanMap.put(cls, ClassUtil.newInstance(cls,true));
                }
            }
        }
        loaded = true;
    }

}
