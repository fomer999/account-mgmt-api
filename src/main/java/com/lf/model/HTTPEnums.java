package com.lf.model;

public class HTTPEnums {
    public enum verbs {
        PUT("PUT"),
        GET("GET"),
        POST("POST"),
        DELETE("DELETE");
        private final String value;

        private verbs(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }

    public enum GrantType {
        PASSWORD("password"),
        REFRESH_TOKEN("refresh_token");
        private final String value;

        private GrantType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }

    public enum BaseEndpointEnum {
        USERS("/v1/users"),
        OAUTH("/oauth/token"),
        WORKFLOW("/v1/workflow"),
        ACCOUNTS("/v1/accounts"),
        PULLREQUESTS("/v1/pullRequests"),
        NOTIFICATIONS("/v1/notifications"),
        CONFIGDEFAULTS("/v1/configDefaults"),
        CONFIGURATIONS("/v1/configurations"),
        INTERIMCONFIGURATIONS("/v1/interimConfigurations");
        private final String value;

        private BaseEndpointEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }

    public enum OAuthServerEndpoints {
        VALIDATE("/oauth/validate"),
        GETOAUTHTOKEN(BaseEndpointEnum.OAUTH + "?grant_type=password&username=[username]&password=[password]"),
        REFRESHOAUTHTOKEN(BaseEndpointEnum.OAUTH + "?grant_type=refresh_token&refresh_token=[token]");
        private final String value;

        private OAuthServerEndpoints(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static String replaceUsernameAndPassword(final String username, final String password, final String endpoint) {
            return endpoint.replace("[username]", username).replace("[password]", password);
        }

        public static String replaceToken(final String token, final String endpoint) {
            return endpoint.replace("[token]", token);
        }

        @Override
        public String toString() {
            return this.value;
        }
    }

