package com.lrm.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 山水夜止
 */
public class ProcessData {

    /**
     * @param list 某String集合
     * @return 去重后的集合
     */
    public static List<String> removeDuplicateElement(List<String> list) {
        List<String> listNew = new ArrayList<String>();
        for (String str : list) {
            if (!listNew.contains(str)) {
                listNew.add(str);
            }
        }
        return listNew;
    }
}
