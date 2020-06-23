package cc.ives.aeg.util;

import android.content.pm.PackageManager;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cc.ives.aeg.AEGContext;
import cc.ives.aeg.AutoEntryGenerator;
import cc.ives.aeg.annotation.EntryClassInfo;
import cc.ives.aeg.annotation.EntryOnClick;

/**
 * @author wangziguang
 * @date 2020/5/24 0024
 * @description
 */
public class AegHelper {

    /**
     * 查找指定类的入口方法
     * @param entryClass
     * @return
     */
    private static Method findEntryMethod(Class entryClass){
        Method[] methods = entryClass.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(EntryOnClick.class)){
                return method;
            }
        }
        return null;
    }

    /**
     * 实例化或者静态调用该类的入口方法
     * @param entryClass
     */
    public static void invokeEntryMethod(Class entryClass){
        Method entryMethod = findEntryMethod(entryClass);
        if (entryMethod == null){
            JLog.w("AegHelper", "invokeEntryMethod() you have no declared any entry method with annotation EntryOnClick");
            return;
        }
        entryMethod.setAccessible(true);

        if (Modifier.isStatic(entryMethod.getModifiers())){

            try {
                entryMethod.invoke(null);// 必须是无参方法
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

        }else {

            try {
                Object obj = entryClass.newInstance();// 必须有非私有的无参构造器
                entryMethod.invoke(obj);// 必须是无参方法
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取该包名下的所有类
     * @return
     */
    public static Set<String> getAllClassUnderPackage(){
        try {
            return ClassUtil.getFileNameByPackageName(AEGContext.getAppContext(), AEGContext.getAppContext().getPackageName());

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new HashSet<>();
    }

    /**
     * 返回扫描到的类信息，此方法会阻塞当前线程。
     * 调用前必须要保证Context已经初始化
     * @return
     */
    public static List<EntryClassInfo> getEntryClassListSync(){

        List<EntryClassInfo> infoList = null;
        infoList = AutoEntryGenerator.scan();

        Collections.sort(infoList, new Comparator<EntryClassInfo>() {
            @Override
            public int compare(EntryClassInfo o1, EntryClassInfo o2) {
                return o1.getIndexTime() - o2.getIndexTime();
            }
        });

        return infoList;
    }
}
