package com.recipemanager.common;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.recipemanager.IntegrationTest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Import(GlobalExceptionHandlerTest.ErrorTestController.class)
class GlobalExceptionHandlerTest extends IntegrationTest {

    private static final String SECRET = "db password is hunter2";

    @Test
    @DisplayName("NFR Validation: invalid request DTO returns 400 ProblemDetail with field errors")
    void invalidBodyReturnsFieldErrors() throws Exception {
        mockMvc.perform(post("/test/errors/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.detail").value("One or more fields are invalid."))
                .andExpect(jsonPath("$.errors.length()").value(1))
                .andExpect(jsonPath("$.errors[0].field").value("title"))
                .andExpect(jsonPath("$.errors[0].message").value("must not be blank"));
    }

    @Test
    @DisplayName("NFR Validation: invalid request parameter returns 400 ProblemDetail with field errors")
    void invalidParamReturnsFieldErrors() throws Exception {
        mockMvc.perform(get("/test/errors/param").param("size", "100"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.errors[0].field").value("size"))
                .andExpect(jsonPath("$.errors[0].message").value("must be less than or equal to 48"));
    }

    @Test
    @DisplayName("NFR Security: malformed JSON returns 400 ProblemDetail")
    void malformedJsonReturns400() throws Exception {
        mockMvc.perform(post("/test/errors/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not json"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("NFR Security: unhandled exception returns 500 ProblemDetail without stack trace or message")
    void unhandledExceptionHidesInternals() throws Exception {
        mockMvc.perform(get("/test/errors/boom"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.title").value("Internal Server Error"))
                .andExpect(jsonPath("$.detail").value("An unexpected error occurred."))
                .andExpect(jsonPath("$.trace").doesNotExist())
                .andExpect(jsonPath("$.exception").doesNotExist())
                .andExpect(content().string(not(containsString(SECRET))))
                .andExpect(content().string(not(containsString("IllegalStateException"))))
                .andExpect(content().string(not(containsString("at com.recipemanager"))));
    }

    @Test
    @DisplayName("Unknown path returns 404 ProblemDetail")
    void unknownPathReturns404() throws Exception {
        mockMvc.perform(get("/api/v1/does-not-exist"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404));
    }

    record SampleRequest(@NotBlank @Size(max = 120) String title) {
    }

    @TestConfiguration
    @RestController
    @RequestMapping("/test/errors")
    static class ErrorTestController {

        @PostMapping("/validation")
        SampleRequest validation(@Valid @RequestBody SampleRequest request) {
            return request;
        }

        @GetMapping("/param")
        int param(@RequestParam @Max(48) int size) {
            return size;
        }

        @GetMapping("/boom")
        void boom() {
            throw new IllegalStateException(SECRET);
        }
    }
}
