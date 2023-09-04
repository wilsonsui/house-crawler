package com.wilson.queue;

import com.wilson.entity.ProxyEntity;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author wilson
 * @description: 验证队列，用于存放待验证的代理IP，防止重复验证，提高效率
 */
@Component
public class VerifyQueue extends LinkedBlockingQueue<ProxyEntity> {
}
