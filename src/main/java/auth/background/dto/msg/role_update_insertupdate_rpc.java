package auth.background.dto.msg;

import com.alibaba.fastjson.JSON;

import auth.background.dto.MessageBase;
import auth.background.dto.RoleDto;

public class role_update_insertupdate_rpc extends MessageBase {
	 public role_update_insertupdate_rpc(String exchangeName, RoleDto dto)
     {
		 this.setFailedTimes(0);
		 this.setMessageBodyReturnByte(null);
		 this.setexchangeName(exchangeName);
		 this.setMessageRouter(this.getClass().getName());//类名是消息队列名
		 this.setMessageBodyByte(JSON.toJSONBytes(dto));
     }
}
