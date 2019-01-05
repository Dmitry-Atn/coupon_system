package utilities;
import java.util.Calendar;
import java.sql.Timestamp;

public class TimeTool {
	
	public static Timestamp currentSQLTimestamp() {
		return calendarToTimestamp(Calendar.getInstance());
	}
	
	public static Timestamp calendarToTimestamp(Calendar cal) {
		return new java.sql.Timestamp(cal.getTimeInMillis());
	}
	
	public static Calendar timestampToCalendar(Timestamp timestamp) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp.getTime());
		return cal;
	}
	

}
