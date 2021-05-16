package demo.pattern.abstractf;

import demo.pattern.factory.entity.Keyboard;
import demo.pattern.factory.entity.Mouse;

public class AbstractFactoryDemo {
    public static void main(String[] args) {
        ComputerFactory cf = new HpComputerFactory();
        Keyboard keyboard = cf.createKeyboard();
        Mouse mouse = cf.createMouse();
        mouse.sayHi();
        keyboard.sayHello();
        ComputerFactory factory = new DellComputerFactory();
        Keyboard keyboard1 = factory.createKeyboard();
        Mouse mouse1 = factory.createMouse();
        keyboard1.sayHello();
        mouse1.sayHi();
    }
}
