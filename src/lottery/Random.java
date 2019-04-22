package lottery;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
public class Random {
	//LCG算法的实现
	public final AtomicLong seed=new AtomicLong();
	public final static long C = 1;
	public final static long A = 48271;
	public final static long M = (1L << 31) - 1;
	public Random(int seed){
		this.seed.set(seed);
	}
	public Random(){
		this.seed.set(System.nanoTime());
	}
	public long nextLong(){
		seed.set(System.nanoTime());
		return (A *seed.longValue() + C) % M;
	}
	public int nextInt(int number){
		return new Long( (A * System.nanoTime()/100+ C) % number).intValue();
	}
	public int[] getLucky(int num){
		Map<Integer,Integer> map=new HashMap<Integer,Integer>();
		for(int i=0;i<100000;i++){
			int ran=new Random().nextInt(num);
			if(map.containsKey(ran)){
				map.put(ran, map.get(ran)+1);
			}else{
				map.put(ran, 1);
			}
		}
		int []luck = new int[num];
		Iterator iter = map.entrySet().iterator();
		while (iter.hasNext()) 
		{
			Map.Entry entry = (Map.Entry) iter.next();
			int key = (Integer)entry.getKey();
			int value = map.get(key);
			luck[key]=value;
		}
		return luck;
	}
	public static void main(String[] args) {
		Map<Integer,Integer> map=new HashMap<Integer,Integer>();
		for(int i=0;i<100000;i++){
			int ran=new Random().nextInt(10);
			if(map.containsKey(ran)){
				map.put(ran, map.get(ran)+1);
			}else{
				map.put(ran, 1);
			}
		}
		System.out.println(map);
	}
}
