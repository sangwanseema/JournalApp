package com.flipkart.core;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;


import static io.dropwizard.jackson.Jackson.newObjectMapper;

public class UserTest
{
    private static final ObjectMapper MAPPER = newObjectMapper();

    @Test
    public void testSerialization() throws Exception
    {
        final User user = new User("Radha","Radha","Radha@gmail.com");
        final String json = MAPPER.writeValueAsString(MAPPER.readValue(getClass().getResourceAsStream("/fixtures/user.json"), User.class));
        assertThat(MAPPER.writeValueAsString(user)).isEqualTo(json);
    }

    @Test
    public void testDeserialization() throws Exception
    {

        final User expectedUser = new User("Radha", "Radha", "Radha@gmail.com");
        User actualUser = MAPPER.readValue(getClass().getResource("/fixtures/user.json"), User.class);
        System.out.println("Expected: " + expectedUser);
        System.out.println("Actual: " + actualUser);
        assertThat(actualUser).isEqualTo(expectedUser);
    }
}
