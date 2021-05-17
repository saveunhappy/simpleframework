package org.simpleframework.util;

import com.imooc.controller.frontend.MainPageController;
import org.junit.jupiter.api.*;

import java.util.Set;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClassUtilTest {

    @DisplayName("提取目标类方法：extractPackageClassTest")
    @Test
    public void extractPackageClassTest(){
        Set<Class<?>> classSet = ClassUtil.extractPackageClass("com.imooc.entity");
        System.out.println(classSet);
        Assertions.assertEquals(4,classSet.size());
    }
    @Test
    public void SimpleNameTest() throws Exception{
        Class<?> clazz = MainPageController.class;
        String name = clazz.getName();
        String simpleName = clazz.getSimpleName();
        System.out.println("name " + name);
        System.out.println("simpleName " + simpleName);
    }
}