    public enum AccountManagementEndpoints {
        HEALTH("/health"),
        GETSNSTOPICS("/v1/snsTopicNames"),
        GETUSERROLES(BaseEndpointEnum.ACCOUNTS + "/{account_id}/users/{user_id}/userRoles"),
        GETUSERBYID(BaseEndpointEnum.ACCOUNTS + "/{account_id}/users/{user_id}"),
        GETUSERLIST(BaseEndpointEnum.ACCOUNTS + "/{account_id}/users"),
        CREATEUSER(BaseEndpointEnum.ACCOUNTS + "/{account_id}/users"),
        UPDATEUSER(BaseEndpointEnum.ACCOUNTS + "/{account_id}/users/{user_id}"),
        DELETEUSER(BaseEndpointEnum.ACCOUNTS + "/{account_id}/users/{user_id}"),
        GETACCOUNTBYID(BaseEndpointEnum.ACCOUNTS + "/{account_id}"),
        GETACCOUNTLIST(BaseEndpointEnum.ACCOUNTS.toString()),
        CREATEACCOUNT(BaseEndpointEnum.ACCOUNTS.toString()),
        UPDATEACCOUNT(BaseEndpointEnum.ACCOUNTS + "/{account_id}"),
        DELETEACCOUNT(BaseEndpointEnum.ACCOUNTS + "/{account_id}"),
        CREATECONFIGURATION(BaseEndpointEnum.ACCOUNTS + "/{account_id}/configurations"),
        GETCONFIGURATIONLIST(BaseEndpointEnum.ACCOUNTS + "/{account_id}/configurations"),
        USERROLES(BaseEndpointEnum.ACCOUNTS + "/{account_id}/users/{user_id}/userRoles"),
        CREATESERVERCONFIGURABLEDEFAULT(BaseEndpointEnum.ACCOUNTS + "/{account_id}/configDefaults"),
        GETSERVERCONFIGURABLEDEFAULTSLIST(BaseEndpointEnum.ACCOUNTS + "/{account_id}/configDefaults"),
        CREATEINTERIMCONFIGURATION(BaseEndpointEnum.ACCOUNTS + "/{account_id}/interimConfigurations"),
        GETCONFIGURATION(BaseEndpointEnum.ACCOUNTS + "/{account_id}/configurations/{configuration_id}"),
        DELETESERVERCONFIGURABLEDEFAULT(BaseEndpointEnum.ACCOUNTS + "/{account_id}/configDefaults/{id}"),
        UPDATESERVERCONFIGURABLEDEFAULT(BaseEndpointEnum.ACCOUNTS + "/{account_id}/configDefaults/{id}"),
        GETSERVERCONFIGURABLEDEFAULTSID(BaseEndpointEnum.ACCOUNTS + "/{account_id}/configDefaults/{id}"),
        UPDATECONFIGURATION(BaseEndpointEnum.ACCOUNTS + "/{account_id}/configurations/{configuration_id}"),
        DELETECONFIGURATION(BaseEndpointEnum.ACCOUNTS + "/{account_id}/configurations/{configuration_id}"),
        SENDSNS(BaseEndpointEnum.ACCOUNTS + "/{account_id}/configurations/{configuration_id}/snsNotifications"),
        GETPULLREQUESTBYID(BaseEndpointEnum.ACCOUNTS + "/{account_id}/pullRequestNotification/{notification_id}"),
        DELETEPULLREQUESTNOTIFICATION(BaseEndpointEnum.ACCOUNTS + "/{account_id}/pullRequestNotification/{notification_id}"),
        UPDATEPULLREQUESTNOTIFICATION(BaseEndpointEnum.ACCOUNTS + "/{account_id}/pullRequestNotification/{notification_id}"),
        GETSERVERCONFIGURABLEDEFAULTSNAME(BaseEndpointEnum.ACCOUNTS + "/{account_id}/configDefaults/settingName/{setting_name}"),
        DELETEINTERIMCONFIGURATION(BaseEndpointEnum.ACCOUNTS + "/{account_id}/interimConfigurations/{interim_configuration_id}"),
        GETPULLREQUESTBYNAME(BaseEndpointEnum.ACCOUNTS + "/{account_id}/pullRequestNotification/configuration/{configuration_name}"),
        UPDATESERVERCONFIGURABLEDEAFULTSBYNAME(BaseEndpointEnum.ACCOUNTS + "/{account_id}/configDefaults/settingName/{setting_name}"),
        GETSERVERCONFIGURABLENODEFAULTNAME(BaseEndpointEnum.ACCOUNTS + "/{account_id}/configDefaults//noDefaultSettingName/{setting_name}"),
        CREATEPULLREQUESTNOTIFICATION(BaseEndpointEnum.ACCOUNTS + "/{account_id}/configurations/{configuration_id}/pullRequestNotification"),
        UPDATESERVERCONFIFURABLEDEFAULTVALUEONCONFGLIST(BaseEndpointEnum.ACCOUNTS + "/{account_id}/configDefaults/settingName/{setting_name}/settingValue/{setting_value}"),
        UPDATESERVERCONFIGURABLEDEAFULTVALUEFORSETTINGONCONFIG(BaseEndpointEnum.ACCOUNTS + "/{account_id}/configuration/{configuration_id}/configDefaults/settingName/{setting_name}/settingValue/{setting_value}");
        private final String value;

