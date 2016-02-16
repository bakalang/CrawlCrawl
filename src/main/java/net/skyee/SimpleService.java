package net.skyee;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.jdbi.DBIFactory;
import de.spinscale.dropwizard.jobs.JobsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import net.skyee.bean.Template;
import net.skyee.resources.TemplateResource;
import org.skife.jdbi.v2.DBI;


public class SimpleService extends Application<SampleConf> {
    private Context context;

    public static void main(String[] args) throws Exception {
        new SimpleService().run(args);
    }

    @Override
    public void initialize(Bootstrap<SampleConf> bootstrap) {

        bootstrap.addBundle(new AssetsBundle("/assets/", "/"));

        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        bootstrap.getConfigurationSourceProvider(), new EnvironmentVariableSubstitutor(false)
                )
        );

        bootstrap.addBundle(new JobsBundle("net.skyee.schedulers"));

    }

    @Override
    public void run(SampleConf sampleConf, Environment environment) throws Exception {
        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(environment, sampleConf.getDataSourceFactory(), "mariadb");
//        final StocksDAO dao = jdbi.onDemand(StocksDAO.class);

        final Template template = sampleConf.buildTemplate();
//        environment.jersey().register(new TemplateResource(template, dao));

        context = Context.getInstance().updateDBInterface(jdbi);
        TemplateResource templateResource = new TemplateResource(template, context.templateDAO());
        environment.jersey().register(templateResource);
    }
}
