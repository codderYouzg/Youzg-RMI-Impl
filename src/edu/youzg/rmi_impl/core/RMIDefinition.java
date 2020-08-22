package edu.youzg.rmi_impl.core;

/**
 * RMI方法信息：<br/>
 * 用于保存一个类的 Class对象 和 一个实体类对象<br/>
 * 便于我们之后的调用
 */
public class RMIDefinition {
    private Class<?> klass;
    private Object object;

    public RMIDefinition() {
    }

    public RMIDefinition(Class<?> klass, Object object) {
        this.klass = klass;
        this.object = object;
    }

    public Class<?> getKlass() {
        return klass;
    }

    public void setKlass(Class<?> klass) {
        this.klass = klass;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

}