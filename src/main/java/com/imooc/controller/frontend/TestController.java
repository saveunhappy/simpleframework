package com.imooc.controller.frontend;

import lombok.extern.slf4j.Slf4j;
import org.simpleframework.core.annotation.Controller;

@Controller
@Slf4j
public class TestController {

    public void test(){
        log.info("Test aop hello方法被执行了");
    }
}
