package pers.ly.mall.order.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class EsOptimisticLockVO {
    //乐观锁关键
    private Long seqNo;
    private Long primaryTerm;
    //待更新字段
    private Map<String,Object> updateFields;

    public Object getField(String key) {
        return updateFields.get(key);
    }
}
