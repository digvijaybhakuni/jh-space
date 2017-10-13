package jh.mono.sqlsample.web.rest;

import jh.mono.sqlsample.JhSqlMonoSampleApp;

import jh.mono.sqlsample.domain.Site;
import jh.mono.sqlsample.domain.AppSystem;
import jh.mono.sqlsample.repository.SiteRepository;
import jh.mono.sqlsample.service.SiteService;
import jh.mono.sqlsample.service.dto.SiteDTO;
import jh.mono.sqlsample.service.mapper.SiteMapper;
import jh.mono.sqlsample.web.rest.errors.ExceptionTranslator;
import jh.mono.sqlsample.service.SiteQueryService;

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
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.List;

import static jh.mono.sqlsample.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the SiteResource REST controller.
 *
 * @see SiteResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JhSqlMonoSampleApp.class)
public class SiteResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATION_DATE_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATION_DATE_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private SiteMapper siteMapper;

    @Autowired
    private SiteService siteService;

    @Autowired
    private SiteQueryService siteQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restSiteMockMvc;

    private Site site;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final SiteResource siteResource = new SiteResource(siteService, siteQueryService);
        this.restSiteMockMvc = MockMvcBuilders.standaloneSetup(siteResource)
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
    public static Site createEntity(EntityManager em) {
        Site site = new Site()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .creationDateTime(DEFAULT_CREATION_DATE_TIME);
        // Add required entity
        AppSystem appSystem = AppSystemResourceIntTest.createEntity(em);
        em.persist(appSystem);
        em.flush();
        site.setAppSystem(appSystem);
        return site;
    }

    @Before
    public void initTest() {
        site = createEntity(em);
    }

    @Test
    @Transactional
    public void createSite() throws Exception {
        int databaseSizeBeforeCreate = siteRepository.findAll().size();

        // Create the Site
        SiteDTO siteDTO = siteMapper.toDto(site);
        restSiteMockMvc.perform(post("/api/sites")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(siteDTO)))
            .andExpect(status().isCreated());

        // Validate the Site in the database
        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeCreate + 1);
        Site testSite = siteList.get(siteList.size() - 1);
        assertThat(testSite.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSite.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testSite.getCreationDateTime()).isEqualTo(DEFAULT_CREATION_DATE_TIME);
    }

    @Test
    @Transactional
    public void createSiteWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = siteRepository.findAll().size();

        // Create the Site with an existing ID
        site.setId(1L);
        SiteDTO siteDTO = siteMapper.toDto(site);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSiteMockMvc.perform(post("/api/sites")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(siteDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Site in the database
        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = siteRepository.findAll().size();
        // set the field null
        site.setName(null);

        // Create the Site, which fails.
        SiteDTO siteDTO = siteMapper.toDto(site);

        restSiteMockMvc.perform(post("/api/sites")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(siteDTO)))
            .andExpect(status().isBadRequest());

        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSites() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList
        restSiteMockMvc.perform(get("/api/sites?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(site.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].creationDateTime").value(hasItem(sameInstant(DEFAULT_CREATION_DATE_TIME))));
    }

    @Test
    @Transactional
    public void getSite() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get the site
        restSiteMockMvc.perform(get("/api/sites/{id}", site.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(site.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.creationDateTime").value(sameInstant(DEFAULT_CREATION_DATE_TIME)));
    }

    @Test
    @Transactional
    public void getAllSitesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where name equals to DEFAULT_NAME
        defaultSiteShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the siteList where name equals to UPDATED_NAME
        defaultSiteShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllSitesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where name in DEFAULT_NAME or UPDATED_NAME
        defaultSiteShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the siteList where name equals to UPDATED_NAME
        defaultSiteShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllSitesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where name is not null
        defaultSiteShouldBeFound("name.specified=true");

        // Get all the siteList where name is null
        defaultSiteShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllSitesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where description equals to DEFAULT_DESCRIPTION
        defaultSiteShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the siteList where description equals to UPDATED_DESCRIPTION
        defaultSiteShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllSitesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultSiteShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the siteList where description equals to UPDATED_DESCRIPTION
        defaultSiteShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllSitesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where description is not null
        defaultSiteShouldBeFound("description.specified=true");

        // Get all the siteList where description is null
        defaultSiteShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    public void getAllSitesByCreationDateTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where creationDateTime equals to DEFAULT_CREATION_DATE_TIME
        defaultSiteShouldBeFound("creationDateTime.equals=" + DEFAULT_CREATION_DATE_TIME);

        // Get all the siteList where creationDateTime equals to UPDATED_CREATION_DATE_TIME
        defaultSiteShouldNotBeFound("creationDateTime.equals=" + UPDATED_CREATION_DATE_TIME);
    }

    @Test
    @Transactional
    public void getAllSitesByCreationDateTimeIsInShouldWork() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where creationDateTime in DEFAULT_CREATION_DATE_TIME or UPDATED_CREATION_DATE_TIME
        defaultSiteShouldBeFound("creationDateTime.in=" + DEFAULT_CREATION_DATE_TIME + "," + UPDATED_CREATION_DATE_TIME);

        // Get all the siteList where creationDateTime equals to UPDATED_CREATION_DATE_TIME
        defaultSiteShouldNotBeFound("creationDateTime.in=" + UPDATED_CREATION_DATE_TIME);
    }

    @Test
    @Transactional
    public void getAllSitesByCreationDateTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where creationDateTime is not null
        defaultSiteShouldBeFound("creationDateTime.specified=true");

        // Get all the siteList where creationDateTime is null
        defaultSiteShouldNotBeFound("creationDateTime.specified=false");
    }

    @Test
    @Transactional
    public void getAllSitesByCreationDateTimeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where creationDateTime greater than or equals to DEFAULT_CREATION_DATE_TIME
        defaultSiteShouldBeFound("creationDateTime.greaterOrEqualThan=" + DEFAULT_CREATION_DATE_TIME);

        // Get all the siteList where creationDateTime greater than or equals to UPDATED_CREATION_DATE_TIME
        defaultSiteShouldNotBeFound("creationDateTime.greaterOrEqualThan=" + UPDATED_CREATION_DATE_TIME);
    }

    @Test
    @Transactional
    public void getAllSitesByCreationDateTimeIsLessThanSomething() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList where creationDateTime less than or equals to DEFAULT_CREATION_DATE_TIME
        defaultSiteShouldNotBeFound("creationDateTime.lessThan=" + DEFAULT_CREATION_DATE_TIME);

        // Get all the siteList where creationDateTime less than or equals to UPDATED_CREATION_DATE_TIME
        defaultSiteShouldBeFound("creationDateTime.lessThan=" + UPDATED_CREATION_DATE_TIME);
    }


    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultSiteShouldBeFound(String filter) throws Exception {
        restSiteMockMvc.perform(get("/api/sites?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(site.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].creationDateTime").value(hasItem(sameInstant(DEFAULT_CREATION_DATE_TIME))));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultSiteShouldNotBeFound(String filter) throws Exception {
        restSiteMockMvc.perform(get("/api/sites?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingSite() throws Exception {
        // Get the site
        restSiteMockMvc.perform(get("/api/sites/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSite() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);
        int databaseSizeBeforeUpdate = siteRepository.findAll().size();

        // Update the site
        Site updatedSite = siteRepository.findOne(site.getId());
        updatedSite
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .creationDateTime(UPDATED_CREATION_DATE_TIME);
        SiteDTO siteDTO = siteMapper.toDto(updatedSite);

        restSiteMockMvc.perform(put("/api/sites")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(siteDTO)))
            .andExpect(status().isOk());

        // Validate the Site in the database
        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeUpdate);
        Site testSite = siteList.get(siteList.size() - 1);
        assertThat(testSite.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSite.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testSite.getCreationDateTime()).isEqualTo(UPDATED_CREATION_DATE_TIME);
    }

    @Test
    @Transactional
    public void updateNonExistingSite() throws Exception {
        int databaseSizeBeforeUpdate = siteRepository.findAll().size();

        // Create the Site
        SiteDTO siteDTO = siteMapper.toDto(site);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restSiteMockMvc.perform(put("/api/sites")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(siteDTO)))
            .andExpect(status().isCreated());

        // Validate the Site in the database
        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteSite() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);
        int databaseSizeBeforeDelete = siteRepository.findAll().size();

        // Get the site
        restSiteMockMvc.perform(delete("/api/sites/{id}", site.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Site.class);
        Site site1 = new Site();
        site1.setId(1L);
        Site site2 = new Site();
        site2.setId(site1.getId());
        assertThat(site1).isEqualTo(site2);
        site2.setId(2L);
        assertThat(site1).isNotEqualTo(site2);
        site1.setId(null);
        assertThat(site1).isNotEqualTo(site2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SiteDTO.class);
        SiteDTO siteDTO1 = new SiteDTO();
        siteDTO1.setId(1L);
        SiteDTO siteDTO2 = new SiteDTO();
        assertThat(siteDTO1).isNotEqualTo(siteDTO2);
        siteDTO2.setId(siteDTO1.getId());
        assertThat(siteDTO1).isEqualTo(siteDTO2);
        siteDTO2.setId(2L);
        assertThat(siteDTO1).isNotEqualTo(siteDTO2);
        siteDTO1.setId(null);
        assertThat(siteDTO1).isNotEqualTo(siteDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(siteMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(siteMapper.fromId(null)).isNull();
    }
}
