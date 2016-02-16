package net.skyee.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Define;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

import java.math.BigDecimal;

@UseStringTemplate3StatementLocator
public interface StocksDAO {

    @SqlUpdate("insert into st values (:sno, :datetime, :close_price, :increase, :increase_percentage, :in_count, :out_count, :total_count)")
    void insert(@Bind("sno") String sno,
                @Bind("datetime") String datetime,
                @Bind("close_price") BigDecimal close_price,
                @Bind("increase") BigDecimal increase,
                @Bind("increase_percentage") BigDecimal increase_percentage,
                @Bind("in_count") BigDecimal in_count,
                @Bind("out_count") BigDecimal out_count,
                @Bind("total_count") BigDecimal total_count);

    @SqlUpdate("CREATE TABLE IF NOT EXISTS <new_table> LIKE template_table")
    int findTableExist(@Define("new_table") String new_table);

}