        private AccountManagementEndpoints(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static String replacePop(final String popName, final String endpoint) {
            return endpoint.replace("{pop_name}", popName);
        }

        public static String replaceUserId(final String userId, final String endpoint) {
            return endpoint.replace("{user_id}", userId);
        }

        public static String replaceNotificationId(String notificationId, String endpoint) {
            return endpoint.replace("{notification_id}", notificationId);
        }

        public static String replaceAccountId(final String accountId, final String endpoint) {
            return endpoint.replace("{account_id}", accountId);
        }

        public static String replaceSettingName(final String settingName, final String endpoint) {
            return endpoint.replace("{setting_name}", settingName);
        }

        public static String replaceSettingValue(final String settingValue, final String endpoint) {
            return endpoint.replace("{setting_value}", settingValue);
        }

        public static String replaceConfigurationId(final String configurationId, final String endpoint) {
            return endpoint.replace("{configuration_id}", configurationId);
        }

        public static String replaceConfigurationName(final String configurationName, final String endpoint) {
            return endpoint.replace("{configuration_name}", configurationName);
        }

        public static String replaceUsernameAndPassword(final String username, final String password, final String endpoint) {
            return endpoint.replace("[username]", username).replace("[password]", password);
        }

        public static String replaceAccountIdConfigurationId(final String accountId, final String configurationId, final String endpoint) {
            return endpoint.replace("{account_id}", accountId).replace("{configuration_id}", configurationId);
        }

        public static String replaceServerConfigurableDefaultById(final String serverConfigurableDefaultId, final String endpoint) {
            return endpoint.replace("{id}", serverConfigurableDefaultId);
        }

        @Override
        public String toString() {
            return this.value;
        }
    }

    public enum MacawEndpoints {
        HEALTH("/health"),
        DELETEPRNOTIFICATION(BaseEndpointEnum.NOTIFICATIONS + "/{environment}/accounts/{account_id}/users/{user_id}/name/{configuration_name}/notification"),
        CREATEPRNOTIFICATION(BaseEndpointEnum.NOTIFICATIONS + "/{environment}/accounts/{account_id}/users/{user_id}/interimConfiguration/{interim_configuration_id}/notification"),
        UPDATEPRNOTIFICATION(BaseEndpointEnum.NOTIFICATIONS + "/{environment}/accounts/{account_id}/users/{user_id}/interimConfiguration/{interim_configuration_id}/notification");
        private final String value;

        private MacawEndpoints(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }

    public enum MonitorPoPEndpoints {
        GETMONITORSINALERTSTATEFORPOP("/v1/monitorPop/{pop_name}");
        private final String value;

        private MonitorPoPEndpoints(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }

    public enum Headers {
        BEARER("Bearer"),
        AUTHORIZATION("Authorization"),
        CONNECTION("Connection"),
        CLOSE("close");
        private final String value;

        private Headers(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }

    public enum Status {
        OK(200, "OK"),
        NOT_FOUND(404, "Not Found"),
        BAD_REQUEST(400, "Bad Request"),
        UNAUTHORIZED(401, "Unauthorized"),
        METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
        INTERNAL_SERVER_ERROR(500, "Internal Server Error");
        private final int statusCode;
        private final String message;

        private Status(int statusCode, String message) {
            this.message = message;
            this.statusCode = statusCode;
        }

        public String getMessage() {
            return message;
        }

        public int getStatusCode() {
            return statusCode;
        }

        @Override
        public String toString() {
            return "Response [statusCode=" + statusCode + ", message=" + message + "]";
        }
    }

    public enum Response {
        STATUS("status"),
        MESSAGE("message");
        private final String value;

        private Response(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }

    /**
     * Standardize the X-Security-Context header.
     */
    public enum SecurityHeader {
        XDLVRSECURITYCONTEXT("X-Security-Context");
        private final String value;

        private SecurityHeader(String value) {
            this.value = value;
        }

        public final String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }

    /**
     * Standardized macaw responses.
     */
    public enum MacawResponse {
        EDIT("macaw edit"),
        DENY("macaw deny"),
        ACCEPT("macaw accept"),
        ACCEPTCONFIGURATION("macaw accept configuration"),
        DENYCONFIGURATION("macaw deny configuration"),
        DELETECONFIGURATION("macaw delete configuration");
        private final String value;

        private MacawResponse(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }

    /**
     * Used for determining whether or not a workflow was registered or deprecated.
     */
    public enum RegistrationOptions {
        REGISTERED("REGISTERED"),
        DEPRECATED("DEPRECATED");
        private final String value;

