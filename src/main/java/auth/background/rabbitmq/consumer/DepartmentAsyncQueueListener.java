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
import auth.background.dto.DepartmentDto;
import auth.background.dto.MenuDto;
import auth.background.service.DepartmentAppService;
import auth.background.service.MenuAppService;

@Component
public class DepartmentAsyncQueueListener {
	@Resource
	private DepartmentAppService _departmentAppService;
	
    /** listener
     */
	@RabbitListener(queues = "department_delete_deletedepartment_normal_fonour_consumerauth")
    public void ProcessMsg_Role(MessageBase msg) {
			byte[] msgbyte = msg.getMessageBodyByte();
	    	TypeReference<ArrayList<String>> typeReference = new TypeReference<ArrayList<String>>() {};
	    	List<String> Ids = JSON.parseObject(msgbyte, 0, msgbyte.length,Charset.forName("Utf-8"), typeReference.getType());
	    	_departmentAppService.DeleteBatchImpl(Ids);
    }

	@RabbitListener(queues = "department_update_insertupdate_rpc")
    public MessageBase ProcessRequestMsg_User(MessageBase msg) {
		String Id = "";
		byte[] msgbyte = msg.getMessageBodyByte();
		DepartmentDto dto = JSON.parseObject(msgbyte, DepartmentDto.class); 
	    if(dto!=null)
	    	Id = _departmentAppService.InsertUpdateImpl(dto);
	    msg.setMessageBodyReturnByte(JSON.toJSONBytes(Id));
	    return msg;
    }
 
}
