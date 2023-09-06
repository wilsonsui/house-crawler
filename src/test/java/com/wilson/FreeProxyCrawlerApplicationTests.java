package com.wilson;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//@SpringBootTest
class FreeProxyCrawlerApplicationTests {

    @Test
    void contextLoads() {
//        String input = "低楼层 (共15层) | 2007年建 | 1室1厅 | 53.09平米 | 南";
        String input = "低楼层 (共15层) |   1室1厅 |2007年建 | 53.09平米 | 南";
//        String input = "低楼层 (共15层)  | 1室1厅 | 53.09平米 | 南";
        // 定义正则表达式、
        // 定义正则表达式
        // 定义正则表达式
        String regex = "\\((.*?)层\\).*?(\\d+年建)?.*?(\\d+室\\d+厅)(.*?\\b\\d{4}年建)?[^\\d]*(\\d+\\.\\d+平米).*?\\|(.*?)$";

        // 创建 Pattern 对象
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);

        // 创建 Matcher 对象
        Matcher matcher = pattern.matcher(input);

        // 查找匹配的字符串
        if (matcher.find()) {
            // 提取属性
            String floor = matcher.group(1);
            String yearBuilt1 = matcher.group(2);
            String roomLayout = matcher.group(3);
            String yearBuilt2 = matcher.group(4);
            String area = matcher.group(5);
            String orientation = matcher.group(6);

            // 输出结果
            System.out.println("楼层: " + floor);
            System.out.println("建造年份: " + getYearBuilt(yearBuilt1, yearBuilt2));
            System.out.println("房间布局: " + roomLayout);
            System.out.println("面积: " + area);
            System.out.println("朝向: " + orientation);
        }
    }

    private static String getYearBuilt(String yearBuilt1, String yearBuilt2) {
        if (yearBuilt1 != null) {
            return yearBuilt1.trim();
        } else if (yearBuilt2 != null) {
            return yearBuilt2.trim();
        } else {
            return "未知";
        }
    }
}


