package lottery;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

public class ProcessQQLog
{
	private String url;
	private File file;
	private BufferedReader br;
	private String regex = "[0-9]{4}-[0-9]{2}-[0-9]{2}$";
	private HashMap<String, List<Record>> idRecordMap = new HashMap<String, List<Record>>();//��Ϊqq�ţ�ֵΪ��¼�б�
	private HashMap<String, String> idNameMap = new HashMap<>(); //��Ϊqq�ţ�ֵΪqq����
	private HashMap<String, List<Record>> idDrawMap = new HashMap<String, List<Record>>();//qq��-���齱ʱ���¼
	private HashMap<String, List<Record>> tempMap = new HashMap<String, List<Record>>();
	private List<String> userList = new ArrayList<String>();
	public ProcessQQLog(String url)
	{
		this.url = url;
		this.work();
	}
	//����qq�Ż�ȡqq��
	public String getQQName(String qqNumber)
	{
		return idNameMap.get(qqNumber);
	}
	//��ȡ�����û�
	public HashMap<String, String> getAllUser()
	{
		return idNameMap;
	}
	//��ȡ�����û�id��QQ�ţ�
	public List<String> getAllUserId()
	{
		Iterator iter = idDrawMap.entrySet().iterator();
		while (iter.hasNext()) 
		{
			Map.Entry entry = (Map.Entry) iter.next();
			userList.add((String)entry.getKey());
		}
		return userList;
	}
	//����qq�Ż�ȡ���������¼
 	public HashMap<String, List<Record>>getRecordList()
	{
		return idRecordMap;
	}
	//���ݸ����������ز���齱�б�
	public HashMap<String, List<Record>> getDrawList(Date beg, Date end, String tag)
	{
		this.filter(tag);
		Iterator iter = idDrawMap.entrySet().iterator();
		while (iter.hasNext()) 
		{
			Map.Entry entry = (Map.Entry) iter.next();
			String qqNumber = (String)entry.getKey();
			List<Record> list = (List<Record>)entry.getValue();
			for(int i = 0; i < list.size(); i++)
			{
				if(Date.biggerOrEqual(list.get(i).getDate(), beg) && Date.biggerOrEqual(end, list.get(i).getDate()))
				{
					if(!tempMap.containsKey(qqNumber))
					{
						tempMap.put(qqNumber, new ArrayList<Record>());
					}
					tempMap.get(qqNumber).add(list.get(i));
				}
			}
		}
		return tempMap;
	}
	
	
	private void work()
	{
		String qqNumber = "";
		String qqName = "";
		try
		{
			file = new File(url);
			FileInputStream in2 = new FileInputStream(file);
			br = new BufferedReader(new UnicodeReader(in2, "UTF-8"));
			String temp = "";
			while((temp = br.readLine()) != null)
			{
				
				if(temp.length() > 20 && temp.substring(0, 10).matches(regex) && (temp.charAt(temp.length()-1) == ')' || temp.charAt(temp.length()-1) == '>' ))//������
				{
					
					int index1 = temp.length() - 1;
					while(temp.charAt(index1) != '(' && temp.charAt(index1) != '<')
					{
						index1--;
					}
					qqNumber = temp.substring(index1 + 1, temp.length() - 1);
					int index2 = 15;
					while(temp.charAt(index2) != ':')
					{
						index2++;
					}
					qqName = temp.substring(index2 + 4, index1);
					if(!idNameMap.containsKey(qqNumber))
					{
						
						idRecordMap.put(qqNumber, new ArrayList<Record>());
						idDrawMap.put(qqNumber, new ArrayList<Record>());
					}
					idNameMap.put(qqNumber, qqName);
					int y = Integer.parseInt(temp.substring(0, 4));
					int m = Integer.parseInt(temp.substring(5, 7));
					int d = Integer.parseInt(temp.substring(8, 10));
					int h = Integer.parseInt(temp.substring(11, index2 - 3));
					int min = Integer.parseInt(temp.substring(index2-2, index2));
					int s = Integer.parseInt(temp.substring(index2 + 1, index2 + 3));
					Record r = new Record();
					r.setDate(new Date(y, m, d, h, min, s));
					r.setContent("");
					idRecordMap.get(qqNumber).add(r);
				}
				else
				{
					idRecordMap.get(qqNumber).get(idRecordMap.get(qqNumber).size()-1).appendContent(temp);
				}
			}
		} 
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void filter(String tag)
	{
		String t = "";
		Iterator iter = idRecordMap.entrySet().iterator();
		while (iter.hasNext()) 
		{
			Map.Entry entry = (Map.Entry) iter.next();
			String qqNumber = (String)entry.getKey();
			List<Record> list = (List<Record>)entry.getValue();
			for(int i = 0; i < list.size(); i++)
			{
				String s = list.get(i).getContent();
				for(int j =0; j < s.length(); j++)
				{
					if(s.charAt(j) == '#')
					{
						if(t.equals(tag.substring(1, tag.length()-1)))
						{
							idDrawMap.get(qqNumber).add(list.get(i));
						}
						t = "";
					}
					else
					{
						t += s.charAt(j);
					}
				}
				
				
			}
		}
	}
	
	
	public void visitIdRecord(HashMap map)
	{
		Iterator iter = map.entrySet().iterator();
		while (iter.hasNext()) 
		{
			Map.Entry entry = (Map.Entry) iter.next();
			String qqNumber = (String)entry.getKey();
			List<Record> list = (List<Record>)entry.getValue();
			for(int i = 0; i < list.size(); i++)
			{
				System.out.println(qqNumber + "  " + list.get(i).getContent() + " ʱ�䣺" + list.get(i).getDate().toString());
			}
		}
	}
}




class Record
{
	private Date date;
	private String content = "";
	public Date getDate()
	{
		return date;
	}
	public void setDate(Date date)
	{
		this.date = date;
	}
	public String getContent()
	{
		return content;
	}
	public void appendContent(String ap)
	{
		this.content += ap;
	}
	public void setContent(String content)
	{
		this.content = content;
	}
	
}
class Date
{
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;
	private int second;
	