        private RegistrationOptions(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }

    /**
     * The various environments and workflow can interact with.
     */
    public enum Environments {
        STAGING("staging"),
        PRODUCTION("production");
        private final String value;

        private Environments(String value) { this.value = value; }

        public String getValue() { return value; }

        @Override
        public String toString() { return this.value; }
    }

    public enum WorkflowTypes {
        NEW("new"),
        EDIT("edit"),
        DELETE("delete");
        private final String value;

        private WorkflowTypes(String value) { this.value = value; }

        public String getValue() { return value; }

        @Override
        public String toString() { return this.value; }
    }

    /**
     * Workflow service endpoints
     */
    public enum WorkflowServiceEndpoints {
        CREATECONFIGURATIONWORKFLOW(BaseEndpointEnum.WORKFLOW + "/{environment}/account/{account_id}/user/{user_id}"),
        USERRESPONSEONMANUALMODE(BaseEndpointEnum.WORKFLOW + "/{environment}/type/{workflow_type}/approved/{user_input}"),
        UPDATECONFIGURATIONWORKFLOW(BaseEndpointEnum.WORKFLOW + "/{environment}/account/{account_id}/user/{user_id}/notification/{notification_id}"),
        DELETECONFIGURATIONWORKFLOW(BaseEndpointEnum.WORKFLOW + "/{environment}/account/{account_id}/user/{user_id}/configuration/{configuration_id}/notification/{notification_id}");
        private final String value;

        private WorkflowServiceEndpoints(String value) { this.value = value; }

        public String getValue() { return value; }

        @Override
        public String toString() { return this.value; }

        public static String replaceUserId(final String userId, final String endpoint) {
            return endpoint.replace("{user_id}", userId);
        }

        public static String replaceEnvironment(final String environment, final String endpoint) {
            return endpoint.replace("{environment}", environment);
        }

        public static String replaceType(final String type, final String endpoint) {
            return endpoint.replace("{workflow_type}", type);
        }

        public static String replaceUserInput(final String userInput, final String endpoint) {
            return endpoint.replace("{user_input}", userInput);
        }

        public static String replaceNotificationId(String notificationId, String endpoint) {
            return endpoint.replace("{notification_id}", notificationId);
        }

        public static String replaceAccountId(final String accountId, final String endpoint) {
            return endpoint.replace("{account_id}", accountId);
        }

        public static String replaceConfigurationId(final String configurationId, final String endpoint) {
            return endpoint.replace("{configuration_id}", configurationId);
        }

    }

    /**
     *  A set of helper methods to construct fully qualified endpoint urls -- used primarily in Macaw.
     */
    public static String createFullyQualifiedGetUserEndpoint(final String endpointBase, final String accountId, final String userId) {
        return AccountManagementEndpoints.replaceUserId(userId,
                AccountManagementEndpoints.replaceAccountId(accountId,
                                endpointBase + AccountManagementEndpoints.GETUSERBYID.getValue()));

    }

    public static String createFullyQualifiedGetConfigurationByIdEndpoint(final String endpointBase, final String accountId, final String configurationId) {
        return AccountManagementEndpoints.replaceAccountId(accountId,
                AccountManagementEndpoints.replaceConfigurationId(configurationId,
                        endpointBase + AccountManagementEndpoints.GETCONFIGURATION.getValue()));
    }

    public static String createFullyQualifiedUserRolesEndpoint(final String endpointBase, final String accountId, final String userId) {
        return AccountManagementEndpoints.replaceUserId(userId,
                AccountManagementEndpoints.replaceAccountId(accountId,
                        endpointBase + AccountManagementEndpoints.USERROLES.getValue()));
    }

