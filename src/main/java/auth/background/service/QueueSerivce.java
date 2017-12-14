package auth.background.service;

import javax.annotation.Resource;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import auth.background.dto.MessageBase;
//http://blog.csdn.net/oMaverick1/article/details/51331004
@Service
public class QueueSerivce<T> {
	//稳定消息队列接口rabbitmq
	@Resource
	private AmqpTemplate amqpTemplate;
	//rabbitmq exchangename
	 public static final String exchangeName = "auth.exchange";
     //记录到kafka的topicname
	 public static final String logTopicName = "auth.operate";
     
	public AmqpTemplate getAmqpTemplate() {
		return amqpTemplate;
	}

	public void setAmqpTemplate(AmqpTemplate amqpTemplate) {
		this.amqpTemplate = amqpTemplate;
	}
	
	@SuppressWarnings("unchecked")
	public void Request(T dto, MessageBase msg, MessageBase replyMsg, RunnableQueueSucc<T,MessageBase>  succHandle, int runcnt) throws InterruptedException{
		try
        {
			MessageBase reply = (MessageBase)amqpTemplate.convertSendAndReceive(QueueSerivce.exchangeName, msg.getMessageRouter(), msg);
			replyMsg=reply;
            if (replyMsg.getMessageBodyReturnByte() != null)
            {
            	succHandle.Handle(dto, replyMsg);
            }
           // _bigQueue.PublishTopic(msg, _logTopicName, null);
        }
        catch (Exception e)
        {
            if (runcnt != 4) //4次机会重发,每次等待2*失败次数的时间
            {
                runcnt++;
					Thread.sleep(2000 * runcnt);
                Request(dto,msg, replyMsg, succHandle,  runcnt);
            }
        }
	}

}
