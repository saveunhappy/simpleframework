package demo.annotation;
@CouseInfoAnnotation(couseName = "java面试",courseTag = "面试",courseProfile = "提高内功")
public class ImoocCourse {
    @PersonInfoAnnotation(name = "小翔哥",language = {"java","groovy","kotlin"})
    private String author;
    @CouseInfoAnnotation(couseName = "商铺",courseTag = "实战",courseProfile = "增加项目经验")
    public void getCourseInfo(){

    }
}
