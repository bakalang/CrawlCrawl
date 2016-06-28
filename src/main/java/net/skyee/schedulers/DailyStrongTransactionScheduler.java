package net.skyee.schedulers;

import de.spinscale.dropwizard.jobs.Job;

import de.spinscale.dropwizard.jobs.annotations.On;
import net.skyee.Context;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

@On("0 0 8 * * ?")
public class DailyStrongTransactionScheduler extends Job {
    Logger log = LoggerFactory.getLogger(DailyStrongTransactionScheduler.class);

    public static final String URL="http://jsjustweb.jihsun.com.tw/z/zg/zg_F_0_1.djhtm";

    private Context context;

    public DailyStrongTransactionScheduler() {
        context= Context.getInstance();
    }

    @Override
    public void doJob() {
        log.info("DailyStrongTransactionScheduler start");

        Pattern p = Pattern.compile("\\d{5}");
        DateTime now = new org.joda.time.DateTime();
        DateTimeFormatter yyyyMMdd_fm = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTimeFormatter MMdd_fm = DateTimeFormat.forPattern("MM/dd");
        String yyyyMMdd = yyyyMMdd_fm.print(now);

        Document doc = null;
        try {
            doc = Jsoup.connect(URL).get();

            // get StrongList
            Elements newsHeadlines = doc.select("#oMainTable");
            if(!MMdd_fm.print(now).equals(newsHeadlines.select(".t11").text().split("：")[1])) {
                log.info("data from "+newsHeadlines.select(".t11").text().split("：")[1]+" not update yet ");
                return;
            }
            Iterator<Element> rr = newsHeadlines.select("tr").iterator();
            while(rr.hasNext()){
                Iterator<Element> dd =rr.next().select("td").iterator();

                List<String> tmpList = new ArrayList<String>();
                while(dd.hasNext()){
                    Element dd_e = dd.next();
                    if(dd_e.className().startsWith("t3")) {
                        tmpList.add(Jsoup.parse(dd_e.html()).text());
                    }
                }

                if(tmpList.size() == 8){
                    if(!p.matcher(tmpList.get(1)).find()) {
                        context.templateDAO().insert(tmpList.get(1).split(" ")[0].trim(),
                                yyyyMMdd,
                                getBigDecimal(tmpList.get(2), 2),
                                getBigDecimal(tmpList.get(3), 2),
                                getBigDecimal(tmpList.get(4), 2),
                                getBigDecimal(tmpList.get(5), 2),
                                getBigDecimal(tmpList.get(6), 2),
                                getBigDecimal(tmpList.get(7), 2)
                              );
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        log.info("DailyStrongTransactionScheduler start");
    }

    private BigDecimal getBigDecimal(String tmp, int scale){
        tmp = tmp.replace(String.valueOf((char) 160), " ");
        tmp = tmp.replace("%", "");
        tmp = tmp.replace(",", "");
        tmp = tmp.replace("+", "");

        BigDecimal b = BigDecimal.valueOf(Double.valueOf(tmp));
        b=b.setScale(scale);
        return b;
    }

}