package auth.background.rabbitmq.consumer;

//
//import java.nio.charset.Charset;
//import java.util.ArrayList;
//import java.util.List;
//import javax.annotation.Resource;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Component;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.TypeReference;
//import auth.background.dto.MessageBase;
//import auth.background.dto.MenuDto;
//import auth.background.service.MenuAppService;
//
//@Component
//public class MenuAsyncQueueListener {
//	@Resource
//	private MenuAppService _menuAppService;
//
//    /** listener
//     */
//	@RabbitListener(queues = "menu_delete_deletemenu_normal_fonour_consumerauth")
//    public void ProcessMsg_Role(MessageBase msg) {
//			byte[] msgbyte = msg.getMessageBodyByte();
//	    	TypeReference<ArrayList<String>> typeReference = new TypeReference<ArrayList<String>>() {};
//	    	List<String> Ids = JSON.parseObject(msgbyte, 0, msgbyte.length,Charset.forName("Utf-8"), typeReference.getType());
//	    	_menuAppService.DeleteBatchImpl(Ids);
//    }
//
//	@RabbitListener(queues = "menu_update_insertupdate_rpc")
//    public MessageBase ProcessRequestMsg_User(MessageBase msg) {
//		String Id = "";
//		byte[] msgbyte = msg.getMessageBodyByte();
//		MenuDto dto = JSON.parseObject(msgbyte, MenuDto.class);
//	    if(dto!=null)
//	    	Id = _menuAppService.InsertUpdateImpl(dto);
//	    msg.setMessageBodyReturnByte(JSON.toJSONBytes(Id));
//	    return msg;
//    }
//
//}
