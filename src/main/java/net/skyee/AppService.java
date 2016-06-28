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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AppService extends Application<AppConf> {
    private Context context;

    private final Logger logger = LoggerFactory.getLogger(AppService.class);

    public static void main(String[] args) throws Exception {
        new AppService().run(args);
    }

    @Override
    public void initialize(Bootstrap<AppConf> bootstrap) {

        bootstrap.addBundle(new AssetsBundle("/web", "/web", "index.html", "web"));

        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        bootstrap.getConfigurationSourceProvider(), new EnvironmentVariableSubstitutor(false)
                )
        );

        bootstrap.addBundle(new JobsBundle("net.skyee.schedulers"));

    }

    @Override
    public void run(AppConf appConf, Environment environment) throws Exception {
        logger.info("running DropWizard!");

        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(environment, appConf.getDataSourceFactory(), "mariadb");

        final Template template = appConf.buildTemplate();

        context = Context.getInstance().updateDBInterface(jdbi);
        TemplateResource templateResource = new TemplateResource(template, context.templateDAO());
        environment.jersey().register(templateResource);
//        environment.jersey().register(MultiPartFeature.class);
    }
}
