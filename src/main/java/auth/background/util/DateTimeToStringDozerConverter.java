package auth.background.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.dozer.DozerConverter;

 ///日期格式转换类dozer
public class DateTimeToStringDozerConverter extends DozerConverter<Date, String> {

	  public DateTimeToStringDozerConverter() {
	    super(Date.class, String.class);
	  }

	@Override
	public String convertTo(Date source, String destination) {
		  DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
		  if(source==null)return"0001-01-01 00:00:00";
		  return sdf.format(source);//.replace(" ", "T");
	}

	@Override
	public Date convertFrom(String source, Date destination) {
		if(source==null) return new Date();
		  DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
		  Date dt=null;
		  try {
			 dt = sdf.parse(source);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dt;
	}

	}