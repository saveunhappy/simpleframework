package demo.pattern.abstractf;

import demo.pattern.factory.entity.Keyboard;
import demo.pattern.factory.entity.Mouse;

public interface ComputerFactory {
    Mouse createMouse();
    Keyboard createKeyboard();
}
