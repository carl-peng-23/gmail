package com.atecut.gmall.bean;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

@ToString
@Data
public class SpuSaleAttr  implements Serializable {

    @Id
    @Column
    String id ;

    @Column
    String spuId;

    @Column
    String saleAttrId;

    @Column
    String saleAttrName;


    @Transient
    List<SpuSaleAttrValue> spuSaleAttrValueList;

}