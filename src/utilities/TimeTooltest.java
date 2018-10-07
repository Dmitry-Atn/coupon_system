
package utilities;
import java.util.Calendar;
import java.util.TimeZone;

public class TimeTooltest {


	public static void main(String[] args) {
		System.out.println(TimeTool.currentSQLTimestamp());
		System.out.println(TimeTool.timestampToCalendar(TimeTool.currentSQLTimestamp()));
		Calendar cal = TimeTool.timestampToCalendar(TimeTool.currentSQLTimestamp());
		cal.setTimeZone(TimeZone.getTimeZone("GMT"));
		System.out.println(cal);
		System.out.println(TimeTool.calendarToTimestamp(cal));
	}
}
