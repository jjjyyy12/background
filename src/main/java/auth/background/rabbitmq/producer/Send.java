package auth.background.rabbitmq.producer;

import java.util.UUID;  

import org.springframework.amqp.rabbit.core.RabbitTemplate;  
import org.springframework.amqp.rabbit.support.CorrelationData;  
import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.stereotype.Component;  

import auth.background.config.BackMVCConfig;
  
/**  
 * 消息生产者  
 *   
 
 */  
//@Component  
//public class Send implements RabbitTemplate.ConfirmCallback {  
//    
//    private RabbitTemplate rabbitTemplate;  
//  
//    /**  
//     * 构造方法注入  
//     */  
//    @Autowired  
//    public Send(RabbitTemplate rabbitTemplate) {  
//        this.rabbitTemplate = rabbitTemplate;  
//        rabbitTemplate.setConfirmCallback(this); //rabbitTemplate如果为单例的话，那回调就是最后设置的内容  
//    }  
//  
//    public void sendMsg(String exchange,String routingkey,String content) {  
//        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());  
//        rabbitTemplate.convertAndSend(exchange, routingkey, content, correlationId);  
//    }  
//  
//    /**  
//     * 回调  
//     */  
//	@Override
//	public void confirm(CorrelationData correlationData, boolean ack, String arg2) {
//		 System.out.println(" 回调id:" + correlationData);  
//	        if (ack) {  
//	            System.out.println("消息成功消费");  
//	        } else {  
//	            System.out.println("消息消费失败:"+arg2);  
//	        }  
//	}
//  
//}  
