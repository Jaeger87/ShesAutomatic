package bot.organizerbox;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

public class Agenda {

	private HashMap<LocalDate, HashMap<Integer,DailyTask>> agendaDict;
	private int lastId;
	
	public Agenda() 
	{
		agendaDict = new HashMap<>();
		lastId = 0;
	}
	
	protected boolean addTask(String task, DateTime schedule, boolean notice)
	{
		if(!schedule.isAfterNow())
			return false;
		LocalDate lc = schedule.toLocalDate();
		if(!agendaDict.containsKey(lc))
			agendaDict.put(lc, new HashMap<>());
		agendaDict.get(lc).put(lastId, new DailyTask(task, schedule,lastId, notice));
		lastId++;
		return true;
	}
	
	protected List<DailyTask> checkAgendaToday()
	{
		return checkAgenda(new LocalDateTime());
	}
	
	
	private List<DailyTask> checkAgenda(LocalDateTime day)
	{
		if(!agendaDict.containsKey(day.toLocalDate()))
		    return null;
		List<DailyTask> nowTasks = agendaDict.get(day.toLocalDate())
				.values()
				.stream()
				.filter(dt -> dt.isEnable())
				.filter(dt -> dt.check(day.toDateTime()))
				.sorted()
				.collect(Collectors.toList());
		if(nowTasks.isEmpty())
			return null;
		nowTasks.forEach(dt -> dt.disable());
		return nowTasks;
	}
	
	
	protected void disableTask(LocalDate ld, int id)
	{
		DailyTask dt = findTask(ld, id);
		if(dt == null)
			return;
		dt.disable();
	}
	
	protected void enableTask(LocalDate ld, int id)
	{
		DailyTask dt = findTask(ld, id);
		if(dt == null)
			return;
		dt.enable();
	}
	
	protected DailyTask findTask(LocalDate ld, int id)
	{
		if(!agendaDict.containsKey(ld))
			return null;
		if(!agendaDict.get(ld).containsKey(id))
			return null;
		return agendaDict.get(ld).get(id);
	}
	
	
	protected List<DailyTask> getTodayDailyAgenda()
	{
		return getDailyAgenda(new LocalDate()); 
	}
	
	
	protected List<DailyTask> getDailyAgenda(LocalDate ld)
	{
		if(!agendaDict.containsKey(ld))
			return null;
		return agendaDict.get(ld)
				.values()
				.stream()
				.sorted()
				.collect(Collectors.toList());
	}
	
	protected boolean removeTask(LocalDate ld, int id)
	{
		if(!agendaDict.containsKey(ld))
			return false;
		if(!agendaDict.get(ld).containsKey(id))
			return false;
		agendaDict.get(ld).remove(id);
		return true;
	}
}