    public static String createFullyQualifiedCreatePullRequestNotificationEndpoint(final String endpointBase, final String accountId, final String configurationId) {
        return AccountManagementEndpoints.replaceAccountId(accountId,
                        AccountManagementEndpoints.replaceConfigurationId(configurationId,
                                endpointBase + AccountManagementEndpoints.CREATEPULLREQUESTNOTIFICATION.getValue()));
    }

    public static String createFullyQualifiedUpdatePullRequestNotificationEndpoint(final String endpointBase, final String accountId, final String notificationId) {
        return AccountManagementEndpoints.replaceAccountId(accountId,
                AccountManagementEndpoints.replaceNotificationId(notificationId,
                                endpointBase + AccountManagementEndpoints.UPDATEPULLREQUESTNOTIFICATION.getValue()));
    }

    public static String createFullyQualifiedDeletePullRequestNotificationEndpoint(final String endpointBase, final String accountId, final String notificationId, boolean useName) {
        return AccountManagementEndpoints.replaceAccountId(accountId,
                AccountManagementEndpoints.replaceNotificationId(notificationId,
                                endpointBase + AccountManagementEndpoints.DELETEPULLREQUESTNOTIFICATION.getValue())) + ((useName) ? "?use-name=true" : "?use-name=false");
    }

    public static String createFullyQualifiedGetPullRequestNotificationByNameEndpoint(final String endpointBase, final String accountId, final String configurationName, final String notificationId) {
        return AccountManagementEndpoints.replaceAccountId(accountId,
                AccountManagementEndpoints.replaceNotificationId(notificationId,
                        AccountManagementEndpoints.replaceConfigurationName(configurationName,
                                endpointBase + AccountManagementEndpoints.GETPULLREQUESTBYNAME.getValue())));
    }

    public static String createFullyQualifiedGetPullRequestNotificationEndpoint(final String endpointBase, final String accountId, final String notificationId) {
        return AccountManagementEndpoints.replaceAccountId(accountId,
                AccountManagementEndpoints.replaceNotificationId(notificationId,
                        endpointBase + AccountManagementEndpoints.GETPULLREQUESTBYID.getValue()));
    }

    public static String createFullyQualifiedDeleteConfigurationEndpoint(final String endpointBase, final String accountId, final String configurationId, final boolean useName) {
        return AccountManagementEndpoints.replaceAccountId(accountId,
                AccountManagementEndpoints.replaceConfigurationId(configurationId,
                        endpointBase + AccountManagementEndpoints.DELETECONFIGURATION.getValue())) + ((useName) ? "?use-name=true" : "?use-name=false");
    }

    public static String createFullyQualifiedDeleteInterimConfigurationEndpoint(final String endpointBase, final String accountId, final String interimConfigurationId) {
        return AccountManagementEndpoints.replaceAccountId(accountId,
                AccountManagementEndpoints.replaceConfigurationId(interimConfigurationId,
                        endpointBase + AccountManagementEndpoints.DELETEINTERIMCONFIGURATION.getValue()));
    }

    public static String createFullyQualifiedCreateConfigurationWorkflowEndpoint(final String endpointBase, final String accountId, final String environment, final String userId) {
        return WorkflowServiceEndpoints.replaceAccountId(accountId,
                WorkflowServiceEndpoints.replaceEnvironment(environment,
                        WorkflowServiceEndpoints.replaceUserId(userId,
                                endpointBase + WorkflowServiceEndpoints.DELETECONFIGURATIONWORKFLOW.getValue())));
    }

    public static String createFullyQualifiedUpdateServerConfigurableDefaultValueForSetting(final String endpointBase, final String endpoint, final String accountId, final String settingName, final String settingValue) {
        return AccountManagementEndpoints.replaceSettingName(settingName,
                AccountManagementEndpoints.replaceSettingValue(settingValue,
                    AccountManagementEndpoints.replaceAccountId(accountId, endpointBase + endpoint)));
    }

