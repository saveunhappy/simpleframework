package org.simpleframework.aop;

import org.junit.jupiter.api.Test;
import org.simpleframework.aop.aspect.AspectInfo;
import org.simpleframework.aop.mock.*;

import java.util.ArrayList;
import java.util.List;

public class AspectListExecutorTest {
    @Test
    public void sortTest() throws Exception{
        List<AspectInfo> aspectInfoList = new ArrayList<>();
        aspectInfoList.add(new AspectInfo(3,new Mock1(),null));
        aspectInfoList.add(new AspectInfo(5,new Mock2(),null));
        aspectInfoList.add(new AspectInfo(2,new Mock3(),null));
        aspectInfoList.add(new AspectInfo(4,new Mock4(),null));
        aspectInfoList.add(new AspectInfo(1,new Mock5(),null));
        AspectListExecutor aspectListExecutor = new AspectListExecutor(AspectListExecutorTest.class,aspectInfoList);
        List<AspectInfo> sortedAspectInfoList = aspectListExecutor.getAspectInfoList();
        for (AspectInfo aspectInfo : sortedAspectInfoList) {
            System.out.println(aspectInfo.getAspectObject().getClass().getName());
        }
    }
}
