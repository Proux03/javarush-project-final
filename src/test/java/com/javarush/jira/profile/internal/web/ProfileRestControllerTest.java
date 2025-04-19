package com.javarush.jira.profile.internal.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javarush.jira.AbstractControllerTest;
import com.javarush.jira.common.util.JsonUtil;
import com.javarush.jira.profile.ProfileTo;
import com.javarush.jira.profile.internal.ProfileMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.javarush.jira.login.internal.web.UserTestData.USER_MAIL;
import static com.javarush.jira.profile.internal.web.ProfileRestController.REST_URL;
import static com.javarush.jira.profile.internal.web.ProfileTestData.PROFILE_TO_MATCHER;
import static com.javarush.jira.profile.internal.web.ProfileTestData.USER_PROFILE_TO;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class ProfileRestControllerTest extends AbstractControllerTest {

    @Autowired
    protected ProfileMapper profileMapper;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getProfileSuccess() throws Exception {

        MvcResult result = perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        ProfileTo actualProfile = objectMapper.readValue(responseBody, ProfileTo.class);

        Assertions.assertEquals(actualProfile, USER_PROFILE_TO);

    }

    @Test
    void getProfileNonAuthorized() throws Exception {

        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isUnauthorized())
                .andDo(print());

    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void updateProfileAuthorized() throws Exception {

        perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(USER_PROFILE_TO)))
                .andExpect(status().isNoContent());

    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void updateProfileSuccess() throws Exception {
        ProfileTo updatedTo = ProfileTestData.getUpdatedTo();
        perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andDo(print())
                .andExpect(status().isNoContent());

        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(PROFILE_TO_MATCHER.contentJson(updatedTo));
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void updateProfileInvalid() throws Exception {

        perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(ProfileTestData.getInvalidTo())))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());

    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void updateProfileUnknownContact() throws Exception {

        perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(ProfileTestData.getWithUnknownContactTo())))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

}
