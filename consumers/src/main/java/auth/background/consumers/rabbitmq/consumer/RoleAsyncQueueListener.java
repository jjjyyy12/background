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
import auth.background.domain.dto.RoleDto;
import auth.background.domain.dto.RoleMenuMsg;
import auth.background.domain.dto.UserDto;
import auth.background.domain.dto.UserRoleMsg;
import auth.background.application.RoleAppService;
import auth.background.application.UserAppService;

@Component
public class RoleAsyncQueueListener {
	@Resource
	private RoleAppService _roleAppService;
	
    /**user listener
     */
//	@RabbitHandler
	@RabbitListener(queues = "role_delete_deleterole_normal_fonour_consumerauth")
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
	@RabbitListener(queues = "role_update_rolemenus_normal_fonour_consumerauth")
    public void ProcessMsg_RoleMenu(MessageBase msg) {
			byte[] msgbyte = msg.getMessageBodyByte();
	    	TypeReference<RoleMenuMsg> typeReference = new TypeReference<RoleMenuMsg>() {};
	    	RoleMenuMsg realmsg = JSON.parseObject(msgbyte, 0, msgbyte.length,Charset.forName("Utf-8"), typeReference.getType());
	    	_roleAppService.UpdateRowMenusImpl(realmsg.Id,realmsg.menuIds);
    }
 
}