	public Date(int y, int m, int d, int h, int min, int s)
	{
		setDate(y, m, d, h, min, s);
	}
	public Date()
	{
		setDate(0,0,0,0,0,0);
	}
	
	public String toString()
	{
		String t = Integer.toString(year)+"-"+Integer.toString(month) + "-" + Integer.toString(day);
		String tt = Integer.toString(hour)+":"+Integer.toString(minute) + ":" + Integer.toString(second);
		return t + " " + tt;
	}
	
	public static Boolean biggerOrEqual(Date a, Date b)
	{
		Boolean flag = false;
		if(a.year > b.year)
			flag = true;
		else if(a.year == b.year)
		{
			if(a.month > b.month)
				flag = true;
			else if(a.month == b.month)
			{
				if(a.day > b.day)
					flag = true;
				else if(a.day == b.day)
				{
					if(a.hour > b.hour)
						flag = true;
					else if(a.hour == b.hour)
					{
						if(a.minute > b.minute)
							flag = true;
						else if(a.minute == b.minute)
						{
							if(a.second >= b.second)
								flag = true;
						}
							
					}
				}
			}
		}
		return flag;
	}
	
	private void setDate(int y, int m, int d, int h, int min, int s)
	{
		this.year = y;
		this.month = m;
		this.day = d;
		this.hour = h;
		this.minute = min;
		this.second = s;
	}
	public int getYear()
	{
		return year;
	}
	public void setYear(int year)
	{
		this.year = year;
	}
	public int getMonth()
	{
		return month;
	}
	public void setMonth(int month)
	{
		this.month = month;
	}
	public int getDay()
	{
		return day;
	}
	public void setDay(int day)
	{
		this.day = day;
	}
	public int getHour()
	{
		return hour;
	}
	public void setHour(int hour)
	{
		this.hour = hour;
	}
	public int getMinute()
	{
		return minute;
	}
	public void setMinute(int minute)
	{
		this.minute = minute;
	}
	public int getSecond()
	{
		return second;
	}
	public void setSecond(int second)

	
	{
		this.second = second;
	}
}
