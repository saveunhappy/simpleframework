package org.simpleframework.aop;

import com.imooc.controller.frontend.HelloController;
import com.imooc.controller.frontend.TestController;
import com.imooc.controller.superadmin.HeadLineOperationController;
import org.junit.jupiter.api.Test;
import org.simpleframework.core.BeanContainer;
import org.simpleframework.core.inject.DependencyInjector;

public class AspectWeaverTest {
    @Test
    public void doAopTest() throws Exception{
        BeanContainer beanContainer = BeanContainer.getInstance();
        beanContainer.loadBeans("com.imooc");

        new AspectWeaver().doAop();
        new DependencyInjector().doIoc();
        HeadLineOperationController headLineOperationController = (HeadLineOperationController) beanContainer.getBean(HeadLineOperationController.class);
        headLineOperationController.addHeadLine(null,null);

        TestController testController = (TestController) beanContainer.getBean(TestController.class);
        testController.test();

        HelloController helloController = (HelloController) beanContainer.getBean(HelloController.class);
        helloController.hello();
    }
}
