package io.walker.servertrack.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "Server")
@NamedQueries({
        @NamedQuery(
                name = "io.walker.servertrack.core.Server.findAll",
                query = "SELECT s FROM Server s"
        ),
        @NamedQuery(
                name = "io.walker.servertrack.core.Server.findByName",
                query = "SELECT s FROM Server s WHERE s.serverName = :serverName"
        )
})
public class Server {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "serverName", nullable = false)
    private String serverName;

    @Column(name = "cpuLoad", nullable = false)
    private Double cpuLoad;
    
    @Column(name = "memLoad", nullable = false)
    private Double memLoad;
    
    @Column(name = "timestamp", nullable = false)
    private long timestamp;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public Double getCpuLoad() {
		return cpuLoad;
	}

	public void setCpuLoad(Double cpuLoad) {
		this.cpuLoad = cpuLoad;
	}

	public Double getMemLoad() {
		return memLoad;
	}

	public void setMemLoad(Double memLoad) {
		this.memLoad = memLoad;
	}

    public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Server)) return false;

        final Server that = (Server) o;

        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.serverName, that.serverName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, serverName);
    }
}
