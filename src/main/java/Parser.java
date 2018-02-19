import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class Parser {
    public HashMap<String,ArrayList<String>> parsing(String marketName, String html) throws SQLException {
        Document document = Jsoup.parse(html);
        Element table = document.select("table").get(0);
        Elements rows = table.select("tr");

        HashMap<String,ArrayList<String>> arrOfRow = new HashMap<>(rows.size());
        rows.forEach(row -> {
            ArrayList<String> arrOfCol = new ArrayList<>();
            Elements cols = row.select("td");
            cols.forEach(col -> {
                arrOfCol.add(col.text());
            });
            if(marketName.equals("btc")){
                //Правило - Все валюты, кроме btc, имеют перед ценой символ валюты.
                //Монеты могут называться одинаково, но быть в разных валютах
                arrOfRow.put(arrOfCol.get(0)+arrOfCol.get(4), arrOfCol);
            }else {
                arrOfRow.put(arrOfCol.get(0) + arrOfCol.get(1).charAt(0), arrOfCol);
            }
        });
        return arrOfRow;
    }
}
