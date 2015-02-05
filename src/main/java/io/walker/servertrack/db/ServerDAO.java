package io.walker.servertrack.db;

import com.google.common.base.Optional;

import io.dropwizard.hibernate.AbstractDAO;
import io.walker.servertrack.core.Server;

import org.hibernate.SessionFactory;

import java.sql.Timestamp;
import java.util.List;

public class ServerDAO extends AbstractDAO<Server> {
	
	public ServerDAO(SessionFactory factory) {
        super(factory);
    }

    public Optional<Server> findById(Long id) {
        return Optional.fromNullable(get(id));
    }

    public Server create(Server server) {
    	server.setTimestamp(System.currentTimeMillis());
        return persist(server);
    }

    public List<Server> findAll() {
        return list(namedQuery("io.walker.servertrack.core.Server.findAll"));
    }

	public List<Server> findByName(String serverName) {
		return list(namedQuery("io.walker.servertrack.core.Server.findByName").setString("serverName", serverName));
	}

}