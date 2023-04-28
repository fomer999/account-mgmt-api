package com.lf.model;

import com.microsoft.azure.spring.data.cosmosdb.core.mapping.Document;
import com.microsoft.azure.spring.data.cosmosdb.core.mapping.PartitionKey;

import org.springframework.data.annotation.Id;

import com.lf.util.PatternMatcher;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

@JsonInclude(Include.NON_NULL)
public class Account {

    @Id
    @PartitionKey
    private String id;

    private String createdBy;
    private Date createdDate;
    private Boolean isActive;
    private String description;

    @Size(min=1)
    @Pattern(regexp = PatternMatcher.UUID_PATTERN, message = "UUIDFormatError")
    private String parentAccountId;

    @NotNull(message = "name")
    @Size(min = 1)
    private String name;

    @JsonProperty("id")
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("parent_account_id")
    public String getParentAccountId() {
        return parentAccountId;
    }
    public void setParentAccountId(String parentAccountId) {
        this.parentAccountId = parentAccountId;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
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

    @JsonProperty("is_active")
    public Boolean getIsActive() {
        return isActive;
    }
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Account withId(String id) {
        setId(id);
        return this;
    }

    public Account withParentAccountId(String parentAccountId) {
        setParentAccountId(parentAccountId);
        return this;
    }

    public Account withDescription(String description) {
        setDescription(description);
        return this;
    }

    public Account() {}

    public Account(final String name, final String parentAccountId, final String description, final String createdBy, final Date createdDate) {
        this.name = name;
        this.parentAccountId = parentAccountId;
        this.description = description;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
    }
    
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .add("parent Account Id", parentAccountId)
                .add("description", description)
                .add("Created By", createdBy)
                .add("Created Date", createdDate)
                .add("Active", isActive)
                .toString();
    }
}
