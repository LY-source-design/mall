package pers.ly.mall.common.entity;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class DelayMessage<T> {
    private T message;
    private List<Long> delayList;

    public DelayMessage() {}
    public DelayMessage(T message, List<Long> delayList) {
        this.message = message;
        this.delayList = delayList;
    }

    public boolean hasNextDelay() {
        return delayList != null && !delayList.isEmpty();
    }

    public Long removeNextDelay(){
        return delayList.remove(0);
    }
}
