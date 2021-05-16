package demo.reflect;

public class ReflectTarget {

    public String name;
    protected int index;
    char type;
    private String targetInfo;

    @Override
    public String toString() {
        return "ReflecTarget{" +
                "name='" + name + '\'' +
                ", index=" + index +
                ", type=" + type +
                ", targetInfo='" + targetInfo + '\'' +
                '}';
    }

    //----------构造函数--------------
    ReflectTarget(String str) {
        System.out.println("(默认)的构造方法 str = " + str);
    }

    public ReflectTarget() {
        System.out.println("调用了无参构造函数");
    }

    public ReflectTarget(char name) {
        System.out.println("调用了一个带参数的构造方法，参数值为：" + name);
    }

    public ReflectTarget(String name, int index) {
        System.out.println("调用了带有多个参数的构造方法，参数值为【目标名】" + name + "【序号】" + index);
    }

    protected ReflectTarget(boolean n) {
        System.out.println("受保护的构造方法+n:" + n);
    }

    //私有的构造方法
    private ReflectTarget(int index) {
        System.out.println("私有的构造方法，序号:" + index);
    }
    //***************成员方法***************//
    public void show1(String s){
        System.out.println("调用了公有的，String参数的show1(): s = " + s);
    }
    protected void show2(){
        System.out.println("调用了受保护的，无参的show2()");
    }
    void show3(){
        System.out.println("调用了默认的，无参的show3()");
    }
    private String show4(int index){
        System.out.println("调用了私有的，并且有返回值的，int参数的show4(): index = " + index);
        return "show4result";
    }
    public static void main(String[] args) throws ClassNotFoundException {
        //第一种方式获取Class对象
        ReflectTarget target = new ReflectTarget();
        Class clazz1 = target.getClass();
        System.out.println("1st:" + clazz1.getName());
        //第二种方式获取Class对象
        Class clazz2 = ReflectTarget.class;
        System.out.println("2nt:" + clazz2.getName());
        System.out.println(clazz1 == clazz2);
        //第三种方式获取Class对象
        Class<?> clazz3 = Class.forName("demo.reflect.ReflectTarget");
        System.out.println("3rd:" + clazz3.getName());
        System.out.println(clazz2 == clazz3);


    }

}
