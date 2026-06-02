package com.bubble.service.impl;

import com.bubble.service.EmailService;
import com.bubble.service.VerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private EmailService emailService;

    @Value("${verification-code.length:6}")
    private int codeLength;

    @Value("${verification-code.expire-minutes:5}")
    private long expireMinutes;

    private static final String REDIS_KEY_PREFIX = "verify_code:";

    @Override
    public String generateAndSaveCode(String email) {
        // 生成随机6位验证码
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < codeLength; i++) {
            code.append(random.nextInt(10));
        }
        String codeStr = code.toString();

        // 存入Redis，设置过期时间
        String key = REDIS_KEY_PREFIX + email;
        redisTemplate.opsForValue().set(key, codeStr, expireMinutes, TimeUnit.MINUTES);

        // 发送邮件
        emailService.sendVerificationCode(email, codeStr);

        return codeStr;
    }

    @Override
    public boolean verifyCode(String email, String code) {
        String key = REDIS_KEY_PREFIX + email;
        String savedCode = redisTemplate.opsForValue().get(key);
        if (savedCode != null && savedCode.equals(code)) {
            // 验证成功后立即删除验证码（防止重复使用）
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }
}
