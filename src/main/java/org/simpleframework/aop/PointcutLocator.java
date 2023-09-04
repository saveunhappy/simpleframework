package org.simpleframework.aop;

import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.ShadowMatch;

import java.lang.reflect.Method;

public class PointcutLocator {
    /**
     * Pointcut解析器,直接给他赋值上AspectJ的所有表达式,以便支持对众多表达式的解析
     */
    private final PointcutParser pointcutParser = PointcutParser.getPointcutParserSupportingSpecifiedPrimitivesAndUsingContextClassloaderForResolution(
            PointcutParser.getAllSupportedPointcutPrimitives()
    );
    /**
     * 表达式解析器
     */
    private PointcutExpression pointcutExpression;

    public PointcutLocator(String expression){
        this.pointcutExpression = pointcutParser.parsePointcutExpression(expression);
    }

    /**
     * @param targetClass: 目标类
     * @Description:判断传入的class对象是否是Aspect的目标代理类，即匹配Pointcut表达式（初筛）
     * {@code @return:} boolean
     */
    public boolean roughMatches(Class<?> targetClass) {
        //couldMatchJoinPointsInType比较坑，只能效验within
        //不能效验（execution,call,get,set），面对无法效验的表达式，直接返回true,
        //所以需要我们再精筛，这一步是先筛选出类，不是再筛选出@Controller\@Service标注的了
        //而是具体哪个类，如果不小心筛选错类，那么后面还有表达式解析的，能把初筛的错误的给过滤掉
        return pointcutExpression.couldMatchJoinPointsInType(targetClass);
    }

    /**
     * @param method:
     * @Description:判断传入的Method对象是否是Aspect的目标dialing方法，即匹配Pointcut表达式（精筛选）
     * @return: boolean
     */
    public boolean accurateMatches(Method method){
        ShadowMatch shadowMatch = pointcutExpression.matchesMethodExecution(method);
        return shadowMatch.alwaysMatches();
    }
}
