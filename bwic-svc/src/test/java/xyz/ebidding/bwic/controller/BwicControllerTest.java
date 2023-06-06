package xyz.ebidding.bwic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import xyz.ebidding.bid.client.BidClient;
import xyz.ebidding.bwic.TestConfig;
import xyz.ebidding.bwic.dto.CreateBwicRequest;
import xyz.ebidding.bwic.model.BondReference;
import xyz.ebidding.bwic.model.Bwic;
import xyz.ebidding.bwic.service.BwicService;
import xyz.ebidding.common.error.GlobalExceptionTranslator;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BwicController.class)
@RunWith(SpringRunner.class)
@Import({TestConfig.class})
@ImportAutoConfiguration(GlobalExceptionTranslator.class)
public class BwicControllerTest extends TestCase {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BwicService bwicService;

    @MockBean
    BidClient bidClient;

    ObjectMapper OM = new ObjectMapper();


//    @Test
    public void create() throws Exception {
        when(bwicService.saveBwic(any())).thenAnswer(ans -> {
                    CreateBwicRequest createBwicRequest = ans.getArgument(0);
                    return Bwic.builder().id("id1")
                            .size(createBwicRequest.getSize())
                            .clientId(createBwicRequest.getClientId())
                            .dueDate(createBwicRequest.getDueDate())
                            .bondReference(BondReference.builder().cusip(createBwicRequest.getCusip())
                                    .issuer("issuer").build())
                            .version(1L).build();
                }
        );

        String createBondPayload = "{\n" +
                "                \"clientId\": \"client\",\n" +
                "                \"cusip\": \"cusip\",\n" +
                "                \"size\": 100.0,\n" +
                "                \"dueDate\": \"2022-04-17T00:00:00Z\"\n" +
                "            }";

        String expectedResponse = "{\n" +
                "  \"message\": null,\n" +
                "  \"code\": \"SUCCESS\",\n" +
                "  \"data\": {\n" +
                "    \"id\": \"id1\",\n" +
                "    \"clientId\": \"client\",\n" +
                "    \"cusip\": \"cusip\",\n" +
                "    \"size\": 100.0,\n" +
                "    \"startingPrice\": null,\n" +
                "    \"issuer\": \"issuer\",\n" +
                "    \"rating\": null,\n" +
                "    \"coupon\": null,\n" +
                "    \"isOverDue\": true,\n" +
                "    \"dueDate\": \"2022-04-17T00:00:00Z\",\n" +
                "    \"version\": 1\n" +
                "  },\n" +
                "  \"success\": true\n" +
                "}";
        mockMvc.perform(post("/v1/bwic/create").content(createBondPayload).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse.replace("\n", "").replace(" ", "")))
        ;
    }


    @Test
    public void validateCreateRequest() throws Exception {
        Map<String, Object> inputPayload = new HashMap<>();
        inputPayload.put("clientId", "clientId");
        inputPayload.put("cusip", "cusip");
        inputPayload.put("size", 100.0);
        inputPayload.put("dueDate", "2022-04-17T00:00:00Z");

        Map<String, Object> clientEmpty = new HashMap<>(inputPayload);
        clientEmpty.put("clientId", "");

        Map<String, Object> cusipEmpty = new HashMap<>(inputPayload);
        cusipEmpty.put("cusip", "");

        Map<String, Object> negativeSize = new HashMap<>(inputPayload);
        negativeSize.put("size", -100.0);

        Map<String, Object> nullDueDate = new HashMap<>(inputPayload);
        nullDueDate.remove("dueDate");

        Map<String, Object> invalidDueDate = new HashMap<>(inputPayload);
        invalidDueDate.put("dueDate", "hi");

        for (Object[] payloadAndErrorCode : Arrays.asList(
                new Object[] {clientEmpty, "PARAM_VALID_ERROR"},
                new Object[] {cusipEmpty, "PARAM_VALID_ERROR"},
                new Object[] {negativeSize, "PARAM_VALID_ERROR"},
                new Object[] {nullDueDate, "PARAM_VALID_ERROR"},
                new Object[] {invalidDueDate, "MSG_NOT_READABLE"}
                )) {
            Object payload = payloadAndErrorCode[0];
            String content = OM.writeValueAsString(payload);
            String res = mockMvc.perform(post("/v1/bwic/create")
                    .content(content).contentType("application/json"))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            final Map<?, ?> resMap = OM.readValue(res, Map.class);
            assertEquals(false, resMap.get("success"));
            assertEquals(payloadAndErrorCode[1], resMap.get("code"));
        }
    }

    @Test
    public void validateDeleteRequest() throws Exception {
        Map<String, Object> emptyId = Collections.singletonMap("id", "");
        Map<String, Object> nullId = Collections.singletonMap("id", null);

        for (Map<String, Object> payload : Arrays.asList(emptyId, nullId)) {
            String content = OM.writeValueAsString(payload);
            assertEquals("{\"message\":\"id:must not be empty\",\"code\":\"PARAM_VALID_ERROR\",\"success\":false}",
                    mockMvc.perform(post("/v1/bwic/delete").content(content).contentType("application/json"))
                            .andExpect(status().isOk())
                            .andReturn().getResponse().getContentAsString()
            );
        }
    }

