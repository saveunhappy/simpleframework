package demo.pattern.singleton;

import java.lang.reflect.Constructor;

public class EnumStarvingSingleton {
    private EnumStarvingSingleton(){}
    public static EnumStarvingSingleton getInstance(){
        return ContainerHolder.HOLDER.instance;
    }
    private enum ContainerHolder{
        HOLDER;
        private EnumStarvingSingleton instance;
        ContainerHolder(){
            instance = new EnumStarvingSingleton();
        }
    }

    public static void main(String[] args) throws Exception{
        Class<?> clazz = ContainerHolder.class;
        Constructor constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        System.out.println(EnumStarvingSingleton.getInstance());
        System.out.println(constructor.newInstance());
//        System.out.println(ContainerHolder.HOLDER);
//        System.out.println(ContainerHolder.HOLDER.name());
//        System.out.println(ContainerHolder.HOLDER.ordinal());
//        System.out.println(ContainerHolder.HOLDER.toString());

    }
}
