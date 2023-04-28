package com.lf.service;

import com.lf.model.Configuration;
import com.lf.repository.AccountRepository;
import com.lf.repository.ConfigurationRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsEmptyCollection.emptyCollectionOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ConfigurationServiceTest {

  private static final String ACCOUNT_ID = "testAccountId";
  private static final String CONFIG_NAME = "configNameId";
  private static final String ACCOUNT_NAME = "testAccountId";
  private static final String CONFIGURATION_ID = "testConfigurationId";
  private static final String CONFIGURATION_AUDIT_ENTRY_ID = "testConfigurationAuditEntryId";
  private static final List<String> ACCOUNT_ID_LIST = Arrays.asList(ACCOUNT_ID);
  private static final List<String> CONFIG_ID_LIST = Arrays.asList("testId", "testId1");
  private static final List<String> CONFIG_NAME_LIST = Arrays.asList("testConfigName", "testConfigName1");
  private static final List<String> ACCOUNT_NAME_LIST = Arrays.asList("testAccountName", "testAccountName1");
  private final String SERVER_CONFIGURABLE_ID = "testServerConfigurableDefault";

  @Mock
  private ConfigurationRepository repository;

  @Mock
  private AccountRepository accountRepository;

  @InjectMocks
  private ConfigurationServiceImpl service;

  @Test
  public void listShouldReturnEmptyListWhenNoUsersFound() throws Exception {
    when(repository.findAll()).thenReturn(emptyList());
    Optional<List<Configuration>> result = service.list(ACCOUNT_ID);
    assertThat(result.get(), is(emptyCollectionOf(Configuration.class)));
  }

  @Test
  public void listShouldReturnAllConfigurationsForAccount() throws Exception {
    Configuration configuration1 = new Configuration().withId("testConfigurationId1");
    Configuration configuration2 = new Configuration().withId("testConfigurationId2");
    when(repository.findByAccountId(ACCOUNT_ID)).thenReturn(asList(configuration1, configuration2));
    Optional<List<Configuration>> result = service.list(ACCOUNT_ID);
    assertThat(result.get(), containsInAnyOrder(configuration1, configuration2));
  }

  @Test
  public void readShouldReturnEmptyOptionalWhenNoConfigurationFound() throws Exception {
    when(repository.findById(CONFIGURATION_ID)).thenReturn(Optional.empty());
    Optional<Configuration> result = service.getConfigurationById(CONFIGURATION_ID);
    assertThat(result, is(Optional.empty()));
  }

  @Test
  public void readShouldReturnResultWhenConfigurationFound() throws Exception {
    Configuration configuration = new Configuration().withId(CONFIGURATION_ID);
    when(repository.findById(CONFIGURATION_ID)).thenReturn(Optional.of(configuration));
    Configuration result = service.getConfigurationById(CONFIGURATION_ID).get();
    assertThat(result, is(equalTo(configuration)));
  }

  @Test
  public void saveShouldReturnNewConfigurationWhenConfigurationDoesNotExist() throws Exception {
    Configuration newConfiguration = new Configuration().withId(CONFIGURATION_ID);
    Configuration newConfigurationWithName = newConfiguration;
    newConfiguration.setName(CONFIGURATION_ID);

    when(repository.findById(CONFIGURATION_ID)).thenReturn(Optional.empty());
    when(repository.findById(CONFIGURATION_ID)).thenReturn(Optional.of(newConfiguration));
    Configuration result = service.save(ACCOUNT_ID, newConfiguration).get();
    assertThat(result, is(equalTo(newConfigurationWithName)));
    verify(repository, atLeastOnce()).save(newConfiguration);
  }

  @Test
  public void updateShouldReturnEmptyOptionalWhenConfigurationNotFound() throws Exception {
    Configuration newConfigurationData = new Configuration().withId(CONFIGURATION_ID).withName("new-name");
    when(repository.findById(CONFIGURATION_ID)).thenReturn(Optional.empty());
    Optional<Configuration> result = service.update(ACCOUNT_ID, CONFIGURATION_ID, newConfigurationData);
    assertThat(result, is(Optional.empty()));
    verify(repository, never()).save(newConfigurationData);
  }

  @Test
  public void updateShouldAddNewEntryAndUpdateVersionWhenConfigurationExists() throws Exception {
    Configuration oldConfigurationData = new Configuration().withId(CONFIGURATION_ID).withName("name");
    oldConfigurationData.setName(CONFIGURATION_ID);

    Configuration newConfigurationData = new Configuration().withId(CONFIGURATION_ID).withName("new-name");
    newConfigurationData.setName(CONFIGURATION_ID);
    when(repository.findByName(CONFIGURATION_ID)).thenReturn(Optional.of(oldConfigurationData));
    Configuration result = service.update(ACCOUNT_ID, CONFIGURATION_ID, newConfigurationData).get();
    assertThat(result.getName(), is(equalTo(newConfigurationData.getName())));
  }

  @Test
  public void deleteShouldReturnEmptyOptionalWhenConfigurationNotFound() throws Exception {
    Configuration configuration = new Configuration().withId(CONFIGURATION_ID);
    when(repository.findById(CONFIGURATION_ID)).thenReturn(Optional.empty());
    Optional<Configuration> result = service.delete(CONFIGURATION_ID, false);
    assertThat(result, is(Optional.empty()));
    verify(repository, never()).save(configuration);
  }

  @Test
  public void deleteShouldSetActiveFlagToFalseWhenConfigurationFound() throws Exception {
    Configuration configuration = new Configuration().withId(CONFIGURATION_ID);
    when(repository.findById(CONFIGURATION_ID)).thenReturn(Optional.of(configuration));
    Configuration result = service.delete(CONFIGURATION_ID, false).get();
    assertFalse(result.getConfigurationEnabled());
  }
}