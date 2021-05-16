package demo.generic;


public class GenericDemo {
    public static void handleMember1(GenericClassExample<Integer> integerGenericClassExample){
        Integer result = 111 + integerGenericClassExample.getMember();
        System.out.println(result);
    }
                                                          //要求子类是integer
    public static void handleMember2(GenericClassExample<? super Integer> integerGenericClassExample){
        Integer result = 111 + (Integer) integerGenericClassExample.getMember();
        System.out.println(result);
    }
                                                        //要求父类是Number
    public static void handleMember(GenericClassExample<? extends Number> integerGenericClassExample){
        Integer result = 111 + (Integer) integerGenericClassExample.getMember();
        System.out.println(result);
    }
    public static void main(String[] args) {
        GenericClassExample<String> stringExample = new GenericClassExample<>("abc");
        GenericClassExample<Number> integerExample = new GenericClassExample<>(123);
        System.out.println(stringExample.getMember().getClass());
        System.out.println(integerExample.getMember().getClass());
        System.out.println(stringExample.getClass());
        System.out.println(integerExample.getClass());
        Integer[] integers = {1,2,3,4,5,6};
        Double[] doubles = {1.1,1.2,1.3,1.4,1.5};
        Character[] characters = {'A','B','C','D','E','F'};
        GenericClassExample.printArray(integers);
        GenericClassExample.printArray(doubles);
        GenericClassExample.printArray(characters);
        System.out.println("------------------------");
        stringExample.printList(integers);
        stringExample.printList(doubles);
        stringExample.printList(characters);
    //    handleMember(stringExample);

    }
}
