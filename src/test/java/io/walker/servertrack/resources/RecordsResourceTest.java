package io.walker.servertrack.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableList;

import io.dropwizard.testing.junit.ResourceTestRule;
import io.walker.servertrack.core.Server;
import io.walker.servertrack.db.ServerDAO;
import io.walker.servertrack.resources.RecordsResource;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link PeopleResource}.
 */
@RunWith(MockitoJUnitRunner.class)
public class RecordsResourceTest {
    private static final ServerDAO dao = mock(ServerDAO.class);
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new RecordsResource(dao))
            .build();
    @Captor
    private ArgumentCaptor<Server> serverCaptor;
    private Server server;

    @Before
    public void setUp() {
        server = new Server();
        server.setServerName("pri");
        server.setCpuLoad(11000000.00);
        server.setMemLoad(5.009889);
    }

    @After
    public void tearDown() {
        reset(dao);
    }

    @Test
    public void createServer() throws JsonProcessingException {
        when(dao.create(any(Server.class))).thenReturn(server);
        final Response response = resources.client().target("/records")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(server, MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);
        verify(dao).create(serverCaptor.capture());
        assertThat(serverCaptor.getValue()).isEqualTo(server);
    }

    @Test
    public void listPeople() throws Exception {
        final ImmutableList<Server> records = ImmutableList.of(server);
        when(dao.findAll()).thenReturn(records);

        final List<Server> response = resources.client().target("/records")
                .request().get(new GenericType<List<Server>>() {});

        verify(dao).findAll();
        assertThat(response).containsAll(records);
    }
}