    // TODO: A more flexible way for creating FQE used in ams-external refactor out above in Macaw
    public static String createFullyQualifiedAccountManagementEndpoint(final String endpointBase, final String endpoint, final String id) {
        return AccountManagementEndpoints.replaceAccountId(id, endpointBase + endpoint);
    }

    // Change back to individual endpoints
    public static String createFullyQualifiedAccountManagementEndpoint(final String endpointBase, final String endpoint, final String accountId, final String value) {
        if(endpoint.equals(AccountManagementEndpoints.GETSERVERCONFIGURABLEDEFAULTSID.getValue())) {
            return AccountManagementEndpoints.replaceServerConfigurableDefaultById(value,
                    AccountManagementEndpoints.replaceAccountId(accountId, endpointBase + endpoint));
        } else if (endpoint.equals(AccountManagementEndpoints.GETCONFIGURATION.getValue())) {
            return AccountManagementEndpoints.replaceConfigurationId(value,
                    AccountManagementEndpoints.replaceAccountId(accountId, endpointBase + endpoint));
        } else if (endpoint.equals(AccountManagementEndpoints.GETUSERROLES.getValue())) {
            return AccountManagementEndpoints.replaceUserId(value,
                    AccountManagementEndpoints.replaceAccountId(accountId, endpointBase + endpoint));
        } else if (endpoint.equals(AccountManagementEndpoints.GETSERVERCONFIGURABLEDEFAULTSNAME.getValue())) {
            return AccountManagementEndpoints.replaceSettingName(value,
                    AccountManagementEndpoints.replaceAccountId(accountId, endpointBase + endpoint));
        } else if (endpoint.equals(AccountManagementEndpoints.GETSERVERCONFIGURABLENODEFAULTNAME.getValue())) {
            return AccountManagementEndpoints.replaceSettingName(value,
                    AccountManagementEndpoints.replaceAccountId(accountId, endpointBase + endpoint));
        } else if (endpoint.equals(AccountManagementEndpoints.UPDATESERVERCONFIGURABLEDEAFULTSBYNAME.getValue())) {
            return AccountManagementEndpoints.replaceSettingName(value,
                    AccountManagementEndpoints.replaceAccountId(accountId, endpointBase + endpoint));
        } else if (endpoint.equals(AccountManagementEndpoints.CREATESERVERCONFIGURABLEDEFAULT.getValue())) {
            return AccountManagementEndpoints.replaceAccountId(accountId, endpointBase + endpoint);
        } else if (endpoint.equals(AccountManagementEndpoints.GETUSERLIST.getValue())) {
            return AccountManagementEndpoints.replaceAccountId(accountId, endpointBase + endpoint);
        } else if (endpoint.equals(AccountManagementEndpoints.GETUSERBYID.getValue())){
            return AccountManagementEndpoints.replaceUserId(value,
                    AccountManagementEndpoints.replaceAccountId(accountId, endpointBase + endpoint));
        } else if (endpoint.equals(AccountManagementEndpoints.CREATEUSER.getValue())) {
            return AccountManagementEndpoints.replaceAccountId(accountId, endpointBase + endpoint);
        } else if (endpoint.equals(AccountManagementEndpoints.UPDATEUSER.getValue())){
            return AccountManagementEndpoints.replaceUserId(value,
                    AccountManagementEndpoints.replaceAccountId(accountId, endpointBase + endpoint));
        } else if (endpoint.equals(AccountManagementEndpoints.DELETEUSER.getValue())){
            return AccountManagementEndpoints.replaceUserId(value,
                    AccountManagementEndpoints.replaceAccountId(accountId, endpointBase + endpoint));
        } else if (endpoint.equals(AccountManagementEndpoints.GETACCOUNTBYID.getValue())){
            return AccountManagementEndpoints.replaceAccountId(accountId, endpointBase + endpoint);
        } else if (endpoint.equals(AccountManagementEndpoints.UPDATEACCOUNT.getValue())){
            return AccountManagementEndpoints.replaceAccountId(accountId, endpointBase + endpoint);
        } else if (endpoint.equals(AccountManagementEndpoints.DELETEACCOUNT.getValue())){
            return AccountManagementEndpoints.replaceAccountId(accountId, endpointBase + endpoint);
        }
        return "";
    }

