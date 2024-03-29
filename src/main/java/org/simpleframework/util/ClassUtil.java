package org.simpleframework.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class ClassUtil {

    public static final String FILE_PROTOCAL = "file";
    public static final String SUFFIX_CLASS = ".class";

    /*
            获取包下类的集合,用set装起来
            1.获取到类的加载器
            2.通过类加载器获取到加载的资源信息
            3.依据不同的资源类型，采用不同的方式获取资源的集合

            获取类加载器的目的：获取项目发布的实际路径，com.imooc
            为什么不让用户传入绝对路径？废话，windows和mac路径怎么能一样？
            如果打的是war包或者是jar包，根本找不到路径
            因此通用的做法是通过项目的类加载器来获取

             */
    public static Set<Class<?>> extractPackageClass(String packageName) {
//        1.获取到类的加载器
        ClassLoader classLoder = getClassLoder();
//        2.通过类加载器获取到加载的资源信息
        //url = file:/D:/devCode/simpleframework/target/classes/com/imooc/entity
        URL url = classLoder.getResource(packageName.replace(".", "/"));
        if (url == null) {
            log.warn("unable to retrive anything from package" + packageName);
            return null;
        }
//        3.依据不同的资源类型，采用不同的方式获取资源的集合
        /*
         * Finds the resource with the given name.  A resource is some data
         * (images, audio, text, etc) that can be accessed by class code in a way
         * that is independent of the location of the code.
         *
         * <p> The name of a resource is a '{@code /}'-separated path name that
         * identifies the resource. </p>
         */

        Set<Class<?>> classSet = null;
        if (url.getProtocol().equalsIgnoreCase(FILE_PROTOCAL)) {
            classSet = new HashSet<>();
            //   /D:/devCode/simpleframework/target/classes/com/imooc/entity
            //   D:/devCode/simpleframework/target/classes/com/imooc/entity
            //   D:\devCode\simpleframework\target\classes\com\imooc\entity
            //这个getPath获取的是绝对路径
//            String path = url.getPath().substring(1).replaceAll("/", "\\\\");
            String os = System.getProperty("os.name");
            String path = null;
            if (!os.toLowerCase().startsWith("win")) {
                path = url.getPath().replace("/", File.separator);
            } else {
                path = url.getPath().substring(1).replace("/", File.separator);

            }
//            String path = url.getPath().substring(1).replace("/", File.separator);
            //这里刚开始不能使用File.separator,是因为下面使用的是replaceAll，应该是用replace
            //String path1 = url.getPath().substring(1).replaceAll("/", File.separator);
            /*
            去掉协议，默认是/，就是linux下的根路径，先用substring截取，从1开始，第一个不要了，
            然后,windows是反斜线，全部给替换掉，
             */

            //packageDirectory是包名的真实路径，也是一个文件，packageName是包名。
            //在下面的调用中是要递归的去找的，所以packageDirectory会在递归中一直变
            //是一个文件，对象，所以是要传的
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
        //如果不是目录，直接return，为啥不用操心文件？因为这是第一层，必须得是目录才行，递归是在下面那个方法里面做的，如果不是目录就是文件

        if (!fileSource.isDirectory()) {
            return;
        }
        //如果是一个文件夹，则调用其list方法获取文件夹下的文件或文件夹。
        //这边我们关注的是字节码和目录，其他的都return false,如果返回的是true的话，
        //就代表提取到这个数组里面去了。
        File[] files = fileSource.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return true;
                } else {
                    //获取文件的绝对值路径
                    //D:\devCode\simpleframework\target\classes\com\imooc\entity\bo\HeadLine.class
                    String absolutePath = file.getAbsolutePath();
                    if (absolutePath.endsWith(SUFFIX_CLASS)) {
                        //如果是class文件，则直接加载
                        addToClassSet(absolutePath);
                    }
                }
                return false;
            }

            //根据class文件的绝对值路径，获取并生成class对象，并放入classSet中
            private void addToClassSet(String absolutePath) {
                //1.从class文件的绝对值路径里提取出包含了package的类名
                //如/Users/baidu/imooc/springframework/sampleframework/target/classes/com/imooc/entity/dto/MainPageInfoDTO.class
                //需要弄成com.imooc.entity.dto.MainPageInfoDTO
                absolutePath = absolutePath.replace(File.separator, ".");
                //你传入的包名，应该就是com.imooc.entity，从这里开始截取
                //这里虽然盘符变了：D:.devCode.simpleframework.target.classes.com.imooc.entity.bo.HeadLine.class，
                // 但是，我们截取的是包名，前面的都不要，这里不用处理
                String className = absolutePath.substring(absolutePath.indexOf(packageName));
                //去掉.class文件 变成这样：com.imooc.entity.bo.HeadLine
                className = className.substring(0, className.lastIndexOf("."));
                //2.通过反射机制获取对应的Class对象并加入到classSet里,是通过Class.forName()
                Class<?> targetClass = loadClass(className);
                emptyClassSet.add(targetClass);
//                System.out.println(targetClass.toString());

            }

        });
        if (files != null) {
            for (File f : files) {
                //递归调用
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

    /**
     *
     * @param field 成员变量
     * @param target 类实例
     * @param value 成员变量的值
     * @param accessible 是否允许访问私有属性
     */
    public static void setField(Field field, Object target, Object value, boolean accessible){
        field.setAccessible(accessible);
        try {
            field.set(target,value);
        } catch (IllegalAccessException e) {
            log.error("setField error",e);
            throw new RuntimeException(e);
        }
    }

    /*
    获取类加载器
     */
    public static ClassLoader getClassLoder() {
        return Thread.currentThread().getContextClassLoader();
    }

    public static void main(String[] args) {
        extractPackageClass("com.imooc.entity");
    }
}
