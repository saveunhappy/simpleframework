package org.simpleframework.core;

import com.imooc.controller.DispatcherServlet;
import com.imooc.controller.frontend.MainPageController;
import com.imooc.service.solo.HeadLineService;
import com.imooc.service.solo.impl.HeadLineServiceImpl;
import org.junit.jupiter.api.*;
import org.simpleframework.core.annotation.Controller;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BeanContainerTest {
    private static BeanContainer beanContainer;

    @BeforeAll
    static void init() {
        beanContainer = BeanContainer.getInstance();
    }

    @Order(1)
    @Test
    public void loadBeanTest() throws Exception {
        Assertions.assertFalse(beanContainer.isLoaded());
        beanContainer.loadBeans("com.imooc");
        Assertions.assertEquals(6, beanContainer.size());
        Assertions.assertTrue(beanContainer.isLoaded());
    }

    @DisplayName("根据类获取其实例：getBeanTest")
    @Order(2)
    @Test
    public void getBeanTest() throws Exception {
        MainPageController controller =
                (MainPageController) beanContainer.getBean(MainPageController.class);
        Assertions.assertEquals(true,controller instanceof MainPageController);
        DispatcherServlet dispatcherServlet = (DispatcherServlet)beanContainer.getBean(DispatcherServlet.class);
        Assertions.assertNull(dispatcherServlet);
    }
    @DisplayName("根据注解获取对应的实例：getClassByAnnotationTest")
    @Order(3)
    @Test
    public void getClassesByAnnotationTest() throws Exception{
        Assertions.assertTrue(beanContainer.isLoaded());
        Assertions.assertEquals(3,beanContainer.getClassesByAnnotation(Controller.class).size());
    }
    @DisplayName("根据注解获取对应的实例：getClassByAnnotationTest")
    @Order(3)
    @Test
    public void getClassesBySuperTest() throws Exception{
        Assertions.assertTrue(beanContainer.isLoaded());
        Assertions.assertTrue(beanContainer.getClassesBySuper(HeadLineService.class).contains(HeadLineServiceImpl.class));
    }

}
