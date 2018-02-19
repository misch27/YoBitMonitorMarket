import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;



public class DAO {
    private int numOfSt=0;
    private String tabName;
    private Connection connection = getMysqlConnection();
    private Executor executor;


    public DAO(String name) throws SQLException{
        this.tabName = name;
        createTable(name);
    }

    private static Connection getMysqlConnection() {
        try {

            DriverManager.registerDriver((Driver) Class.forName("com.mysql.jdbc.Driver").newInstance());
            StringBuilder url = new StringBuilder();
            url.
                    append("jdbc:mysql://").
                    append("localhost:").
                    append("3306/").
                    append("YoBit?").
                    append("useSSL=false&").
                    append("user=root&").
                    append("password=root");

//            System.out.println("URL: " + url + "\n");

            return DriverManager.getConnection(url.toString());
        } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException  e) {
            e.printStackTrace();
        }
        return null;
    }
    private void createTable(String nameOfTable) throws SQLException {
        this.executor = new Executor(connection);
        StringBuilder request = new StringBuilder();
        request.append("CREATE TABLE if not exists ")
                .append(nameOfTable)
                .append("(`date` TIMESTAMP,")
                .append("`currency` VARCHAR(128) NOT NULL,")
                .append("`price` VARCHAR(45) NOT NULL,")
                .append("`changer` LONGTEXT NOT NULL,")
                .append("`amount` VARCHAR(10) NOT NULL,")
                .append("`explanation` VARCHAR(45) NOT NULL)");
//                .append("PRIMARY KEY (`date`));");
        executor.executeInsert(request.toString());
    }
    public void batchSQL(HashMap<String, ArrayList<String>> statementSql) throws SQLException {
        try {
            executor.setBatchProp(20, "INSERT INTO " +
                    tabName+
                    "(currency, price, changer, amount, explanation)" +
                    " VALUES (?,?,?,?,?)");
            statementSql.values()
                    .forEach(colArr->{
                        try {
                            executor.batchStrings(colArr);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });

        }finally {
            executor.close();
        }

    }
    public void insertOldValue(ArrayList<String> newVal) throws SQLException {
//        StringBuilder request = new StringBuilder();
//        request.append("INSERT INTO ")
//                .append(tabName)
//                .append("(currency, price, changer, amount)")
//                .append(" VALUES (")
//
//                .append(" )");
//        executor.executeInsert(request.toString());
        try {
            executor.setBatchProp(1,"INSERT INTO " +
                    tabName+
                    "(currency, price, changer, amount, explanation)" +
                    " VALUES (?,?,?,?,? )");
            executor.batchStrings(newVal);
        }finally {
            executor.close();
        }
    }

}
