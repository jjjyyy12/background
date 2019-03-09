package auth.background.domain.dto.msg;

import com.alibaba.fastjson.JSON;

import auth.background.domain.dto.DepartmentDto;
import auth.background.domain.dto.MessageBase;

public class department_update_insertupdate_rpc extends MessageBase {
	 public department_update_insertupdate_rpc(String exchangeName, DepartmentDto dto)
     {
		 this.setFailedTimes(0);
		 this.setMessageBodyReturnByte(null);
		 this.setexchangeName(exchangeName);
		 this.setMessageRouter(this.getClass().getName());//类名是消息队列名
		 this.setMessageBodyByte(JSON.toJSONBytes(dto));
     }
}
