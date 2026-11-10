
package com.experimentlab.simulation;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.json.JsonMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SimulationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @Test
    void successfullyRunsSimulation() throws Exception {

        // Step 1: Create a demo session.
        MvcResult sessionResult = mockMvc.perform(
                        get("/api/demo/session")
                )
                .andExpect(status().isOk())
                .andReturn();

        Cookie sessionCookie = sessionResult.getResponse()
                .getCookie("DEMO_SESSION_ID");

        // Step 2: Create an experiment.
        MvcResult experimentResult = mockMvc.perform(
                        post("/api/experiments")
                                .cookie(sessionCookie)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "name": "Simulation Test",
                                "description": "Testing simulation generation",
                                "hypothesis": "The new button increases conversions",
                                "primaryMetric": "CONVERSION_RATE"
                            }
                            """)
                )
                .andExpect(status().isCreated())
                .andReturn();

        String experimentId = jsonMapper.readTree(
                experimentResult.getResponse().getContentAsString()
        ).get("id").asString();

        // Step 3: Create two variants with 50% traffic each.
        mockMvc.perform(
                        post("/api/experiments/" + experimentId + "/variants")
                                .cookie(sessionCookie)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "name": "Control",
                                "description": "Original experience",
                                "configuration": {
                                    "simulation": {
                                        "clickProbability": 0.40,
                                        "conversionProbability": 0.20
                                    }
                                },
                                "trafficPercentage": 50
                            }
                            """)
                )
                .andExpect(status().isCreated());

        mockMvc.perform(
                        post("/api/experiments/" + experimentId + "/variants")
                                .cookie(sessionCookie)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "name": "Treatment",
                                "description": "Improved experience",
                                "configuration": {
                                    "simulation": {
                                        "clickProbability": 0.60,
                                        "conversionProbability": 0.30
                                    }
                                },
                                "trafficPercentage": 50
                            }
                            """)
                )
                .andExpect(status().isCreated());

        // Step 4: Start the experiment.
        mockMvc.perform(
                        post("/api/experiments/" + experimentId + "/start")
                                .cookie(sessionCookie)
                )
                .andExpect(status().isOk());

        // Step 5: Launch a simulation with 1,000 users.
        mockMvc.perform(
                        post("/api/experiments/" + experimentId + "/simulations")
                                .cookie(sessionCookie)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "targetUsers": 1000
                            }
                            """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.targetUsers").value(1000))
                .andExpect(jsonPath("$.usersProcessed").value(1000))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.startedAt").isNotEmpty())
                .andExpect(jsonPath("$.completedAt").isNotEmpty());
    }
}

