package net.skyee;


import net.skyee.dao.StocksDAO;
import org.skife.jdbi.v2.DBI;

public class Context {
    private static Context context;
    private StocksDAO stocksDAO;
    private DBI dbInterface;
//    private Environment environment;

    protected Context() {
    }

    public static Context getInstance() {
        if (context == null) {
            context = new Context();
        }
        return context;
    }

    public Context updateDBInterface(DBI dbInterface) {
        this.dbInterface = dbInterface;
        return this;
    }

    public StocksDAO templateDAO() throws ClassNotFoundException {

        if (stocksDAO == null) {
            stocksDAO = dbInterface.onDemand(StocksDAO.class);
        }
        return stocksDAO;
    }
}
