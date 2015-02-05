package io.walker.servertrack.resources;

import io.dropwizard.hibernate.UnitOfWork;
import io.walker.servertrack.core.Server;
import io.walker.servertrack.db.ServerDAO;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import static java.lang.String.format;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;

import java.util.List;

@Path("/records")
@Produces(MediaType.APPLICATION_JSON)
public class RecordsResource {
	private final ServerDAO serverDAO;

    public RecordsResource(ServerDAO serverDAO) {
        this.serverDAO = serverDAO;
    }

    @POST
    @UnitOfWork
    public Server createServer(Server server) {
        return serverDAO.create(server);
    }

    @GET
    @UnitOfWork
    @Timed
    public List<Server> listServer(@QueryParam("serverName") Optional<String> name, @QueryParam("cpuLoad") Optional<Double> cpuLoad, @QueryParam("memLoad") Optional<Double> memLoad) {
    	if( name.isPresent() && cpuLoad.isPresent() && memLoad.isPresent()){
    		Server server = new Server();
    		server.setServerName(format(name.or("")));
    		server.setCpuLoad(cpuLoad.get());
    		server.setMemLoad(memLoad.get());
    		serverDAO.create(server);
    	}
        return serverDAO.findAll();
    }
}
