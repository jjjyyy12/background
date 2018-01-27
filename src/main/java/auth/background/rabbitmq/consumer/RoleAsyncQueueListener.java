package auth.background.rabbitmq.consumer;


import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import auth.background.dto.MessageBase;
import auth.background.dto.RoleDto;
import auth.background.dto.UserDto;
import auth.background.dto.UserRoleMsg;
import auth.background.service.RoleAppService;
import auth.background.service.UserAppService;

@Component
public class RoleAsyncQueueListener {
	@Resource
	private RoleAppService _roleAppService;
	
    /**user listener
     */
//	@RabbitHandler
	@RabbitListener(queues = "role_delete_deleteuser_normal_fonour_consumerauth")
    public void ProcessMsg_Role(MessageBase msg) {
			byte[] msgbyte = msg.getMessageBodyByte();
	    	TypeReference<ArrayList<String>> typeReference = new TypeReference<ArrayList<String>>() {};
	    	List<String> Ids = JSON.parseObject(msgbyte, 0, msgbyte.length,Charset.forName("Utf-8"), typeReference.getType());
	    	_roleAppService.DeleteBatchImpl(Ids);
    }

	@RabbitListener(queues = "role_update_insertupdate_rpc")
//	@SendTo("role_update_insertupdate_rpc_reply")
    public MessageBase ProcessRequestMsg_User(MessageBase msg) {
		String Id = "";
		byte[] msgbyte = msg.getMessageBodyByte();
		RoleDto dto = JSON.parseObject(msgbyte, RoleDto.class); 
	    if(dto!=null)
	    	Id = _roleAppService.InsertUpdateImpl(dto);
	    msg.setMessageBodyReturnByte(JSON.toJSONBytes(Id));
	    return msg;
    }
 
 
}
