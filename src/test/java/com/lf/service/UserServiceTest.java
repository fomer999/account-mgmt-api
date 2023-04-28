package com.lf.service;

import com.lf.model.User;
import com.lf.model.UserRole;
import com.lf.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.ArrayList;
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
public class UserServiceTest {

  private static final String ACCOUNT_ID = "testAccountId";
  private static final String USER_ID = "testUserId";
  private static final String USERNAME = "testUsername";

  @Mock
  private UserRepository repository;

  @InjectMocks
  private UserServiceImpl service;

  @Test
  public void listShouldReturnEmptyListWhenNoUsersFound() throws Exception {

    when(repository.findAll()).thenReturn(emptyList());
    List<User> result = service.list(ACCOUNT_ID);
    assertThat(result, is(emptyCollectionOf(User.class)));
  }

  @Test
  public void listShouldReturnAllUsersForAccount() throws Exception {

    User user1 = new User().withId("testUserId1");
    User user2 = new User().withId("testUserId2");
    when(repository.findByAccountId(ACCOUNT_ID)).thenReturn(asList(user1, user2));
    List<User> result = service.list(ACCOUNT_ID);
    assertThat(result, containsInAnyOrder(user1, user2));
  }

  @Test
  public void readShouldReturnEmptyOptionalWhenNoUserFound() throws Exception {

    when(repository.findById("testUserId")).thenReturn(Optional.empty());
    Optional<User> result = service.getUserById(USER_ID);
    assertThat(result, is(Optional.empty()));
  }

  @Test
  public void readShouldReturnResultWhenUserFound() throws Exception {

    User user = new User().withId("testUserId");
    when(repository.findById("testUserId")).thenReturn(Optional.of(user));
    User result = service.getUserById(USER_ID).get();
    assertThat(result, is(equalTo(user)));
  }

  @Test
  public void readShouldReturnUserRolesWhenUserIsPresent() throws Exception {

    List<UserRole> userRoles = new ArrayList<>();
    userRoles.add(UserRole.ROLE_RETRIEVE_ROLES);
    User user = new User();
    user.setUserRoles(userRoles);
    user.withId("testUserId");

    when(repository.findById("testUserId")).thenReturn(Optional.of(user));
    List<UserRole> result = service.getUserRolesById(USER_ID).get();
    assertThat(result, is(equalTo(userRoles)));
  }

  @Test
  public void saveShouldReturnNewUserWhenUserDoesNotExist() throws Exception {

    User newUser = new User().withId(USER_ID);
    when(repository.findById(USER_ID)).thenReturn(Optional.empty());
    User result = service.save(ACCOUNT_ID, newUser).get();
    assertThat(result, is(equalTo(newUser)));
    verify(repository).save(newUser);
  }

  @Test
  public void updateShouldReturnEmptyOptionalWhenUserNotFound() throws Exception {

    User newUserData = new User().withId(USER_ID).withName("testName");
    when(repository.findById(USER_ID)).thenReturn(Optional.empty());
    Optional<User> result = service.update(ACCOUNT_ID, USER_ID, newUserData);
    assertThat(result, is(Optional.empty()));
    verify(repository, never()).save(newUserData);
  }

  @Test
  public void updateShouldOverwriteExistingDataAndReturnNewDataWhenUserExists() throws Exception {

    User oldUserData = new User().withId(USER_ID).withUsername(USERNAME).withName("testName");
    User newUserData = new User().withId(USER_ID).withUsername(USERNAME).withName("updatedTestName");
    when(repository.findById(USER_ID)).thenReturn(Optional.of(oldUserData));
    User result = service.update(ACCOUNT_ID, USER_ID, newUserData).get();
    assertThat(result.getName(), is(equalTo(newUserData.getName())));
  }

  @Test
  public void updateShouldOverwriteExistingDataAndReturnNewDataWhenUserExistsWithUsernameChange() throws Exception {

    User oldUserData = new User().withId(USER_ID).withUsername(USERNAME).withName("testName");
    User newUserData = new User().withId(USER_ID).withUsername("updatedUsername").withName("updatedTestName");
    when(repository.findByUsername("updatedUsername")).thenReturn(null);
    when(repository.findById(USER_ID)).thenReturn(Optional.of(oldUserData));
    User result = service.update(ACCOUNT_ID, USER_ID, newUserData).get();
    assertThat(result.getName(), is(equalTo(newUserData.getName())));
  }

  @Test
  public void updateShouldReturnEmptyOptionalWhenUserWithUpdatedUsernameAlreadyExists() throws Exception {

    User oldUserData = new User().withId(USER_ID).withUsername(USERNAME).withName("testName");
    User newUserData = new User().withId(USER_ID).withUsername("updatedUsername").withName("updatedTestName");
    List<User> userList = new ArrayList<>();
    userList.add(new User().withId("existingUserId").withUsername("updatedUsername"));
    when(repository.findByUsername("updatedUsername")).thenReturn(userList);
    when(repository.findById(USER_ID)).thenReturn(Optional.of(oldUserData));
    Optional<User> result = service.update(ACCOUNT_ID, USER_ID, newUserData);
    assertThat(result, is(Optional.empty()));
    verify(repository, never()).save(newUserData);
  }

  @Test
  public void updateWithOverwriteShouldReturnEmptyOptionalWhenUserWithUpdatedUsernameAlreadyExists() throws Exception {

    User oldUserData = new User().withId(USER_ID).withUsername(USERNAME).withName("testName");
    User newUserData = new User().withId(USER_ID).withUsername("updatedUsername").withName("updatedTestName");
    List<User> userList = new ArrayList<>();
    userList.add(new User().withId("existingUserId").withUsername("updatedUsername"));
    when(repository.findByUsername("updatedUsername")).thenReturn(userList);
    when(repository.findById(USER_ID)).thenReturn(Optional.of(oldUserData));
    Optional<User> result = service.updateWithOverwrite(ACCOUNT_ID, USER_ID, newUserData);
    assertThat(result, is(Optional.empty()));
    verify(repository, never()).save(newUserData);
  }

  @Test
  public void deleteShouldReturnEmptyOptionalWhenUserNotFound() throws Exception {

    User newUserData = new User().withId(USER_ID).withName("testName");
    when(repository.findById(USER_ID)).thenReturn(Optional.empty());
    Optional<User> result = service.delete(ACCOUNT_ID, USER_ID);
    assertThat(result, is(Optional.empty()));
    verify(repository, never()).save(newUserData);
  }

  @Test
  public void deleteShouldReturnDeletedUserWhenUserFound() throws Exception {

    User userData = new User().withId(USER_ID).withName("testName");
    when(repository.findById(USER_ID)).thenReturn(Optional.of(userData));
    User result = service.delete(ACCOUNT_ID, USER_ID).get();
    assertFalse(result.getIsActive());
  }
}