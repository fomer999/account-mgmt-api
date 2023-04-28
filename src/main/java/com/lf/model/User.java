package com.lf.model;

import org.springframework.data.annotation.Id;
import com.microsoft.azure.spring.data.cosmosdb.core.mapping.Document;
import com.microsoft.azure.spring.data.cosmosdb.core.mapping.PartitionKey;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Document(collection =  "users")
public class User {
    @Id
    @PartitionKey
    private String id;
    private String accountId;   // Every User is linked to an Account

    private String createdBy;
    private Date createdDate;

    @NotNull(message = "name")
    private String name;

    @NotNull(message = "username")
    private String username;

    @NotNull(message = "password")
    private String password;

    private Boolean isActive;

    @NotNull(message = "user_roles")
    private List<UserRole> userRoles;

    @JsonProperty("id")
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("account_id")
    public String getAccountId() {
        return accountId;
    }
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("username")
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    @JsonProperty("password")
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    @JsonProperty("is_active")
    public Boolean getIsActive() {
        return isActive;
    }
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @JsonProperty("user_roles")
    public List<UserRole> getUserRoles() {
        return userRoles;
    }
    public void setUserRoles(List<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    @JsonProperty("created_by")
    public String getCreatedBy() {
        return createdBy;
    }
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @JsonProperty("created_date")
    public Date getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public User withId(String id) {
        setId(id);
        return this;
    }

    public User withName(String name) {
        setName(name);
        return this;
    }

    public User withUsername(String name) {
        setName(name);
        return this;
    }

    public User() {}

    public User(final String accountId, final String name, final String username, final String password, final Boolean isActive, final List<UserRole> userRoles, final String createdBy, final Date createdDate) {
        this.accountId = accountId;
        this.name = name;
        this.username = username;
        this.password = password;
        this.isActive = isActive;
        this.userRoles = userRoles;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("accountId", accountId)
                .add("name", name)
                .add("username", username)
                .add("password", password)
                .add("isActive", isActive)
                .add("userRoles", userRoles)
                .add("createdBy", createdBy)
                .add("createdDate", createdDate)
                .toString();
    }
}
