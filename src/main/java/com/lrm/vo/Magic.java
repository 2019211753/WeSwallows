package com.lrm.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Magic {
    public static final int PAGE_SIZE = 10;
    public static final Integer RECOMMENDED_QUESTIONS_SIZE = 6;
    public static final Integer HIDE_STANDARD1 = 6;
    public static final Integer HIDE_STANDARD2 = 2;

    public static final Map<String, List<String>> ACADEMIES = createMap();

    public static Map<String, List<String>> createMap()
    {
        Map<String, List<String>> map = new HashMap<>(11);
        List<String> majors = new ArrayList<>();

        majors.add("通信工程");
        majors.add("电子信息工程");
        map.put("信息与通信工程学院", majors);

        majors = new ArrayList<>();
        majors.add("电子科学与技术");
        majors.add("电子信息科学与技术");
        majors.add("光电信息科学与工程");
        majors.add("电磁场与无线技术");
        map.put("电子工程学院", majors);

        majors = new ArrayList<>();
        majors.add("数据科学与大数据技术");
        majors.add("网络工程");
        majors.add("计算机科学与技术");
        majors.add("软件工程");
        map.put("计算机学院", majors);

        majors = new ArrayList<>();
        majors.add("信息工程");
        majors.add("智能科学与技术");
        majors.add("人工智能");
        majors.add("自动化");
        majors.add("测控技术与仪器");
        map.put("人工智能学院", majors);

        majors = new ArrayList<>();
        majors.add("工业设计");
        majors.add("数字媒体技术");
        majors.add("数字媒体艺术");
        map.put("数字媒体与设计艺术学院", majors);

        majors = new ArrayList<>();
        majors.add("邮政工程");
        majors.add("邮政管理");
        majors.add("电子商务");
        majors.add("机械工程");
        map.put("现代邮政学院", majors);

        majors = new ArrayList<>();
        majors.add("信息安全");
        majors.add("网络空间安全");
        map.put("网络空间安全学院", majors);

        majors = new ArrayList<>();
        majors.add("电磁场与无线技术");
        map.put("光电信息学院", majors);

        majors = new ArrayList<>();
        majors.add("信息管理与信息系统");
        majors.add("工商管理");
        majors.add("会计学");
        majors.add("经济学");
        majors.add("公共事业管理");
        map.put("经济管理学院", majors);

        majors = new ArrayList<>();
        majors.add("英语");
        majors.add("法学");
        majors.add("日语");
        map.put("人文学院", majors);

        majors = new ArrayList<>();
        majors.add("数学与应用数学");
        majors.add("信息与计算科学");
        majors.add("应用物理学");
        map.put("理学院", majors);

        majors = new ArrayList<>();
        majors.add("电信工程及管理");
        majors.add("物联网工程");
        majors.add("电子商务及法律");
        map.put("国际学院", majors);

        return map;
    }

}
