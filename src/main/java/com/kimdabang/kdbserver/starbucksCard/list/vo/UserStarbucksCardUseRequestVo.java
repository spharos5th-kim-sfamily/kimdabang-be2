package com.kimdabang.kdbserver.starbucksCard.list.vo;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UserStarbucksCardUseRequestVo {
    private Long id;
    private Long starbucksCardId;
    private int charge;
}
