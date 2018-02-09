package auth.background.dto;

import com.alibaba.fastjson.JSON;

public class menu_update_insertupdate_rpc extends MessageBase {
	 public menu_update_insertupdate_rpc(String exchangeName, MenuDto dto)
     {
		 this.setFailedTimes(0);
		 this.setMessageBodyReturnByte(null);
		 this.setexchangeName(exchangeName);
		 this.setMessageRouter(this.getClass().getName());//类名是消息队列名
		 this.setMessageBodyByte(JSON.toJSONBytes(dto));
     }
}
