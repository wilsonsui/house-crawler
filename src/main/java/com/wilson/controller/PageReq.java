package com.wilson.controller;

import lombok.Data;

/**
 * @author wilson
 */
@Data
public class PageReq {
    private Integer page;
    private Integer size = 10;
}
