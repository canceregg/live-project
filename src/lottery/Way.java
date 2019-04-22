package lottery;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class Way {
	public static Way create(){
		Way way=new Way();
		return way;
	}
	
	//不过滤抽奖名单
	public List<String> none(List<String> qqs,int num){
		HashMap<String,Integer> map=new HashMap<String,Integer>();
		int luck[]=new Random().getLucky(qqs.size());
		for(int i=0;i<qqs.size();i++){
			map.put(qqs.get(i), luck[i]);
		}
		List<Map.Entry<String,Integer>> list = new ArrayList<Map.Entry<String,Integer>>(map.entrySet());
		
		Collections.sort(list,new Comparator<Map.Entry<String,Integer>>() {
	           
	            public int compare(Entry<String, Integer> o1,
	                    Entry<String, Integer> o2) {
	                return -o1.getValue().compareTo(o2.getValue());
	            }
	     });
		list=list.subList(0, num);
		List<String> thenone=new ArrayList<String>();
		for(Map.Entry<String,Integer> m:list){ 
			thenone.add(m.getKey());
		} 
		
		return thenone;
	}
	
	//普通过滤抽奖名单
	public List<String> common(HashMap<String, List<Record>> msgs,ProcessQQLog user,int num){
		msgs=clearAssit(msgs,user);
		List<String> qqs=new ArrayList<>(msgs.keySet());
		qqs=none(qqs,num);
		return qqs;
	}
	
	//深度过滤抽奖名单
	public List<String> deep(HashMap<String, List<Record>> msgs,ProcessQQLog user,int num){
		msgs=clearAssit(msgs,user);
		
		return none(msgRate(msgs,num),num);
	}
	
	//将助教及老师从开奖名单中删除
	public HashMap<String, List<Record>>  clearAssit(HashMap<String, List<Record>> msgs,ProcessQQLog user){
		
	
		Iterator iter = msgs.entrySet().iterator();
		while (iter.hasNext()) 
		{
			Map.Entry entry = (Map.Entry) iter.next();
			String qq = (String)entry.getKey();
			String name=user.getQQName(qq);
			if(name.contains("助教")||name.contains("老师")||qq.equals("10000")||qq.equals("1000008")){
				iter.remove();
				msgs.remove(qq);
			}
		}
		return msgs;
	}
	
	
	//对有效发言次数进行排序 有效发言次数越多中奖几率越大
	public List<String> msgRate(HashMap<String, List<Record>> msgs,int num){
		if(num<20) num*=2;
		HashMap<String,Integer> qqN=new HashMap<String,Integer>();
		
		Iterator iter = msgs.entrySet().iterator();
		while (iter.hasNext()) 
		{
			Map.Entry entry = (Map.Entry) iter.next();
			String qq = (String)entry.getKey();
			List<Record> list = (List<Record>)entry.getValue();
			qqN.put(qq, list.size());
		
		}	
		
		List<Map.Entry<String,Integer>> list = new ArrayList<Map.Entry<String,Integer>>(qqN.entrySet());
		
		Collections.sort(list,new Comparator<Map.Entry<String,Integer>>() {
	            public int compare(Entry<String, Integer> o1,
	                    Entry<String, Integer> o2) {
	                return -o1.getValue().compareTo(o2.getValue());
	            }
	          
	      });
		list=list.subList(0, num);
		List<String> themsg=new ArrayList<String>();
		for(Map.Entry<String,Integer> m:list){ 
			themsg.add(m.getKey());
		} 
		
		
		return themsg;
		
	}

}
