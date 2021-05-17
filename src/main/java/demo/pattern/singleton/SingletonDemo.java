package demo.pattern.singleton;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class SingletonDemo {
    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        System.out.println(EnumStarvingSingleton.getInstance());
        Class clazz = EnumStarvingSingleton.class;
        Constructor constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        EnumStarvingSingleton enumStarvingSingleton = (EnumStarvingSingleton)constructor.newInstance();
        //你是突破了外围防线，内围的防线没有突破，因为内围是枚举
        System.out.println(enumStarvingSingleton);
        System.out.println(enumStarvingSingleton.getInstance());
//        System.out.println(StarvingSingleton.getInstance());
//        System.out.println(StarvingSingleton.getInstance());
    }
}
