package auth.background.domain.dto.msg;

import java.util.List;
import com.alibaba.fastjson.JSON;

import auth.background.domain.dto.MessageBase;

public class menu_delete_deletemenu_normal extends MessageBase {
	 public menu_delete_deletemenu_normal(String exchangeName, List<String> ids)
     {
		 this.setFailedTimes(0);
		 this.setMessageBodyReturnByte(null);
		 this.setexchangeName(exchangeName);
		 this.setMessageRouter(this.getClass().getName());//类名是消息队列名
		 this.setMessageBodyByte(JSON.toJSONBytes(ids));
     }
}