    public static String createFullyQualifiedAccountManagementEndpoint(final String endpointBase, final String endpoint, final String accountId, final String configurationId, final String settingName, final String settingValue) {
        return (endpoint.equals(AccountManagementEndpoints.UPDATESERVERCONFIGURABLEDEAFULTVALUEFORSETTINGONCONFIG.getValue())) ?
            AccountManagementEndpoints.replaceAccountId(accountId,
                    AccountManagementEndpoints.replaceConfigurationId(configurationId,
                        AccountManagementEndpoints.replaceSettingName(settingName,
                                AccountManagementEndpoints.replaceSettingValue(settingValue, endpointBase + endpoint)))) : "";
    }

    public static String createFullyQualifiedAccountManagementEndpoint(final String endpointBase, final String endpoint, final String accountId, final String settingName, final String settingValue) {
        if (endpoint.equals(AccountManagementEndpoints.UPDATESERVERCONFIGURABLEDEAFULTVALUEFORSETTINGONCONFIG.getValue())) {
            return AccountManagementEndpoints.replaceAccountId(accountId,
                    AccountManagementEndpoints.replaceSettingName(settingName,
                            AccountManagementEndpoints.replaceSettingValue(settingValue, endpointBase + endpoint)));
        } else if (endpoint.equals(AccountManagementEndpoints.UPDATESERVERCONFIFURABLEDEFAULTVALUEONCONFGLIST.getValue())) {
            return AccountManagementEndpoints.replaceAccountId(accountId,
                    AccountManagementEndpoints.replaceSettingName(settingName,
                            AccountManagementEndpoints.replaceSettingValue(settingValue, endpointBase + endpoint)));
        }
        return "";
    }

    public static String createFullyQualifiedWorkflowEndpoint(final String endpointBase, final String endpoint, final String accountId, final String environment, final String userId) {
        return WorkflowServiceEndpoints.replaceAccountId(accountId,
                WorkflowServiceEndpoints.replaceEnvironment(environment,
                        WorkflowServiceEndpoints.replaceUserId(userId,
                                endpointBase + endpoint)));
    }

    public static String createFullyQualifiedWorkflowEndpoint(final String endpointBase, final String endpoint, final String accountId, final String environment, final String userId, final String configurationId, final String notificationId) {
        return WorkflowServiceEndpoints.replaceAccountId(accountId,
                WorkflowServiceEndpoints.replaceEnvironment(environment,
                        WorkflowServiceEndpoints.replaceConfigurationId(configurationId,
                                WorkflowServiceEndpoints.replaceNotificationId(notificationId,
                                        WorkflowServiceEndpoints.replaceUserId(userId,
                                                endpointBase + endpoint)))));
    }

    public static String createFullyQualifiedWorkflowEndpoint(final String endpointBase, final String endpoint, final String accountId, final String environment, final String userId, final String notificationId) {
        return WorkflowServiceEndpoints.replaceAccountId(accountId,
                WorkflowServiceEndpoints.replaceEnvironment(environment,
                                WorkflowServiceEndpoints.replaceNotificationId(notificationId,
                                        WorkflowServiceEndpoints.replaceUserId(userId,
                                                endpointBase + endpoint))));
    }

    public static String createFullyQualifiedWorkflowEndpoint(final String endpointBase, final String endpoint, final String environment, final String workflowType, final Boolean userInput) {
        return WorkflowServiceEndpoints.replaceEnvironment(environment,
                WorkflowServiceEndpoints.replaceType(workflowType,
                        WorkflowServiceEndpoints.replaceUserInput(userInput.toString(),
                                endpointBase + endpoint)));
    }


}
