package org.simpleframework.inject;

import com.imooc.controller.frontend.MainPageController;
import com.imooc.service.combine.HeadLineShopCategoryCombineServiceImpl;
import com.imooc.service.combine.HeadLineShopCategoryCombineServiceImpl2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.simpleframework.core.BeanContainer;
import org.simpleframework.core.inject.DependencyInjector;

public class DependencyInjectorTest {
    @DisplayName("依赖注入doIoc")
    @Test
    public void doIocTest(){
        BeanContainer beanContainer = BeanContainer.getInstance();
        beanContainer.loadBeans("com.imooc");
        Assertions.assertTrue(beanContainer.isLoaded());
        MainPageController mainPageController = (MainPageController) beanContainer.getBean(MainPageController.class);
        Assertions.assertTrue(mainPageController instanceof MainPageController);
        //这个时候只是装载了，还没有依赖注入
        Assertions.assertNull(mainPageController.getHeadLineShopCategoryCombineService());
        //这里执行依赖注入
        new DependencyInjector().doIoc();

        Assertions.assertNotNull(mainPageController.getHeadLineShopCategoryCombineService());
        Assertions.assertTrue(mainPageController.getHeadLineShopCategoryCombineService() instanceof HeadLineShopCategoryCombineServiceImpl);
        Assertions.assertFalse(mainPageController.getHeadLineShopCategoryCombineService() instanceof HeadLineShopCategoryCombineServiceImpl2);

    }
}
