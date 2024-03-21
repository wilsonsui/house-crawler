package com.wilson.douban;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author wilson
 */
@Data
public class DouBanExcel {
    @ExcelProperty(value = "标题")
    private String title;

    @ExcelProperty(value = "链接")
    private String url;
    @ExcelProperty(value = "回复数")
    private String count;
    @ExcelProperty(value = "最后回复时间")
    private String time;
    @ExcelProperty(value = "具体内容")
    private String content;

}
