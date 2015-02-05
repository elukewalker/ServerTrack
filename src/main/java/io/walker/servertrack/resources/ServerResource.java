package io.walker.servertrack.resources;

import io.dropwizard.hibernate.UnitOfWork;
import io.walker.servertrack.core.Server;
import io.walker.servertrack.db.ServerDAO;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Path("/server/{serverName}")
@Produces(MediaType.APPLICATION_JSON)
public class ServerResource {
	private final ServerDAO serverDAO;

    public ServerResource(ServerDAO serverDAO) {
        this.serverDAO = serverDAO;
    }

    @GET
    @UnitOfWork
    @Timed
    public List<Server> getPerson(@PathParam("serverName") String name) {
        return findSafely(name);
    }
    
    @GET
    @Path("/byMinute")
    @UnitOfWork
    public List<Server> getServerByMinute(@PathParam("serverName") String name) {
        return getAvgLoadByMinute(name);
    }
    
    @GET
    @Path("/byHour")
    @UnitOfWork
    public List<Server> getServerByHour(@PathParam("serverName") String name) {
        return getAvgLoadByHour(name);
    }
        
    private List<Server> findSafely(String serverName) {
        final List<Server> server = serverDAO.findByName(serverName);
        if (server.isEmpty()) {
            throw new NotFoundException("No such server.");
        }
        return server;
    }
    
    private List<Server> getAvgLoadByMinute(String serverName) {
    	final List<Server> samples = findSafely(serverName);
    	return getAvgLoadByPeriod(samples, serverName, 1000*60, 60);
    }
    
    private List<Server> getAvgLoadByHour(String serverName) {
    	final List<Server> samples = findSafely(serverName);
    	return getAvgLoadByPeriod(samples, serverName, 1000*60*60, 24);
    }
    
    private Server generate(ArrayList<Double> cpu, ArrayList<Double> mem, String serverName, long currentSecond){
    	Double sum = 0.0;
		for(int j=0; j < cpu.size() ; j++)
			sum = sum + cpu.get(j);
		Double cpuAvg = sum / cpu.size();
		
		sum = 0.0;
		for(int j=0; j < mem.size() ; j++)
			sum = sum + mem.get(j);
		Double memAvg = sum / mem.size();
		
		final Server server = new Server();
		server.setCpuLoad(cpuAvg);
		server.setMemLoad(memAvg);
		server.setServerName(serverName);
		server.setTimestamp(currentSecond);
		
		return server;
    }
    
    private List<Server> getAvgLoadByPeriod(List<Server> samples, String serverName, long period, int limit) {
    	CopyOnWriteArrayList<Server> list = new CopyOnWriteArrayList<Server>();
    	
    	if(samples != null && samples.size() > 0){
    		ArrayList<Double> cpu = new ArrayList<Double>();
    		ArrayList<Double> mem = new ArrayList<Double>();
    		long currentSecond = samples.get(samples.size()-1).getTimestamp();
    		
    		for(int i=samples.size()-1; i >= 0; i--) {
    			long diff = Math.abs(samples.get(i).getTimestamp() - currentSecond);
    			
    			if(diff < period){	// Period of 1 hour
    				// Within the current second
    				cpu.add(samples.get(i).getCpuLoad());
    				mem.add(samples.get(i).getMemLoad());
    			} else {
    				// New period
    				
    				list.add(generate(cpu, mem, serverName, currentSecond));
    				
    				// Set new current second
    				currentSecond = samples.get(i).getTimestamp();
    				cpu.clear();
    				mem.clear();
    				cpu.add(samples.get(i).getCpuLoad());
    				mem.add(samples.get(i).getMemLoad());
    			}
    		}
    		// catch the case we are in the last period
    		if(!cpu.isEmpty()){
    			list.add(generate(cpu, mem, serverName, currentSecond));
    		}
    	}
    	if(list.size() > limit){
    		return list.subList(0, limit+1);
    	} else {
    		return list;
    	}
    	
    }
}
