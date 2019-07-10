package com.walid.dynamic.lib;

import android.util.SparseArray;

import com.walid.promise.Promise;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Author   : walid
 * Date     : 2019-07-09  11:18
 * Describe :
 */
public class DynamicEvent {

    // key:Object value：<key:Integer value:Promise>
    private Map<Object, SparseArray<Promise<DynamicBean, String>>> listenerMap = new HashMap<>();

    private DynamicEvent() {
    }

    private static class SingletonHolder {
        static DynamicEvent instance = new DynamicEvent();
    }

    public static Promise<DynamicBean, String> register(Object o, int key) {
        Map<Object, SparseArray<Promise<DynamicBean, String>>> listenerMap = SingletonHolder.instance.listenerMap;
        SparseArray<Promise<DynamicBean, String>> sparseArray = listenerMap.get(o) != null ? listenerMap.get(o) : new SparseArray<>();
        Promise<DynamicBean, String> promise = new Promise<>((resolve, reject) -> {
        });
        sparseArray.put(key, promise);
        listenerMap.put(o, sparseArray);
        return promise;
    }

    public static void unRegister(Object o) {
        SingletonHolder.instance.listenerMap.remove(o);
    }

    public static void post(DynamicBean globalBean) {
        DynamicEvent globalEvent = SingletonHolder.instance;
        Set<Map.Entry<Object, SparseArray<Promise<DynamicBean, String>>>> entrySet = globalEvent.listenerMap.entrySet();
        for (Map.Entry<Object, SparseArray<Promise<DynamicBean, String>>> entry : entrySet) {
            Promise<DynamicBean, String> promise = entry.getValue().get(globalBean.key);
            if (promise != null) {
                promise.resolve.run(globalBean);
            }
        }
    }

    public static class DynamicBean {

        public int key;
        public Object value;

        public DynamicBean(int eventKey, Object value) {
            this.key = eventKey;
            this.value = value;
        }

    }

}
