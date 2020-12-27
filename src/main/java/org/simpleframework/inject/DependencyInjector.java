package org.simpleframework.inject;

import lombok.extern.slf4j.Slf4j;
import org.simpleframework.core.BeanContainer;
import org.simpleframework.inject.annotation.Autowired;
import org.simpleframework.util.ClassUtil;
import org.simpleframework.util.ValidationUtil;

import java.lang.reflect.Field;
import java.util.Set;

@Slf4j
public class DependencyInjector {
    /**
     * bean容器
     */
    private BeanContainer beanContainer;

    public DependencyInjector(){
        beanContainer = BeanContainer.getInstance();
    }

    /**
     * 执行Ioc
     */
    public void doIoc(){
        //1.遍历Bean容器所有Class对象
        if (ValidationUtil.isEmpty(beanContainer.getClasses())){
            log.warn("BeanContainer中没有Class实例");
            return;
        }
        for (Class<?> cls:beanContainer.getClasses()){
            //2.遍历Class对象的所有成员变量
            Field[] fields = cls.getDeclaredFields();
            if (ValidationUtil.isEmpty(fields)){
                continue;
            }
            for (Field field : fields){
                //3.找出被Autowired标记的成员变量
                if (field.isAnnotationPresent(Autowired.class)){
                    Autowired autowired = field.getAnnotation(Autowired.class);
                    String autowiredValue = autowired.value();
                    //4.获取这些成员变量的类型
                    Class<?> fieldType = field.getType();
                    //5.获取这些成员变量类型在容器对应的实例
                    Object fieldValue = getFieldInstance(fieldType,autowiredValue);
                    if (fieldValue == null){
                        throw new RuntimeException("获取对象类型失败！"+fieldType.getName());
                    }else{
                        //6.通过反射将对应的成员变量实例注入到成员变量所在类的实例里
                        Object targetBean = beanContainer.getBean(cls);
                        ClassUtil.setField(field, targetBean, fieldValue, true);
                    }
                }
            }

        }

    }


    /**
     * 根据Class在beanContainer里获取其实例或者实现类
     * @param fieldType 成员变量类型
     * @return
     */
    private Object getFieldInstance(Class<?> fieldType, String autowiredValue) {
        Object fieldValue = beanContainer.getBean(fieldType);
        if (fieldValue != null){
            return fieldValue;
        }else {
            Class<?> implementedClass = getImplementClass(fieldType, autowiredValue);
            if (implementedClass !=null ){
                return beanContainer.getBean(implementedClass);
            } else {
                return null;
            }
        }
    }

    /**
     * 获取接口实现类
     * @param fieldType
     * @return
     */
    private Class<?> getImplementClass(Class<?> fieldType, String autowiredValue) {
        Set<Class<?>> classSet = beanContainer.getClassesBySupper(fieldType);
        if (!ValidationUtil.isEmpty(classSet)){
            if (ValidationUtil.isEmpty(autowiredValue)){
                if (classSet.size() == 1){
                    return classSet.iterator().next();
                } else {
                    throw new RuntimeException("没有找到唯一的"+fieldType+"实现类实例"+"请设置唯一的被@Autowired标注的属性。");
                }
            }else {
                for (Class<?> cls : classSet){
                    if (autowiredValue.equals(cls.getSimpleName())){
                        return cls;
                    }
                }
            }
        }
        return null;
    }
}
