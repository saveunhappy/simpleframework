package demo.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class AnnotationParser {
    //解析类的注解
    public static void parseTypeAnnotation() throws Exception {
        Class<?> clazz = Class.forName("demo.annotation.ImoocCourse");
        Annotation[] annotations = clazz.getAnnotations();
        for (Annotation annotation : annotations) {
            CouseInfoAnnotation couseInfoAnnotation = (CouseInfoAnnotation) annotation;
            System.out.println("课程名" + couseInfoAnnotation.couseName());
            System.out.println("课程标签" + couseInfoAnnotation.courseTag());
            System.out.println("课程简介" + couseInfoAnnotation.courseProfile());
            System.out.println("课程序号" + couseInfoAnnotation.courseIndex());
        }
    }
    //解析成员变量上的标签
    //解析类的注解
    public static void parseFieldAnnotation() throws Exception {
        Class<?> clazz = Class.forName("demo.annotation.ImoocCourse");
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            //判断成员变量中是否有指定注解类型的注解
            boolean hasAnnotation = field.isAnnotationPresent(PersonInfoAnnotation.class);
            if (hasAnnotation) {
                PersonInfoAnnotation personInfoAnnotation = field.getAnnotation(PersonInfoAnnotation.class);
                System.out.println("名字：" + personInfoAnnotation.name());
                System.out.println("性别：" + personInfoAnnotation.gender());
                System.out.println("年龄：" + personInfoAnnotation.age());
                for (String language : personInfoAnnotation.language()) {
                    System.out.println("课程名" + language);
                }
            }

        }
    }
    //解析方法注解
    public static void parseMethodAnnotation() throws ClassNotFoundException{
        Class clazz = Class.forName("demo.annotation.ImoocCourse");
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            /*
             * 判断方法中是否有指定注解类型的注解
             */
            boolean hasAnnotation = method.isAnnotationPresent(CouseInfoAnnotation.class);
            if(hasAnnotation){
                CouseInfoAnnotation courseInfoAnnotation = method.getAnnotation(CouseInfoAnnotation.class);
                System.out.println("课程名：" + courseInfoAnnotation.couseName() + "\n" +
                        "课程标签：" + courseInfoAnnotation.courseTag() + "\n" +
                        "课程简介：" + courseInfoAnnotation.courseProfile() + "\n"+
                        "课程序号：" + courseInfoAnnotation .courseIndex() + "\n");
            }
        }
    }

    public static void main(String[] args) throws Exception {
      //  parseTypeAnnotation();
        parseFieldAnnotation();
       // parseMethodAnnotation();

    }
}
