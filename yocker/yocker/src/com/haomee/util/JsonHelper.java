package com.haomee.util;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

/**
 * Created by notrace on 2015-04-06.
 */
public class JsonHelper {


    public <T> T JsonParse(String jsonStr,Class<T> clz)
    {
        Field[] fields = clz.getDeclaredFields();
        JSONObject jo = null;
        try {
            jo = new JSONObject(jsonStr);//得到JSONObject对象
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(jo == null)
        {
            return null;
        }
        T t = null;
        try {
            t = clz.newInstance();//得到需要的对象
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if(t != null)
        {
            try {
                for (int i = 0;i<fields.length;i++)//遍历所有字段
                {
                    Field field = fields[i];
                    field.set(t,jo.get(field.getName()));//对该字段赋值，以字段名去JSONObject中取值
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return t;
    }

}
