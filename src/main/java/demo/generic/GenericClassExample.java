package demo.generic;

import lombok.Data;

@Data
public class GenericClassExample<T> {
    private T member;
    public GenericClassExample(T member) {
        this.member = member;
    }
    public T handleSomething(T target){
        return target;
    }
    public static <E> void printArray(E[] input){
        for (E element : input) {
            System.out.printf("%s",element);
            System.out.printf(" ");
        }
        System.out.println();
    }
    //这证明啥，证明泛型方法并不受制于泛型T，而方法的返回值T和泛型类上边的一样，那么他就不是泛型方法
    //<A>这就是泛型方法的标识符，有了这个才能证明这个是一个泛型方法,能不一样说明就跟static一样
    //static脱离了类，这个就是脱离了泛型
    public <A> void printList(A[] input){
        for (A element : input) {
            System.out.printf("%s",element);
            System.out.print(" ");
        }
        System.out.println();
    }
}
