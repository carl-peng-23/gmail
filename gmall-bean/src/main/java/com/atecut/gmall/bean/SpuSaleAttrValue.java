package com.atecut.gmall.bean;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;

@ToString
@Data
public class SpuSaleAttrValue implements Serializable {

    @Id
    @Column
    String id ;

    @Column
    String spuId;

    @Column
    String saleAttrId;

    @Column
    String saleAttrValueName;

    @Transient
    String isChecked;
}