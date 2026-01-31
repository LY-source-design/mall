package pers.ly.mall.common.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;

@Data
@AllArgsConstructor
public class DelayMessageHandler implements MessagePostProcessor {
    private Long delay;

    @Override
    public Message postProcessMessage(Message message) throws AmqpException {
        message.getMessageProperties().setDelay(delay.intValue());
        return message;
    }
}
