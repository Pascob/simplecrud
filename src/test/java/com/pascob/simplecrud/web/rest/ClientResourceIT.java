package com.pascob.simplecrud.web.rest;

import com.pascob.simplecrud.SimpleCrudApp;
import com.pascob.simplecrud.domain.Client;
import com.pascob.simplecrud.repository.ClientRepository;
import com.pascob.simplecrud.service.ClientService;
import com.pascob.simplecrud.service.dto.ClientCriteria;
import com.pascob.simplecrud.service.ClientQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link ClientResource} REST controller.
 */
@SpringBootTest(classes = SimpleCrudApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class ClientResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_TELEPHONE = "AAAAAAAAAA";
    private static final String UPDATED_TELEPHONE = "BBBBBBBBBB";

    private static final Double DEFAULT_SOLDE = 1D;
    private static final Double UPDATED_SOLDE = 2D;
    private static final Double SMALLER_SOLDE = 1D - 1D;

    private static final Boolean DEFAULT_IS_ACTIF = false;
    private static final Boolean UPDATED_IS_ACTIF = true;

    private static final byte[] DEFAULT_PHOTO = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_PHOTO = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_PHOTO_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_PHOTO_CONTENT_TYPE = "image/png";

    private static final LocalDate DEFAULT_DATE_CREATION = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_CREATION = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DATE_CREATION = LocalDate.ofEpochDay(-1L);

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientQueryService clientQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClientMockMvc;

    private Client client;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Client createEntity(EntityManager em) {
        Client client = new Client()
            .nom(DEFAULT_NOM)
            .telephone(DEFAULT_TELEPHONE)
            .solde(DEFAULT_SOLDE)
            .isActif(DEFAULT_IS_ACTIF)
            .photo(DEFAULT_PHOTO)
            .photoContentType(DEFAULT_PHOTO_CONTENT_TYPE)
            .dateCreation(DEFAULT_DATE_CREATION);
        return client;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Client createUpdatedEntity(EntityManager em) {
        Client client = new Client()
            .nom(UPDATED_NOM)
            .telephone(UPDATED_TELEPHONE)
            .solde(UPDATED_SOLDE)
            .isActif(UPDATED_IS_ACTIF)
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
            .dateCreation(UPDATED_DATE_CREATION);
        return client;
    }

    @BeforeEach
    public void initTest() {
        client = createEntity(em);
    }

    @Test
    @Transactional
    public void createClient() throws Exception {
        int databaseSizeBeforeCreate = clientRepository.findAll().size();
        // Create the Client
        restClientMockMvc.perform(post("/api/clients")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(client)))
            .andExpect(status().isCreated());

        // Validate the Client in the database
        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeCreate + 1);
        Client testClient = clientList.get(clientList.size() - 1);
        assertThat(testClient.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testClient.getTelephone()).isEqualTo(DEFAULT_TELEPHONE);
        assertThat(testClient.getSolde()).isEqualTo(DEFAULT_SOLDE);
        assertThat(testClient.isIsActif()).isEqualTo(DEFAULT_IS_ACTIF);
        assertThat(testClient.getPhoto()).isEqualTo(DEFAULT_PHOTO);
        assertThat(testClient.getPhotoContentType()).isEqualTo(DEFAULT_PHOTO_CONTENT_TYPE);
        assertThat(testClient.getDateCreation()).isEqualTo(DEFAULT_DATE_CREATION);
    }

    @Test
    @Transactional
    public void createClientWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = clientRepository.findAll().size();

        // Create the Client with an existing ID
        client.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restClientMockMvc.perform(post("/api/clients")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(client)))
            .andExpect(status().isBadRequest());

        // Validate the Client in the database
        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNomIsRequired() throws Exception {
        int databaseSizeBeforeTest = clientRepository.findAll().size();
        // set the field null
        client.setNom(null);

        // Create the Client, which fails.


        restClientMockMvc.perform(post("/api/clients")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(client)))
            .andExpect(status().isBadRequest());

        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTelephoneIsRequired() throws Exception {
        int databaseSizeBeforeTest = clientRepository.findAll().size();
        // set the field null
        client.setTelephone(null);

        // Create the Client, which fails.


        restClientMockMvc.perform(post("/api/clients")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(client)))
            .andExpect(status().isBadRequest());

        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllClients() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList
        restClientMockMvc.perform(get("/api/clients?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(client.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE)))
            .andExpect(jsonPath("$.[*].solde").value(hasItem(DEFAULT_SOLDE.doubleValue())))
            .andExpect(jsonPath("$.[*].isActif").value(hasItem(DEFAULT_IS_ACTIF.booleanValue())))
            .andExpect(jsonPath("$.[*].photoContentType").value(hasItem(DEFAULT_PHOTO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].photo").value(hasItem(Base64Utils.encodeToString(DEFAULT_PHOTO))))
            .andExpect(jsonPath("$.[*].dateCreation").value(hasItem(DEFAULT_DATE_CREATION.toString())));
    }
    
    @Test
    @Transactional
    public void getClient() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get the client
        restClientMockMvc.perform(get("/api/clients/{id}", client.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(client.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.telephone").value(DEFAULT_TELEPHONE))
            .andExpect(jsonPath("$.solde").value(DEFAULT_SOLDE.doubleValue()))
            .andExpect(jsonPath("$.isActif").value(DEFAULT_IS_ACTIF.booleanValue()))
            .andExpect(jsonPath("$.photoContentType").value(DEFAULT_PHOTO_CONTENT_TYPE))
            .andExpect(jsonPath("$.photo").value(Base64Utils.encodeToString(DEFAULT_PHOTO)))
            .andExpect(jsonPath("$.dateCreation").value(DEFAULT_DATE_CREATION.toString()));
    }


    @Test
    @Transactional
    public void getClientsByIdFiltering() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        Long id = client.getId();

        defaultClientShouldBeFound("id.equals=" + id);
        defaultClientShouldNotBeFound("id.notEquals=" + id);

        defaultClientShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultClientShouldNotBeFound("id.greaterThan=" + id);

        defaultClientShouldBeFound("id.lessThanOrEqual=" + id);
        defaultClientShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllClientsByNomIsEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where nom equals to DEFAULT_NOM
        defaultClientShouldBeFound("nom.equals=" + DEFAULT_NOM);

        // Get all the clientList where nom equals to UPDATED_NOM
        defaultClientShouldNotBeFound("nom.equals=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    public void getAllClientsByNomIsNotEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where nom not equals to DEFAULT_NOM
        defaultClientShouldNotBeFound("nom.notEquals=" + DEFAULT_NOM);

        // Get all the clientList where nom not equals to UPDATED_NOM
        defaultClientShouldBeFound("nom.notEquals=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    public void getAllClientsByNomIsInShouldWork() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where nom in DEFAULT_NOM or UPDATED_NOM
        defaultClientShouldBeFound("nom.in=" + DEFAULT_NOM + "," + UPDATED_NOM);

        // Get all the clientList where nom equals to UPDATED_NOM
        defaultClientShouldNotBeFound("nom.in=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    public void getAllClientsByNomIsNullOrNotNull() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where nom is not null
        defaultClientShouldBeFound("nom.specified=true");

        // Get all the clientList where nom is null
        defaultClientShouldNotBeFound("nom.specified=false");
    }
                @Test
    @Transactional
    public void getAllClientsByNomContainsSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where nom contains DEFAULT_NOM
        defaultClientShouldBeFound("nom.contains=" + DEFAULT_NOM);

        // Get all the clientList where nom contains UPDATED_NOM
        defaultClientShouldNotBeFound("nom.contains=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    public void getAllClientsByNomNotContainsSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where nom does not contain DEFAULT_NOM
        defaultClientShouldNotBeFound("nom.doesNotContain=" + DEFAULT_NOM);

        // Get all the clientList where nom does not contain UPDATED_NOM
        defaultClientShouldBeFound("nom.doesNotContain=" + UPDATED_NOM);
    }


    @Test
    @Transactional
    public void getAllClientsByTelephoneIsEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where telephone equals to DEFAULT_TELEPHONE
        defaultClientShouldBeFound("telephone.equals=" + DEFAULT_TELEPHONE);

        // Get all the clientList where telephone equals to UPDATED_TELEPHONE
        defaultClientShouldNotBeFound("telephone.equals=" + UPDATED_TELEPHONE);
    }

    @Test
    @Transactional
    public void getAllClientsByTelephoneIsNotEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where telephone not equals to DEFAULT_TELEPHONE
        defaultClientShouldNotBeFound("telephone.notEquals=" + DEFAULT_TELEPHONE);

        // Get all the clientList where telephone not equals to UPDATED_TELEPHONE
        defaultClientShouldBeFound("telephone.notEquals=" + UPDATED_TELEPHONE);
    }

    @Test
    @Transactional
    public void getAllClientsByTelephoneIsInShouldWork() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where telephone in DEFAULT_TELEPHONE or UPDATED_TELEPHONE
        defaultClientShouldBeFound("telephone.in=" + DEFAULT_TELEPHONE + "," + UPDATED_TELEPHONE);

        // Get all the clientList where telephone equals to UPDATED_TELEPHONE
        defaultClientShouldNotBeFound("telephone.in=" + UPDATED_TELEPHONE);
    }

    @Test
    @Transactional
    public void getAllClientsByTelephoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where telephone is not null
        defaultClientShouldBeFound("telephone.specified=true");

        // Get all the clientList where telephone is null
        defaultClientShouldNotBeFound("telephone.specified=false");
    }
                @Test
    @Transactional
    public void getAllClientsByTelephoneContainsSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where telephone contains DEFAULT_TELEPHONE
        defaultClientShouldBeFound("telephone.contains=" + DEFAULT_TELEPHONE);

        // Get all the clientList where telephone contains UPDATED_TELEPHONE
        defaultClientShouldNotBeFound("telephone.contains=" + UPDATED_TELEPHONE);
    }

    @Test
    @Transactional
    public void getAllClientsByTelephoneNotContainsSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where telephone does not contain DEFAULT_TELEPHONE
        defaultClientShouldNotBeFound("telephone.doesNotContain=" + DEFAULT_TELEPHONE);

        // Get all the clientList where telephone does not contain UPDATED_TELEPHONE
        defaultClientShouldBeFound("telephone.doesNotContain=" + UPDATED_TELEPHONE);
    }


    @Test
    @Transactional
    public void getAllClientsBySoldeIsEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where solde equals to DEFAULT_SOLDE
        defaultClientShouldBeFound("solde.equals=" + DEFAULT_SOLDE);

        // Get all the clientList where solde equals to UPDATED_SOLDE
        defaultClientShouldNotBeFound("solde.equals=" + UPDATED_SOLDE);
    }

    @Test
    @Transactional
    public void getAllClientsBySoldeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where solde not equals to DEFAULT_SOLDE
        defaultClientShouldNotBeFound("solde.notEquals=" + DEFAULT_SOLDE);

        // Get all the clientList where solde not equals to UPDATED_SOLDE
        defaultClientShouldBeFound("solde.notEquals=" + UPDATED_SOLDE);
    }

    @Test
    @Transactional
    public void getAllClientsBySoldeIsInShouldWork() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where solde in DEFAULT_SOLDE or UPDATED_SOLDE
        defaultClientShouldBeFound("solde.in=" + DEFAULT_SOLDE + "," + UPDATED_SOLDE);

        // Get all the clientList where solde equals to UPDATED_SOLDE
        defaultClientShouldNotBeFound("solde.in=" + UPDATED_SOLDE);
    }

    @Test
    @Transactional
    public void getAllClientsBySoldeIsNullOrNotNull() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where solde is not null
        defaultClientShouldBeFound("solde.specified=true");

        // Get all the clientList where solde is null
        defaultClientShouldNotBeFound("solde.specified=false");
    }

    @Test
    @Transactional
    public void getAllClientsBySoldeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where solde is greater than or equal to DEFAULT_SOLDE
        defaultClientShouldBeFound("solde.greaterThanOrEqual=" + DEFAULT_SOLDE);

        // Get all the clientList where solde is greater than or equal to UPDATED_SOLDE
        defaultClientShouldNotBeFound("solde.greaterThanOrEqual=" + UPDATED_SOLDE);
    }

    @Test
    @Transactional
    public void getAllClientsBySoldeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where solde is less than or equal to DEFAULT_SOLDE
        defaultClientShouldBeFound("solde.lessThanOrEqual=" + DEFAULT_SOLDE);

        // Get all the clientList where solde is less than or equal to SMALLER_SOLDE
        defaultClientShouldNotBeFound("solde.lessThanOrEqual=" + SMALLER_SOLDE);
    }

    @Test
    @Transactional
    public void getAllClientsBySoldeIsLessThanSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where solde is less than DEFAULT_SOLDE
        defaultClientShouldNotBeFound("solde.lessThan=" + DEFAULT_SOLDE);

        // Get all the clientList where solde is less than UPDATED_SOLDE
        defaultClientShouldBeFound("solde.lessThan=" + UPDATED_SOLDE);
    }

    @Test
    @Transactional
    public void getAllClientsBySoldeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where solde is greater than DEFAULT_SOLDE
        defaultClientShouldNotBeFound("solde.greaterThan=" + DEFAULT_SOLDE);

        // Get all the clientList where solde is greater than SMALLER_SOLDE
        defaultClientShouldBeFound("solde.greaterThan=" + SMALLER_SOLDE);
    }


    @Test
    @Transactional
    public void getAllClientsByIsActifIsEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where isActif equals to DEFAULT_IS_ACTIF
        defaultClientShouldBeFound("isActif.equals=" + DEFAULT_IS_ACTIF);

        // Get all the clientList where isActif equals to UPDATED_IS_ACTIF
        defaultClientShouldNotBeFound("isActif.equals=" + UPDATED_IS_ACTIF);
    }

    @Test
    @Transactional
    public void getAllClientsByIsActifIsNotEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where isActif not equals to DEFAULT_IS_ACTIF
        defaultClientShouldNotBeFound("isActif.notEquals=" + DEFAULT_IS_ACTIF);

        // Get all the clientList where isActif not equals to UPDATED_IS_ACTIF
        defaultClientShouldBeFound("isActif.notEquals=" + UPDATED_IS_ACTIF);
    }

    @Test
    @Transactional
    public void getAllClientsByIsActifIsInShouldWork() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where isActif in DEFAULT_IS_ACTIF or UPDATED_IS_ACTIF
        defaultClientShouldBeFound("isActif.in=" + DEFAULT_IS_ACTIF + "," + UPDATED_IS_ACTIF);

        // Get all the clientList where isActif equals to UPDATED_IS_ACTIF
        defaultClientShouldNotBeFound("isActif.in=" + UPDATED_IS_ACTIF);
    }

    @Test
    @Transactional
    public void getAllClientsByIsActifIsNullOrNotNull() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where isActif is not null
        defaultClientShouldBeFound("isActif.specified=true");

        // Get all the clientList where isActif is null
        defaultClientShouldNotBeFound("isActif.specified=false");
    }

    @Test
    @Transactional
    public void getAllClientsByDateCreationIsEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where dateCreation equals to DEFAULT_DATE_CREATION
        defaultClientShouldBeFound("dateCreation.equals=" + DEFAULT_DATE_CREATION);

        // Get all the clientList where dateCreation equals to UPDATED_DATE_CREATION
        defaultClientShouldNotBeFound("dateCreation.equals=" + UPDATED_DATE_CREATION);
    }

    @Test
    @Transactional
    public void getAllClientsByDateCreationIsNotEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where dateCreation not equals to DEFAULT_DATE_CREATION
        defaultClientShouldNotBeFound("dateCreation.notEquals=" + DEFAULT_DATE_CREATION);

        // Get all the clientList where dateCreation not equals to UPDATED_DATE_CREATION
        defaultClientShouldBeFound("dateCreation.notEquals=" + UPDATED_DATE_CREATION);
    }

    @Test
    @Transactional
    public void getAllClientsByDateCreationIsInShouldWork() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where dateCreation in DEFAULT_DATE_CREATION or UPDATED_DATE_CREATION
        defaultClientShouldBeFound("dateCreation.in=" + DEFAULT_DATE_CREATION + "," + UPDATED_DATE_CREATION);

        // Get all the clientList where dateCreation equals to UPDATED_DATE_CREATION
        defaultClientShouldNotBeFound("dateCreation.in=" + UPDATED_DATE_CREATION);
    }

    @Test
    @Transactional
    public void getAllClientsByDateCreationIsNullOrNotNull() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where dateCreation is not null
        defaultClientShouldBeFound("dateCreation.specified=true");

        // Get all the clientList where dateCreation is null
        defaultClientShouldNotBeFound("dateCreation.specified=false");
    }

    @Test
    @Transactional
    public void getAllClientsByDateCreationIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where dateCreation is greater than or equal to DEFAULT_DATE_CREATION
        defaultClientShouldBeFound("dateCreation.greaterThanOrEqual=" + DEFAULT_DATE_CREATION);

        // Get all the clientList where dateCreation is greater than or equal to UPDATED_DATE_CREATION
        defaultClientShouldNotBeFound("dateCreation.greaterThanOrEqual=" + UPDATED_DATE_CREATION);
    }

    @Test
    @Transactional
    public void getAllClientsByDateCreationIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where dateCreation is less than or equal to DEFAULT_DATE_CREATION
        defaultClientShouldBeFound("dateCreation.lessThanOrEqual=" + DEFAULT_DATE_CREATION);

        // Get all the clientList where dateCreation is less than or equal to SMALLER_DATE_CREATION
        defaultClientShouldNotBeFound("dateCreation.lessThanOrEqual=" + SMALLER_DATE_CREATION);
    }

    @Test
    @Transactional
    public void getAllClientsByDateCreationIsLessThanSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where dateCreation is less than DEFAULT_DATE_CREATION
        defaultClientShouldNotBeFound("dateCreation.lessThan=" + DEFAULT_DATE_CREATION);

        // Get all the clientList where dateCreation is less than UPDATED_DATE_CREATION
        defaultClientShouldBeFound("dateCreation.lessThan=" + UPDATED_DATE_CREATION);
    }

    @Test
    @Transactional
    public void getAllClientsByDateCreationIsGreaterThanSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where dateCreation is greater than DEFAULT_DATE_CREATION
        defaultClientShouldNotBeFound("dateCreation.greaterThan=" + DEFAULT_DATE_CREATION);

        // Get all the clientList where dateCreation is greater than SMALLER_DATE_CREATION
        defaultClientShouldBeFound("dateCreation.greaterThan=" + SMALLER_DATE_CREATION);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultClientShouldBeFound(String filter) throws Exception {
        restClientMockMvc.perform(get("/api/clients?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(client.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE)))
            .andExpect(jsonPath("$.[*].solde").value(hasItem(DEFAULT_SOLDE.doubleValue())))
            .andExpect(jsonPath("$.[*].isActif").value(hasItem(DEFAULT_IS_ACTIF.booleanValue())))
            .andExpect(jsonPath("$.[*].photoContentType").value(hasItem(DEFAULT_PHOTO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].photo").value(hasItem(Base64Utils.encodeToString(DEFAULT_PHOTO))))
            .andExpect(jsonPath("$.[*].dateCreation").value(hasItem(DEFAULT_DATE_CREATION.toString())));

        // Check, that the count call also returns 1
        restClientMockMvc.perform(get("/api/clients/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultClientShouldNotBeFound(String filter) throws Exception {
        restClientMockMvc.perform(get("/api/clients?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restClientMockMvc.perform(get("/api/clients/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingClient() throws Exception {
        // Get the client
        restClientMockMvc.perform(get("/api/clients/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateClient() throws Exception {
        // Initialize the database
        clientService.save(client);

        int databaseSizeBeforeUpdate = clientRepository.findAll().size();

        // Update the client
        Client updatedClient = clientRepository.findById(client.getId()).get();
        // Disconnect from session so that the updates on updatedClient are not directly saved in db
        em.detach(updatedClient);
        updatedClient
            .nom(UPDATED_NOM)
            .telephone(UPDATED_TELEPHONE)
            .solde(UPDATED_SOLDE)
            .isActif(UPDATED_IS_ACTIF)
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
            .dateCreation(UPDATED_DATE_CREATION);

        restClientMockMvc.perform(put("/api/clients")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedClient)))
            .andExpect(status().isOk());

        // Validate the Client in the database
        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeUpdate);
        Client testClient = clientList.get(clientList.size() - 1);
        assertThat(testClient.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testClient.getTelephone()).isEqualTo(UPDATED_TELEPHONE);
        assertThat(testClient.getSolde()).isEqualTo(UPDATED_SOLDE);
        assertThat(testClient.isIsActif()).isEqualTo(UPDATED_IS_ACTIF);
        assertThat(testClient.getPhoto()).isEqualTo(UPDATED_PHOTO);
        assertThat(testClient.getPhotoContentType()).isEqualTo(UPDATED_PHOTO_CONTENT_TYPE);
        assertThat(testClient.getDateCreation()).isEqualTo(UPDATED_DATE_CREATION);
    }

    @Test
    @Transactional
    public void updateNonExistingClient() throws Exception {
        int databaseSizeBeforeUpdate = clientRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientMockMvc.perform(put("/api/clients")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(client)))
            .andExpect(status().isBadRequest());

        // Validate the Client in the database
        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteClient() throws Exception {
        // Initialize the database
        clientService.save(client);

        int databaseSizeBeforeDelete = clientRepository.findAll().size();

        // Delete the client
        restClientMockMvc.perform(delete("/api/clients/{id}", client.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
