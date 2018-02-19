
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final String[] market = {"btc", "eth", "doge", "waves", "usd", "rur"};

    public static void main(String[] args) throws IOException, SQLException {
        Parser parser = new Parser();
        List<MonitorThread> date = new ArrayList<>();
        for (int num=0; num<market.length;num++) {
            ExecutorUrl executorUrl = new ExecutorUrl(market[num]);
            MonitorThread sources = new MonitorThread(market[num],
                    parser.parsing(market[num], executorUrl.sendPost()));
            sources.start();
            date.add(sources);
            System.out.println(market[num] + " загрузил все значения. Поток вошел в сбор информации");
        }
    }


}


