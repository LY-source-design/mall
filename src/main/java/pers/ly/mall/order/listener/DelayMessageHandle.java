package pers.ly.mall.order.listener;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import pers.ly.mall.common.entity.DelayMessage;

@Data
@AllArgsConstructor
public class DelayMessageHandle implements MessagePostProcessor {
    private Long delay;

    @Override
    public Message postProcessMessage(Message message) throws AmqpException {
        message.getMessageProperties().setExpiration(String.valueOf(delay));
        return message;
    }
}
