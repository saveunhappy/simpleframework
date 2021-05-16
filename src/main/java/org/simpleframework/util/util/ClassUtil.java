package org.simpleframework.util.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileFilter;
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
            String path = url.getPath().substring(1).replaceAll("/", "\\\\");
            //这里不能使用File.separator,因为这里只是一个斜线，在这里，还是需要转义的，需要两个斜线
            //String path1 = url.getPath().substring(1).replaceAll("/", File.separator);
            /*
            去掉协议，默认是/，就是linux下的根路径，先用substring截取，从1开始，第一个不要了，
            然后,windows是反斜线，全部给替换掉，
             */
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
        File[] files = fileSource.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return true;
                } else {
                    //获取文件的绝对值路径
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
                absolutePath = absolutePath.replace(File.separator,".");
                //你传入的包名，应该就是com.imooc.entity，从这里开始截取
                //这里虽然盘符变了：D:.devCode.simpleframework.target.classes.com.imooc.entity.bo.HeadLine.class，
                // 但是，我们截取的是包名，前面的都不要，这里不用处理
                String className = absolutePath.substring(absolutePath.indexOf(packageName));
                //去掉.class文件
                className = className.substring(0,className.lastIndexOf("."));
                //2.通过反射机制获取对应的Class对象并加入到classSet里
                Class<?> targetClass = loadClass(className);
                emptyClassSet.add(targetClass);

            }

        });
        if (files != null){
            for (File f : files) {
                //递归调用
                extractClassFile(emptyClassSet,f,packageName);
            }
        }

    }

    /**
     *
     * @param className   class全名 = package + 类名
     * @return
     */
    public static Class<?> loadClass(String className){
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            log.error("load class error",e);
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
