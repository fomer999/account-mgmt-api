package com.lf.model;

import com.microsoft.azure.spring.data.cosmosdb.core.mapping.PartitionKey;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Configuration {

    @Id
    @PartitionKey
    private String id;
    private String accountId;   // Every configuration is linked to an Account
    private String name;
    private Boolean configurationEnabled = true;
    private List<String> configurationProperties = new ArrayList<String>();

    // Book-keeping attributes
    private String createdBy;
    private Date createdDate;
    private String lastModifiedBy;
    private Date lastModifiedDate;

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
    public void setAccountId(String accountId) { this.accountId = accountId;  }

    @JsonProperty("configuration_enabled")
    public Boolean getConfigurationEnabled() {
        return configurationEnabled;
    }
    public void setConfigurationEnabled(Boolean configurationEnabled) {
        this.configurationEnabled = configurationEnabled;
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

    @JsonProperty("last_modified_by")
    public String getModifiedBy() {
        return lastModifiedBy;
    }
    public void setModifiedBy(String modifiedBy) {
        this.lastModifiedBy = modifiedBy;
    }

    @JsonProperty("last_modified_date")
    public Date getModifiedDate() {
        return lastModifiedDate;
    }
    public void setModifiedDate(Date modifiedDate) {
        this.lastModifiedDate = modifiedDate;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }
    public void setName(String humanReadableConfigName) {
        this.name = humanReadableConfigName;
    }

    @JsonProperty("configuration_properties")
    public List<String> getConfigurationProperties() { return configurationProperties;}
    public void setConfigurationProperties(List<String> configurationProperties) { this.configurationProperties = configurationProperties;}

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public Configuration withId(String id) {
        setId(id);
        return this;
    }

    public Configuration withName(String name) {
        setName(name);
        return this;
    }

    public Configuration() {}
}
