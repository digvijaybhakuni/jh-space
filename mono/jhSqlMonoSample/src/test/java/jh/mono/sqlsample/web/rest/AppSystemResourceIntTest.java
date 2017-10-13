package jh.mono.sqlsample.web.rest;

import jh.mono.sqlsample.JhSqlMonoSampleApp;

import jh.mono.sqlsample.domain.AppSystem;
import jh.mono.sqlsample.repository.SystemRepository;
import jh.mono.sqlsample.service.SystemService;
import jh.mono.sqlsample.service.dto.SystemDTO;
import jh.mono.sqlsample.service.mapper.SystemMapper;
import jh.mono.sqlsample.web.rest.errors.ExceptionTranslator;
import jh.mono.sqlsample.service.SystemQueryService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the SystemResource REST controller.
 *
 * @see SystemResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JhSqlMonoSampleApp.class)
public class AppSystemResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    @Autowired
    private SystemRepository systemRepository;

    @Autowired
    private SystemMapper systemMapper;

    @Autowired
    private SystemService systemService;

    @Autowired
    private SystemQueryService systemQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restSystemMockMvc;

    private AppSystem appSystem;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final SystemResource systemResource = new SystemResource(systemService, systemQueryService);
        this.restSystemMockMvc = MockMvcBuilders.standaloneSetup(systemResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AppSystem createEntity(EntityManager em) {
        AppSystem appSystem = new AppSystem()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION);
        return appSystem;
    }

    @Before
    public void initTest() {
        appSystem = createEntity(em);
    }

    @Test
    @Transactional
    public void createSystem() throws Exception {
        int databaseSizeBeforeCreate = systemRepository.findAll().size();

        // Create the AppSystem
        SystemDTO systemDTO = systemMapper.toDto(appSystem);
        restSystemMockMvc.perform(post("/api/systems")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(systemDTO)))
            .andExpect(status().isCreated());

        // Validate the AppSystem in the database
        List<AppSystem> appSystemList = systemRepository.findAll();
        assertThat(appSystemList).hasSize(databaseSizeBeforeCreate + 1);
        AppSystem testAppSystem = appSystemList.get(appSystemList.size() - 1);
        assertThat(testAppSystem.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testAppSystem.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createSystemWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = systemRepository.findAll().size();

        // Create the AppSystem with an existing ID
        appSystem.setId(1L);
        SystemDTO systemDTO = systemMapper.toDto(appSystem);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSystemMockMvc.perform(post("/api/systems")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(systemDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AppSystem in the database
        List<AppSystem> appSystemList = systemRepository.findAll();
        assertThat(appSystemList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = systemRepository.findAll().size();
        // set the field null
        appSystem.setDescription(null);

        // Create the AppSystem, which fails.
        SystemDTO systemDTO = systemMapper.toDto(appSystem);

        restSystemMockMvc.perform(post("/api/systems")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(systemDTO)))
            .andExpect(status().isBadRequest());

        List<AppSystem> appSystemList = systemRepository.findAll();
        assertThat(appSystemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSystems() throws Exception {
        // Initialize the database
        systemRepository.saveAndFlush(appSystem);

        // Get all the systemList
        restSystemMockMvc.perform(get("/api/systems?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appSystem.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void getSystem() throws Exception {
        // Initialize the database
        systemRepository.saveAndFlush(appSystem);

        // Get the appSystem
        restSystemMockMvc.perform(get("/api/systems/{id}", appSystem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(appSystem.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getAllSystemsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        systemRepository.saveAndFlush(appSystem);

        // Get all the systemList where name equals to DEFAULT_NAME
        defaultSystemShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the systemList where name equals to UPDATED_NAME
        defaultSystemShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllSystemsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        systemRepository.saveAndFlush(appSystem);

        // Get all the systemList where name in DEFAULT_NAME or UPDATED_NAME
        defaultSystemShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the systemList where name equals to UPDATED_NAME
        defaultSystemShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllSystemsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        systemRepository.saveAndFlush(appSystem);

        // Get all the systemList where name is not null
        defaultSystemShouldBeFound("name.specified=true");

        // Get all the systemList where name is null
        defaultSystemShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllSystemsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        systemRepository.saveAndFlush(appSystem);

        // Get all the systemList where description equals to DEFAULT_DESCRIPTION
        defaultSystemShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the systemList where description equals to UPDATED_DESCRIPTION
        defaultSystemShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllSystemsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        systemRepository.saveAndFlush(appSystem);

        // Get all the systemList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultSystemShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the systemList where description equals to UPDATED_DESCRIPTION
        defaultSystemShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllSystemsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        systemRepository.saveAndFlush(appSystem);

        // Get all the systemList where description is not null
        defaultSystemShouldBeFound("description.specified=true");

        // Get all the systemList where description is null
        defaultSystemShouldNotBeFound("description.specified=false");
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultSystemShouldBeFound(String filter) throws Exception {
        restSystemMockMvc.perform(get("/api/systems?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appSystem.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultSystemShouldNotBeFound(String filter) throws Exception {
        restSystemMockMvc.perform(get("/api/systems?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingSystem() throws Exception {
        // Get the appSystem
        restSystemMockMvc.perform(get("/api/systems/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSystem() throws Exception {
        // Initialize the database
        systemRepository.saveAndFlush(appSystem);
        int databaseSizeBeforeUpdate = systemRepository.findAll().size();

        // Update the appSystem
        AppSystem updatedAppSystem = systemRepository.findOne(appSystem.getId());
        updatedAppSystem
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION);
        SystemDTO systemDTO = systemMapper.toDto(updatedAppSystem);

        restSystemMockMvc.perform(put("/api/systems")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(systemDTO)))
            .andExpect(status().isOk());

        // Validate the AppSystem in the database
        List<AppSystem> appSystemList = systemRepository.findAll();
        assertThat(appSystemList).hasSize(databaseSizeBeforeUpdate);
        AppSystem testAppSystem = appSystemList.get(appSystemList.size() - 1);
        assertThat(testAppSystem.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAppSystem.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void updateNonExistingSystem() throws Exception {
        int databaseSizeBeforeUpdate = systemRepository.findAll().size();

        // Create the AppSystem
        SystemDTO systemDTO = systemMapper.toDto(appSystem);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restSystemMockMvc.perform(put("/api/systems")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(systemDTO)))
            .andExpect(status().isCreated());

        // Validate the AppSystem in the database
        List<AppSystem> appSystemList = systemRepository.findAll();
        assertThat(appSystemList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteSystem() throws Exception {
        // Initialize the database
        systemRepository.saveAndFlush(appSystem);
        int databaseSizeBeforeDelete = systemRepository.findAll().size();

        // Get the appSystem
        restSystemMockMvc.perform(delete("/api/systems/{id}", appSystem.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<AppSystem> appSystemList = systemRepository.findAll();
        assertThat(appSystemList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AppSystem.class);
        AppSystem appSystem1 = new AppSystem();
        appSystem1.setId(1L);
        AppSystem appSystem2 = new AppSystem();
        appSystem2.setId(appSystem1.getId());
        assertThat(appSystem1).isEqualTo(appSystem2);
        appSystem2.setId(2L);
        assertThat(appSystem1).isNotEqualTo(appSystem2);
        appSystem1.setId(null);
        assertThat(appSystem1).isNotEqualTo(appSystem2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SystemDTO.class);
        SystemDTO systemDTO1 = new SystemDTO();
        systemDTO1.setId(1L);
        SystemDTO systemDTO2 = new SystemDTO();
        assertThat(systemDTO1).isNotEqualTo(systemDTO2);
        systemDTO2.setId(systemDTO1.getId());
        assertThat(systemDTO1).isEqualTo(systemDTO2);
        systemDTO2.setId(2L);
        assertThat(systemDTO1).isNotEqualTo(systemDTO2);
        systemDTO1.setId(null);
        assertThat(systemDTO1).isNotEqualTo(systemDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(systemMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(systemMapper.fromId(null)).isNull();
    }
}
