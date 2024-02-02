package com.wilson.chrome.service;

import com.wilson.util.HttpRequestUtil;

/**
 * @author wilson
 */
public class TestGov {
    public static void main(String[] args) {
        String string = HttpRequestUtil.doRequest("https://www.drc.gov.cn/DocViewH5.aspx?chnid=379&leafid=1338&docid=2907353");
        System.out.println(string);
    }
}
