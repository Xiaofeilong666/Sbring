package org.simpleframework.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class ClassUtil {

    public static final String File_PROTOCOL = "file";

    /**
     * 获取包下类集合
     * @param packageName
     * @return
     */
    public static Set<Class<?>> extractPackageClass(String packageName){
        //获取类加载器
        ClassLoader  classLoader = getClassLoader();
        //通过类加载器获取加载的资源
        URL url = classLoader.getResource(packageName.replace(".","/"));
        if (url == null){
            log.warn("从package下面获取不到任何资源: " + packageName);
            return null;
        }
        //依据不同的资源类型，采用不同的方式获取资源的集合
        Set<Class<?>> classSet = null;
        //过滤文件类型的资源
        if (url.getProtocol().equalsIgnoreCase(File_PROTOCOL)){
            classSet = new HashSet<Class<?>>();
            File packageDirectory = new File(url.getPath());
            extractClassFile(classSet, packageDirectory, packageName);
        }

        return classSet;


    }

    /**
     * 获取Class对象
     *
     * @param className class全名 = package + 类名
     * @return Class
     */
    public static Class<?> loadClass(String className){
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            log.error("类加载异常:",e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 递归获取目标package里面的所有class文件（包括子package里的class文件）
     *
     * @param emptyClassSet 装载目标类的集合
     * @param fileSource 文件或目录
     * @param packageName 包名
     */
    private static void extractClassFile(Set<Class<?>> emptyClassSet, File fileSource, String packageName) {
        if (!fileSource.isDirectory()){
            return;
        }
        //如果是文件夹，则调用listFiles方法获取文件夹下的文件或文件夹
        File[] files = fileSource.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isDirectory()){
                    return true;
                }else {
                    //获取文件的绝对值路径
                    String absolutePath = file.getAbsolutePath();
                    if (absolutePath.endsWith(".class")){
                        //如果是class文件，则直接加载
                        addToClassSet(absolutePath);
                    }
                }
                return false;
            }

            private void addToClassSet(String absolutePath) {
                //从class文件的绝对路径里提取出包含package的类名
                absolutePath = absolutePath.replace(File.separator, ".");
                String className = absolutePath.substring(absolutePath.indexOf(packageName));
                className = className.substring(0, className.lastIndexOf("."));
                //通过反射机制获取对应的Class对象
                Class<?> targetClass = loadClass(className);

                emptyClassSet.add(targetClass);

            }
        });
        if (files != null){
            for (File f : files){
                //递归调用
                extractClassFile(emptyClassSet,f,packageName);
            }
        }
    }

    /**
     * 获取classLoader
     * @return
     */
    public static ClassLoader getClassLoader(){
        return Thread.currentThread().getContextClassLoader();
    }


    /**
     * 实例化class
     * @param cls Class
     * @param accessible 是否支持创建出私有class对象的实例
     * @param <T> class的类型
     * @return 类的实例化对象
     */
    public static <T> T newInstance(Class<?> cls, boolean accessible) {
        try {
            Constructor constructor = cls.getDeclaredConstructor();
            constructor.setAccessible(accessible);
            return  (T)constructor .newInstance();
        } catch (Exception e) {
            log.error("初始化类实例异常",e);
            throw new RuntimeException(e);
        }
    }

    public static void setField(Field field, Object target, Object value, boolean accessible){
        field.setAccessible(accessible);
        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            log.error("设置属性失败！",e);
            throw new RuntimeException(e);
        }
    }

}
