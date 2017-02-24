package io.smsc.repository;

import io.smsc.AbstractTest;
import io.smsc.model.Authority;
import io.smsc.model.Role;
import org.junit.Test;
import org.springframework.security.test.context.support.WithMockUser;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser(username = "Admin", roles = {"ADMIN"})
public class AuthorityRestTest extends AbstractTest {

    @Test
    public void testGetSingleAuthority() throws Exception {
        mockMvc.perform(get("/rest/repository/authorities/1"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.name", is("EXECUTE_PROCESS")));
    }

    @Test
    public void testAuthorityNotFound() throws Exception {
        mockMvc.perform(get("/rest/repository/authorities/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllAuthorities() throws Exception {
        mockMvc.perform(get("/rest/repository/authorities"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$._embedded.authorities", hasSize(2)))
                .andExpect(jsonPath("$._embedded.authorities[0].name", is("EXECUTE_PROCESS")))
                .andExpect(jsonPath("$._embedded.authorities[1].name", is("KILL_PROCESS")));
    }

    @Test
    public void testCreateAuthority() throws Exception {
        Authority authority = new Authority();
        authority.setName("NEW_AUTHORITY");
        String authorityJson = json(authority);
        this.mockMvc.perform(post("/rest/repository/authorities")
                .contentType("application/json;charset=UTF-8")
                .content(authorityJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void testDeleteAuthority() throws Exception {
        mockMvc.perform(delete("/rest/repository/authorities/1"));
        mockMvc.perform(get("/rest/repository/authorities/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateAuthority() throws Exception {
        Role role = new Role();
        role.setId(1L);
        role.setName("NEW_AUTHORITY");
        String authorityJson = json(role);
        mockMvc.perform(put("/rest/repository/authorities/1")
                .contentType("application/json;charset=UTF-8")
                .content(authorityJson))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rest/repository/authorities/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.name", is("NEW_AUTHORITY")));
    }
}