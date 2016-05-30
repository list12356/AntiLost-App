package lst.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemList {
    public static List<myItem> Data=new ArrayList<myItem>();
    public static List<String> Show=new ArrayList<String>();
    List<Map<String, Object>> ShowData = new ArrayList<Map<String, Object>>();

    public void add(String name,String address)
    {
        myItem tmp=new myItem();
        tmp.Address=address;
        tmp.Name=name;
        for(int i=0;i<Data.size();i++) {
            if (Data.get(i).Address.equals(address)) {
                return;
            }
        }
        Data.add(tmp);
        Show.add(name);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name",name);
        map.put("status",name);
        ShowData.add(map);
        //to do: write to the file
    }

    public void Delete(int pos)
    {
        Data.remove(pos);
        Show.remove(pos);
    }
}
