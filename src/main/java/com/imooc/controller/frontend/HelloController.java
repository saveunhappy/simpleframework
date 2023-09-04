package com.imooc.controller.frontend;

import lombok.extern.slf4j.Slf4j;
import org.simpleframework.core.annotation.Controller;

@Controller
@Slf4j
public class HelloController {
    public void hello(){
        log.info("hello aop hello方法被执行了");
    }
}
