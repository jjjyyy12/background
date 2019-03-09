package auth.background.domain.dto.msg;

import com.alibaba.fastjson.JSON;

import auth.background.domain.dto.MessageBase;
import auth.background.domain.dto.UserDto;

public class user_update_insertupdate_rpc extends MessageBase {
	 public user_update_insertupdate_rpc(String exchangeName, UserDto dto)
     {
		 this.setFailedTimes(0);
		 this.setMessageBodyReturnByte(null);
		 this.setexchangeName(exchangeName);
		 this.setMessageRouter(this.getClass().getName());//类名是消息队列名
		 this.setMessageBodyByte(JSON.toJSONBytes(dto));
     }
}
