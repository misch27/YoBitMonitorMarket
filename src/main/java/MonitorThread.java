import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


public class MonitorThread extends Thread {
    private String name;
    private HashMap<String,ArrayList<String>> values;
    private final DAO dao;
    private final ExecutorUrl executorUrl;
    private final Parser parser = new Parser();

    MonitorThread(String name, HashMap<String, ArrayList<String>> values) throws SQLException {
        this.name = name;
        this.values = values;

        executorUrl = new ExecutorUrl(this.name);
        dao = new DAO(this.name);
        dao.batchSQL(values);
    }




    @Override
    public void run() {
        Set<String> keySet = values.keySet();
        while (!isInterrupted()){
            try {
                String htmlCode =  executorUrl.sendPost();
                HashMap<String, ArrayList<String>> newHashMap = parser.parsing(name, htmlCode);
               keySet.forEach(key->{
                   try {
                       if (!values.get(key)
                               .equals(newHashMap.get(key))) {
                           dao.insertOldValue(values.get(key));
                           values.put(key, newHashMap.get(key));
                           System.out.println(name + " and " +newHashMap.get(key));
                       }
                       Thread.sleep(50);
                   }catch (NullPointerException| SQLException | InterruptedException e){e.printStackTrace();
                       System.out.println("!!!!!"+key+"!!!!!");}
               });

            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
