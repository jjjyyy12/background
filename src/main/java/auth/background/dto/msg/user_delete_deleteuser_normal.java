package auth.background.dto.msg;

import java.util.List;
import com.alibaba.fastjson.JSON;

import auth.background.dto.MessageBase;

public class user_delete_deleteuser_normal extends MessageBase {
	 public user_delete_deleteuser_normal(String exchangeName, List<String> ids)
     {
		 this.setFailedTimes(0);
		 this.setMessageBodyReturnByte(null);
		 this.setexchangeName(exchangeName);
		 this.setMessageRouter(this.getClass().getName());//类名是消息队列名
		 this.setMessageBodyByte(JSON.toJSONBytes(ids));
     }
}