//    @Test
    public void delete() throws Exception {
        when(bwicService.deleteBwic("idToDelete")).thenAnswer(ans -> {
                    String idToDelete = ans.getArgument(0);
                    return Bwic.builder().id(idToDelete)
                            .active(false)
                            .build();
                }
        );

        String deleteBondRequest = "{\n" +
                "                \"id\": \"idToDelete\"" +
                "            }";

        String expectedResponse = "{\n" +
                "  \"message\": null,\n" +
                "  \"code\": \"SUCCESS\",\n" +
                "  \"data\": {\n" +
                "    \"id\": \"idToDelete\",\n" +
                "    \"clientId\": null,\n" +
                "    \"cusip\": null,\n" +
                "    \"size\": null,\n" +
                "    \"startingPrice\": null,\n" +
                "    \"issuer\": null,\n" +
                "    \"rating\": null,\n" +
                "    \"coupon\": null,\n" +
                "    \"isOverDue\": false,\n" +
                "    \"dueDate\": null,\n" +
                "    \"version\": null\n" +
                "  },\n" +
                "  \"success\": true\n" +
                "}";
        mockMvc.perform(post("/v1/bwic/delete").content(deleteBondRequest).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse.replace("\n", "").replace(" ", "")))
        ;
    }

    @Test
    public void ValidateListRequest() throws Exception {
        assertEquals("{\"message\":\"limit:must be greater than or equal to 1\",\"code\":\"PARAM_VALID_ERROR\",\"success\":false}",
                mockMvc.perform(get("/v1/bwic/list")
                .param("offset", "0")
                .param("limit", "0")
        ).andReturn().getResponse().getContentAsString());

        assertEquals("{\"message\":\"offset:must be greater than or equal to 0\",\"code\":\"PARAM_VALID_ERROR\",\"success\":false}",
                mockMvc.perform(get("/v1/bwic/list")
                .param("offset", "-1")
                .param("limit", "1")
        ).andReturn().getResponse().getContentAsString());
    }

//    @Test
    public void list() throws Exception {
        when(bwicService.all(PageRequest.of(0, 2))).thenReturn(
                new PageImpl<>(
                        Collections.singletonList(Bwic.builder().bondReference(BondReference.builder().build()).build()),
                        PageRequest.of(0, 2), 10L)
        );
        String expectedResponse = "{\"message\":null,\"code\":\"SUCCESS\"," +
                "\"data\":{\"rows\":" +
                "[" +
                "   {\"id\":null," +
                "   \"clientId\":null," +
                "   \"cusip\":null," +
                "   \"size\":null," +
                "    \"startingPrice\": null,\n" +
                "   \"issuer\":null," +
                "   \"rating\":null," +
                "   \"coupon\":null," +
                "    \"isOverDue\": false,\n" +
                "   \"dueDate\":null," +
                "   \"version\":null}" +
                "],\"limit\":2,\"offset\":0,\"totalElements\":10,\"totalPages\":5},\"success\":true}\n";
        mockMvc.perform(get("/v1/bwic/list?offset=0&limit=2"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .string(expectedResponse.replace("\n", "").replace(" ", "")));
    }

//    @Test
    public void testBond() throws Exception {
        when(bwicService.getBwic("id1")).thenReturn(Bwic.builder().clientId("client").build());
        String res = mockMvc.perform(get("/v1/bwic/id1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals("{\"message\":null,\"code\":\"SUCCESS\",\"data\":{\"id\":null,\"clientId\":\"client\"," +
                "\"cusip\":null,\"size\":null," +
                        "\"startingPrice\":null," +
                        "\"issuer\":null,\"rating\":null,\"coupon\":null,\"isOverDue\":false," +
                        "\"dueDate\":null,\"version\":null},\"success\":true}",
                res);
    }
}