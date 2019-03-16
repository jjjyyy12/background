package auth.background.consumers.rabbitmq.consumer;


import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import auth.background.domain.dto.MessageBase;
import auth.background.domain.dto.UserDto;
import auth.background.domain.dto.UserRoleMsg;
import auth.background.application.UserAppService;

@Component
public class UserAsyncQueueListener {
	@Resource
	private UserAppService _userAppService;
	
    /**user listener
     */
//	@RabbitHandler
	@RabbitListener(queues = "user_delete_deleteuser_normal_fonour_consumerauth")
    public void ProcessMsg_User(MessageBase msg) {
			byte[] msgbyte = msg.getMessageBodyByte();
	    	TypeReference<ArrayList<String>> typeReference = new TypeReference<ArrayList<String>>() {};
	    	List<String> userIds = JSON.parseObject(msgbyte, 0, msgbyte.length,Charset.forName("Utf-8"), typeReference.getType());
	    	_userAppService.DeleteBatchImpl(userIds);
    }

	@RabbitListener(queues = "user_update_insertupdate_rpc")
//	@SendTo("user_update_insertupdate_rpc_reply")
    public MessageBase ProcessRequestMsg_User(MessageBase msg) {
		String userId = "";
		byte[] msgbyte = msg.getMessageBodyByte();
	    UserDto dto = JSON.parseObject(msgbyte, UserDto.class); 
	    if(dto!=null)
	    	userId = _userAppService.InsertUpdateImpl(dto);
	    msg.setMessageBodyReturnByte(JSON.toJSONBytes(userId));
	    return msg;
    }
 
	@RabbitListener(queues = "user_update_userroles_normal_fonour_consumerauth")
    public void ProcessMsg_UserRole(MessageBase msg) {
			byte[] msgbyte = msg.getMessageBodyByte();
	    	TypeReference<UserRoleMsg> typeReference = new TypeReference<UserRoleMsg>() {};
	    	UserRoleMsg realmsg = JSON.parseObject(msgbyte, 0, msgbyte.length,Charset.forName("Utf-8"), typeReference.getType());
	    	_userAppService.BatchUpdateUserRolesImpl(realmsg.userIds,realmsg.roleIds);
    }
	
//	 //参数中使用@Header获取mesage
//    @RabbitListener(queues = { "user_update_insertupdate_rpc_reply" })
//    public void asyncReply(MessageBase msg) {
//         
//        msg.getMessageBodyByte().toString();
//    }
    /**
     * 异步队列，SendTo为回复的队列名称
     * @param id 任务ID
     * @param type 任务名称
     * @return
     */
//    @RabbitListener(queues = "ccs.queue.async")
//    @SendTo("ccs.queue.async.reply")
//    public Object ProcessMsgAsync(String id, @Header("jobName") String jobName) {
//        System.out.println("Received request for id " + id);
//        System.out.println("Received request for job name " + jobName);
//        //返回执行结果（成功，失败）和ID
//        BaseResponse response = new BaseResponse(true, id);
//        return response;
//    }
//    @RabbitListener(queues = "user.update.rpc")  
//    //参数中使用@Header获取mesage  
//    @SendTo("auth.exchange/user.update.reply")  
//    public org.springframework.messaging.Message<String> ProcessMsgAsync(Message message)  
//    {  
//        System.out.println("headers:" + message.getMessageProperties().toString());  
//        String data = new String(message.getBody());  
//        System.out.println("queue_one data:" + data);  
//          
//        return org.springframework.messaging.support.MessageBuilder.withPayload(data).build();  
//    }  
}
