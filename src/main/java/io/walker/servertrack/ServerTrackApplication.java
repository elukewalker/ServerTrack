package io.walker.servertrack;

import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.walker.servertrack.core.Server;
import io.walker.servertrack.db.ServerDAO;
import io.walker.servertrack.resources.RecordsResource;
import io.walker.servertrack.resources.ServerResource;

public class ServerTrackApplication extends Application<ServerTrackConfiguration> {
    public static void main(String[] args) throws Exception {
        new ServerTrackApplication().run(args);
    }

    private final HibernateBundle<ServerTrackConfiguration> hibernateBundle =
            new HibernateBundle<ServerTrackConfiguration>(Server.class) {
                @Override
                public DataSourceFactory getDataSourceFactory(ServerTrackConfiguration configuration) {
                    return configuration.getDataSourceFactory();
                }
            };
            
    @Override
    public String getName() {
        return "ServerTrack";
    }

    @Override
    public void initialize(Bootstrap<ServerTrackConfiguration> bootstrap) {
        bootstrap.addBundle(new MigrationsBundle<ServerTrackConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(ServerTrackConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
        bootstrap.addBundle(hibernateBundle);
    }

    @Override
    public void run(ServerTrackConfiguration configuration, Environment environment) {
        final ServerDAO sdao = new ServerDAO(hibernateBundle.getSessionFactory());

        environment.jersey().register(new RecordsResource(sdao));
        environment.jersey().register(new ServerResource(sdao));
    }
}
