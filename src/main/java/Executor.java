import java.sql.*;
import java.util.List;

public class Executor {
    private final Connection connection;
    private PreparedStatement prstmt;
    private int batchCapacity = 1;
    private int numOfSt = 0;

    Executor(Connection connection) {
        this.connection = connection;
    }

    public void setBatchProp(int batchCapacity, String prepareVal) throws SQLException {
        prstmt = connection.prepareStatement(prepareVal);
        this.batchCapacity = batchCapacity;
    }

    public void executeInsert(String update) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute(update);
        stmt.close();
    }

    public <T> T executeQuery(String query,
                           ResultHandler<T> handler)
            throws SQLException {

        T value;
        Statement stmt = connection.createStatement();
        stmt.execute(query);
        ResultSet result = stmt.getResultSet();
        try {
            value = handler.handle(result);
            return value;
        }catch (SQLException sqlE){
            return null;
            //если значение не найдено - вывести null
        }finally {
            System.out.println();
            result.close();
            stmt.close();
        }


    }

    public void batchStrings(List<String> list) throws SQLException{

        for (int elem=0; elem<list.size(); elem++) {
            switch (elem) {
                default: {
                    prstmt.setString(elem + 1, list.get(elem));
                    break;
                }
//ДОРАБОТАТЬ ДО INT ИНАЧЕ НЕ ПРОИЗВОДИТЕЛЬНО
//                    case 2: {
//                        prstmt.setFloat(elem + 1, Float.valueOf(list.get(elem)
//                                .replaceAll("%","")
//                                .replaceAll(" ","")
//                                .replaceAll("\\+","")));
//                        break;
//                    }
//                    case 3: {
//                        prstmt.setFloat(elem + 1, Float
//                                .valueOf(list.get(elem)));
//                        break;
//                    }

            }
        }
        prstmt.addBatch();
        numOfSt++;
            //отправка если >20 записей
        if (numOfSt > batchCapacity) {
            prstmt.executeBatch();
            numOfSt = 0;
        }

    }

    public void close() throws SQLException {
        prstmt.executeBatch();
        prstmt.close();
    }
}
