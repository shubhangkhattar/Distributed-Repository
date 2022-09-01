package server;

import java.util.ArrayList;
import java.util.HashMap;

public class Repository {

    HashMap<String, ArrayList<Integer>> hashMap = new HashMap<>();

    public Repository() {
        hashMap = new HashMap<>();
    }

    public boolean set(String key, int value){
        //key must not exist or not?
        if(!hashMap.containsKey(key)){
            ArrayList<Integer> listValue = new ArrayList<>();
            listValue.add(value);
            hashMap.put(key, listValue);
            return true;
        }
        else{
            update(key, value);
            return true;
        }
//
//        return false;
    }

    public boolean add(String key, int value){
        //key must not exist or not?
        if(hashMap.containsKey(key)){
            ArrayList<Integer> listValue = hashMap.get(key);
            listValue.add(value);
            hashMap.put(key, listValue);
            return true;
        }


        return false;
    }

    public boolean update(String key, int value){
        //key must exist!!!
        if(hashMap.containsKey(key)){
            ArrayList<Integer> listValue = new ArrayList<>();
            listValue.add(value);
            hashMap.put(key, listValue);
            return true;
        }

        return false;
    }

    public boolean delete(String key){
        if(hashMap.containsKey(key)){
            hashMap.remove(key);
            return true;
        }
        return false;
    }

    public ArrayList<Integer> get(String key){
        return hashMap.get(key);
    }

    /**
     * Aggregation part
     */
    public int sum(String key){
        int sum =  hashMap.get(key).stream().mapToInt(Integer::intValue).sum();
        System.out.println("sum is "+sum);
        return sum;
    }

    public int min(String key){
        return hashMap.get(key).stream().mapToInt(Integer::intValue).min().orElse(-9999);
    }

    public int max(String key){
        return hashMap.get(key).stream().mapToInt(Integer::intValue).max().orElse(9999);
    }



}
