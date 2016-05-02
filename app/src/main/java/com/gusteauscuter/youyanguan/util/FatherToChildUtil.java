package com.gusteauscuter.youyanguan.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Z on 2016/5/2 0002.
 */
public class FatherToChildUtil {
    /*
    * 将父类所有的属性COPY到子类中。
    * 类定义中child一定要extends father；
    * 而且child和father一定为严格javabean写法，属性为deleteDate，方法为getDeleteDate
    */
    public static void fatherToChild (Object father,Object child){
        if(!(child.getClass().getSuperclass()==father.getClass())){
            System.err.println("child不是father的子类");
        }
        Class fatherClass= father.getClass();
        Field ff[]= fatherClass.getDeclaredFields();
        for(int i=0;i<ff.length;i++){
            Field f=ff[i];//取出每一个属性，如deleteDate
            Class type=f.getType();
            try {
                Method m = fatherClass.getMethod("get"+upperHeadChar(f.getName()));//方法getDeleteDate
                Object obj=m.invoke(father);//取出属性值
                f.set(child,obj);
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 首字母大写，in:deleteDate，out:DeleteDate
     */
    private static String upperHeadChar(String in){
        String head=in.substring(0,1);
        String out=head.toUpperCase()+in.substring(1,in.length());
        return out;
    }

}
