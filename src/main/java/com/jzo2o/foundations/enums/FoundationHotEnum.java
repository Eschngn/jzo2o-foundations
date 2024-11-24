package com.jzo2o.foundations.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Wilson
 * @Description: TODO
 * @date 2024/11/23 14:25
 */
@AllArgsConstructor
@Getter
public enum FoundationHotEnum {
    notHot(0,"非热门"),
    HOT(1,"热门");
    private int status;
    private String description;

    public boolean equals(Integer status) {
        return this.status == status;
    }

    public boolean equals(FoundationHotEnum enableStatusEnum) {
        return enableStatusEnum != null && enableStatusEnum.status == this.getStatus();
    }

}
