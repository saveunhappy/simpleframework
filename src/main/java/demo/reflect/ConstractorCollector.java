package demo.reflect;

/*
 * 通过Class对象可以获取某个类中的：构造方法；
 *
 * 获取构造方法：
 *         1).批量的方法：
 *             public Constructor[] getConstructors()：所有"公有的"构造方法
              public Constructor[] getDeclaredConstructors()：获取所有的构造方法(包括私有、受保护、默认、公有)
 *         2).获取单个的方法，并调用：
 *             public Constructor getConstructor(Class... parameterTypes):获取单个的"公有的"构造方法：
 *             public Constructor getDeclaredConstructor(Class... parameterTypes):获取"某个构造方法"可以是私有的，或受保护、默认、公有；
 *
 *             调用构造方法：
 *             Constructor-->newInstance(Object... initargs)
 */
public class ConstractorCollector {
    public static void main(String[] args) throws Exception {
        Class<?> clazz = Class.forName("demo.reflect.ReflectTarget");
        //1.获取所有的公有构造方法：
        System.out.println("------------------获取所有的公有构造方法----------------");
//        Constructor[] conArray = clazz.getConstructors();
        //val conArray = clazz.getConstructors();
        var conArray = clazz.getConstructors();
        for (var c : conArray) {
            System.out.println(c);
        }
        //获取所有的构造方法
        System.out.println("------------------获取所有的构造方法----------------");
        conArray = clazz.getDeclaredConstructors();
        for (var c : conArray) {
            System.out.println(c);
        }
        //获取单个带参数的公有方法
        System.out.println("-----------获取公有的，带有两个参数的构造方法");
        var con = clazz.getConstructor(String.class, int.class);
        System.out.println("con = " + con);
        //获取单个私有的构造方法
        System.out.println("--------------------获取私有构造方法----------");
        con = clazz.getDeclaredConstructor(int.class);
        System.out.println("private con = " + con);
        con.setAccessible(true);
        ReflectTarget o = (ReflectTarget) con.newInstance(1);

    }
}
