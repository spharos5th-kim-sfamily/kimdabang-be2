package com.kimdabang.kdbserver.product.optionList.domain;

import com.kimdabang.kdbserver.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "option_list")
public class OptionList extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("상품 코드")
    @Column(nullable = false)
    private String productCode;

    @Comment("상품 상태/ 판매가능 유무")
    @Column(nullable = false)
    private boolean productStatus = true;

    @Comment("옵션별 상품 자체 할인율")
    @Column(nullable = true)
    private Long discountPercent;

    @Comment("상품 할인가")
    @Column(nullable = true)
    private Long discountPrice;

}
