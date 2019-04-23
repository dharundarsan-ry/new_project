package com.csl456.bikerentalapp.resources;

import com.csl456.bikerentalapp.core.Session;
import com.csl456.bikerentalapp.core.User;
import com.csl456.bikerentalapp.core.UserRole;
import com.csl456.bikerentalapp.db.SessionDAO;
import com.csl456.bikerentalapp.db.UserDAO;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(DropwizardExtensionsSupport.class)
class SessionResourceTest {
    private static final SessionDAO SESSION_DAO = mock(SessionDAO.class);
    private static final UserDAO USER_DAO = mock(UserDAO.class);

    private static final ResourceExtension RESOURCES =
            ResourceExtension.builder()
                    .addResource(new SessionResource(USER_DAO, SESSION_DAO)).build();

    private final ArgumentCaptor<Session> sessionCaptor =
            ArgumentCaptor.forClass(Session.class);

    private Session session;
    private User user;

    @Test
    void login() {
        when(USER_DAO.findUsersByUsernameAndPassword("aditya", "abc"))
                .thenReturn(user);
        MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();
        formData.add("username", "aditya");
        formData.add("password", "abc");
        final Response response =
                RESOURCES.target("/session")
                        .request()
                        .post(Entity.form(formData));
        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);
        verify(SESSION_DAO).insert(sessionCaptor.capture());
        Session capturedSession = sessionCaptor.getValue();
        assertThat(capturedSession.getIdentity()).isEqualTo(session.getIdentity());
    }

    @Test
    void logout() {
        when(SESSION_DAO.remove(anyString())).thenReturn(1);
        final Response response =
                RESOURCES.target("/session").request().delete();
        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);
    }

    @BeforeEach
    void setUp() {
        user = new User("aditya", "abc", UserRole.NORMAL_USER, 1);
        session = new Session("aditya");
    }

    @AfterEach
    void tearDown() {
        reset(SESSION_DAO);
    }

}