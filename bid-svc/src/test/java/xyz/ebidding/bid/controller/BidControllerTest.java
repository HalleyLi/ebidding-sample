package xyz.ebidding.bid.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import junit.framework.TestCase;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import xyz.ebidding.bid.TestConfig;
import xyz.ebidding.bid.dto.CreateBidRequest;
import xyz.ebidding.bid.dto.UpdateBidRequest;
import xyz.ebidding.bid.service.BidService;
import xyz.ebidding.bwic.client.BwicClient;
import xyz.ebidding.common.error.GlobalExceptionTranslator;
import xyz.ebidding.common.model.dto.GenericResponse;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(BidController.class)
@Import({TestConfig.class})
@ImportAutoConfiguration(GlobalExceptionTranslator.class)
@Ignore
public class BidControllerTest extends TestCase {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    BidService bidService;

    @MockBean
    BwicClient bwicClient;

    ObjectMapper OM = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new JavaTimeModule());

    @Test
    public void validateCreateBid() throws Exception {
        List<Object[]> inputPayloadAndErrorMsg = Arrays.asList(
                new Object[]{CreateBidRequest.builder()
                        .bwicId("bwic-1")
                        .price(100.0).size(10.0)
                        .build(), "Parameter Validation Error"},
                new Object[]{CreateBidRequest.builder()
                        .bwicId("bwic-2")
                        .price(-100.0).size(10.0).bwicId("bond")
                        .build(), "Parameter Validation Error"},
                new Object[]{CreateBidRequest.builder()
                        .bwicId("bwic-3")
                        .price(100.0).size(-10.0).bwicId("bond")
                        .build(), "Parameter Validation Error"},
                new Object[]{CreateBidRequest.builder()
                        .bwicId("bwic-4")
                        .price(100.0).bwicId("bond")
                        .build(), "Parameter Validation Error"},
                new Object[]{CreateBidRequest.builder()
                        .bwicId("bwic-5")
                        .price(100.0).bwicId("bond")
                        .build(), "Parameter Validation Error"},
                new Object[]{CreateBidRequest.builder()
                        .bwicId("bwic-6")
                        .price(100.0).size(10.0).bwicId("bond")
                        .build(), "Parameter Validation Error"}
        );
        for (Object[] payload : inputPayloadAndErrorMsg) {
            CreateBidRequest createBidRequest = (CreateBidRequest) payload[0];
            String res = mockMvc.perform(post("/v1/bid/create").contentType("application/json")
                    .content(OM.writeValueAsString(createBidRequest)))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            GenericResponse<?> bidResponse = OM.readValue(res, GenericResponse.class);
            assertFalse(bidResponse.isSuccess());
            assertEquals(payload[1], bidResponse.getCode().getMsg());
        }
    }

    @Test
    public void testDeleteBid() throws Exception {
        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);

        doNothing().when(bidService).deleteBid(idCaptor.capture(), eq("client"));

        mockMvc.perform(post("/v1/bid/delete/id1").contentType("application/json"))
                .andExpect(status().isOk());

        assertEquals("id1", idCaptor.getValue());
    }

    @Test
    public void validateUpdateBid() throws Exception {
        List<Object[]> inputPayloadAndError = Arrays.asList(
                new Object[]{UpdateBidRequest.builder().id("update-id").build(), "Parameter Validation Error"},
                new Object[]{UpdateBidRequest.builder().id("update-id").price(-0.1).build(), "Parameter Validation Error"},
                new Object[]{UpdateBidRequest.builder().id("").price(100.0).build(), "Parameter Validation Error"},
                new Object[]{UpdateBidRequest.builder().price(100.0).build(), "Parameter Validation Error"}
        );

        for (Object[] payload : inputPayloadAndError) {
            UpdateBidRequest updateBidRequest = (UpdateBidRequest) payload[0];
            String res = mockMvc.perform(post("/v1/bid/update").contentType("application/json")
                    .content(OM.writeValueAsString(updateBidRequest))
            ).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
            GenericResponse<?> bidResponse = OM.readValue(res, GenericResponse.class);
            assertFalse(bidResponse.isSuccess());
            assertEquals(payload[1], bidResponse.getCode().getMsg());
        }

    }

    @Test
    public void validateListBondBidResponse() throws Exception {
        assertEquals("{\"message\":\"limit:must be greater than or equal to 1\",\"code\":\"PARAM_VALID_ERROR\",\"success\":false}",
                mockMvc.perform(get("/v1/bid/list/123")
                        .param("offset", "0")
                        .param("limit", "0")
                ).andReturn().getResponse().getContentAsString());

        assertEquals("{\"message\":\"offset:must be greater than or equal to 0\",\"code\":\"PARAM_VALID_ERROR\",\"success\":false}",
                mockMvc.perform(get("/v1/bid/list/123")
                        .param("offset", "-1")
                        .param("limit", "1")
                ).andReturn().getResponse().getContentAsString());
    }

    @Test
    public void validateListBidResponse() throws Exception {
        assertEquals("{\"message\":\"limit:must be greater than or equal to 1\",\"code\":\"PARAM_VALID_ERROR\",\"success\":false}",
                mockMvc.perform(get("/v1/bid/list")
                        .param("offset", "0")
                        .param("limit", "0")
                ).andReturn().getResponse().getContentAsString());

        assertEquals("{\"message\":\"offset:must be greater than or equal to 0\",\"code\":\"PARAM_VALID_ERROR\",\"success\":false}",
                mockMvc.perform(get("/v1/bid/list")
                        .param("offset", "-1")
                        .param("limit", "1")
                ).andReturn().getResponse().getContentAsString());
    }
}