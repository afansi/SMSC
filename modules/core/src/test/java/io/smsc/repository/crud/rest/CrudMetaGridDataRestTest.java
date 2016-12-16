package io.smsc.repository.crud.rest;

import io.smsc.model.crud.CrudMetaGridData;
import io.smsc.AbstractTest;
import org.junit.Test;
import org.springframework.security.test.context.support.WithMockUser;

import static io.smsc.test_data.CrudMetaGridDataTestData.*;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser(username="Admin",roles = {"ADMIN"})
public class CrudMetaGridDataRestTest extends AbstractTest {

    @Test
    public void testGetSingleCrudMetaGridData() throws Exception {
        mockMvc.perform(get("/rest/repository/crud-meta-grid-data/106"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.property", is(CRUD_META_GRID_DATA_1.getProperty())))
                .andExpect(jsonPath("$.editable", is(CRUD_META_GRID_DATA_1.getEditable())))
                .andExpect(jsonPath("$.visible", is(CRUD_META_GRID_DATA_1.getVisible())))
                .andExpect(jsonPath("$.decorator", is(CRUD_META_GRID_DATA_1.getDecorator())))
                .andExpect(jsonPath("$.order", is(CRUD_META_GRID_DATA_1.getOrder())))
                .andExpect(jsonPath("$.columnWidth", is(CRUD_META_GRID_DATA_1.getColumnWidth())));
    }

    @Test
    public void testCrudMetaGridDataNotFound() throws Exception {
        mockMvc.perform(get("/rest/repository/crud-meta-grid-data/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllCrudMetaGridDatas() throws Exception {
        mockMvc.perform(get("/rest/repository/crud-meta-grid-data/search/findAll"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                // paginating is showing 20 items by default
                .andExpect(jsonPath("$._embedded.crud-meta-grid-data", hasSize(31)))
                .andExpect(jsonPath("$._embedded.crud-meta-grid-data[0].property", is(CRUD_META_GRID_DATA_1.getProperty())))
                .andExpect(jsonPath("$._embedded.crud-meta-grid-data[0].editable", is(CRUD_META_GRID_DATA_1.getEditable())))
                .andExpect(jsonPath("$._embedded.crud-meta-grid-data[0].visible", is(CRUD_META_GRID_DATA_1.getVisible())))
                .andExpect(jsonPath("$._embedded.crud-meta-grid-data[0].decorator", is(CRUD_META_GRID_DATA_1.getDecorator())))
                .andExpect(jsonPath("$._embedded.crud-meta-grid-data[0].order", is(CRUD_META_GRID_DATA_1.getOrder())))
                .andExpect(jsonPath("$._embedded.crud-meta-grid-data[0].columnWidth", is(CRUD_META_GRID_DATA_1.getColumnWidth())))
                .andExpect(jsonPath("$._embedded.crud-meta-grid-data[30].property", is(CRUD_META_GRID_DATA_31.getProperty())))
                .andExpect(jsonPath("$._embedded.crud-meta-grid-data[30].editable", is(CRUD_META_GRID_DATA_31.getEditable())))
                .andExpect(jsonPath("$._embedded.crud-meta-grid-data[30].visible", is(CRUD_META_GRID_DATA_31.getVisible())))
                .andExpect(jsonPath("$._embedded.crud-meta-grid-data[30].decorator", is(CRUD_META_GRID_DATA_31.getDecorator())))
                .andExpect(jsonPath("$._embedded.crud-meta-grid-data[30].order", is(CRUD_META_GRID_DATA_31.getOrder())))
                .andExpect(jsonPath("$._embedded.crud-meta-grid-data[30].columnWidth", is(CRUD_META_GRID_DATA_31.getColumnWidth())));
    }

    @Test
    public void testCreateCrudMetaGridData() throws Exception {
        CrudMetaGridData newCrudMetaGridData = new CrudMetaGridData(null,"defaultProperty", true,
                true, null, 10.0, 50.0);
        String crudMetaGridDataJson = json(newCrudMetaGridData);
        this.mockMvc.perform(post("/rest/repository/crud-meta-grid-data")
                .contentType(contentType)
                .content(crudMetaGridDataJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void testDeleteCrudMetaGridData() throws Exception {
        mockMvc.perform(delete("/rest/repository/crud-meta-grid-data/106"));
        mockMvc.perform(get("/rest/repository/crud-meta-grid-data/search/106"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateCrudMetaGridData() throws Exception {
        CrudMetaGridData updated = new CrudMetaGridData(CRUD_META_GRID_DATA_1);
        updated.setDecorator("newDecorator");
        updated.setEditable(false);
        updated.setOrder(20.0);
        updated.setProperty("newProperty");
        updated.setVisible(false);
        updated.setColumnWidth(30.0);
        String permissionJson = json(updated);
        mockMvc.perform(put("/rest/repository/crud-meta-grid-data/106")
                .contentType(contentType)
                .content(permissionJson))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/rest/repository/crud-meta-grid-data/106"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.property", is("newProperty")))
                .andExpect(jsonPath("$.editable", is(false)))
                .andExpect(jsonPath("$.visible", is(false)))
                .andExpect(jsonPath("$.decorator", is("newDecorator")))
                .andExpect(jsonPath("$.order", is(20.0)))
                .andExpect(jsonPath("$.columnWidth", is(30.0)));
    }
}
