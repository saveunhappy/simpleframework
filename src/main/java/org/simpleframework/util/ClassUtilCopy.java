package org.simpleframework.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class ClassUtilCopy {
    public static final String FILE_PROTOCAL = "file";
    public static final String SUFFIX_CLASS = ".class";

    public static Set<Class<?>> extractPackageClass(String packageName) throws ClassNotFoundException {
//        1.获取到类的加载器
        ClassLoader classLoader = getClassLoader();
//        2.通过类加载器获取到加载的资源信息
        URL url = classLoader.getResource(packageName.replace(".", "/"));
        if (url == null) {
            log.warn("unable to retrive anything from package" + packageName);
            return null;
        }
        Set<Class<?>> classSet = null;

//        3.依据不同的资源类型，采用不同的方式获取资源的集合
        if (url.getProtocol().equals(FILE_PROTOCAL)) {
            classSet = new HashSet<>();
            String path = url.getPath().substring(1).replace("/", File.separator);
            File packageDirectory = new File(path);
            extractClassFile(classSet, packageDirectory, packageName);
        }
        return classSet;
    }

    /**
     * @param emptyClassSet 装载目标类的集合
     * @param fileSource    文件或者目录
     * @param packageName   包名
     */
    private static void extractClassFile(Set<Class<?>> emptyClassSet, File fileSource, String packageName) {
        if(!fileSource.isDirectory()){
            return;
        }
        File[] files = fileSource.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if(file.isDirectory()){
                    //如果是目录的话，就收集起来，等着下面的递归调用，直到里面的都是文件位置
                    return true;
                }else{
                    //那么就是文件了，就要添加到set的class中去了
                    String absolutePath = file.getAbsolutePath();
                    //文件可能有别的东西，比如有些人瞎提交代码，把.sql文件都提交进来java的目录中去了
                    // 那么就不行了啊，我们只要class文件。
                    if(absolutePath.endsWith(SUFFIX_CLASS)){
                        addToClassSet(absolutePath);
                    }
                }
                return false;
            }
            private void addToClassSet(String absolutePath) {

                absolutePath = absolutePath.replace(File.separator,".");
                String className = absolutePath.substring(absolutePath.indexOf(packageName));
                className = className.substring(0, className.lastIndexOf("."));
                Class<?> targetClass = loadClass(className);
                emptyClassSet.add(targetClass);
                System.out.println(targetClass);
            }
        });
        if(files!=null){
            for (File f : files) {
                extractClassFile(emptyClassSet, f, packageName);
            }
        }
    }



    public static <T> T newInstance(Class<?> clazz, boolean accessible) {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(accessible);
            return (T) constructor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * @param className class全名 = package + 类名
     * @return
     */
    public static Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            log.error("load class error", e);
            throw new RuntimeException(e);
        }
    }


    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public static void main(String[] args) throws ClassNotFoundException {
        extractPackageClass("com.imooc.entity");
    }
}
