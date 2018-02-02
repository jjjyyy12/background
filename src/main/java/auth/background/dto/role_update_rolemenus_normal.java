package auth.background.dto;

import com.alibaba.fastjson.JSON;

public class role_update_rolemenus_normal  extends MessageBase {
	 public role_update_rolemenus_normal(String exchangeName, RoleMenuMsg msg)
     {
		 this.setFailedTimes(0);
		 this.setMessageBodyReturnByte(null);
		 this.setexchangeName(exchangeName);
		 this.setMessageRouter(this.getClass().getName());//类名是消息队列名
		 this.setMessageBodyByte(JSON.toJSONBytes(msg));
     }
}
