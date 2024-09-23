package com.kimdabang.kdbserver.coupon.userEnrollCoupon.vo;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class UserEnrollCouponUsingRequestVo {
    private Long id;
    private Boolean isUsed;
    private LocalDateTime usedAt;
}
