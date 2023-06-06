package xyz.ebidding.bid.intg;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import xyz.ebidding.bid.config.ModelMapperConfig;
import xyz.ebidding.bid.dto.*;
import xyz.ebidding.bid.model.Bid;
import xyz.ebidding.bid.repo.BidRepo;
import xyz.ebidding.bid.service.BidService;
import xyz.ebidding.bwic.client.BwicClient;
import xyz.ebidding.common.model.dto.BidDto;
import xyz.ebidding.common.model.dto.GenericResponse;
import xyz.ebidding.common.model.dto.PageableDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static xyz.ebidding.common.model.TYPES.BID_RESPONSE;
import static xyz.ebidding.common.model.TYPES.PAGEABLE_BID_RESPONSE;

@WebMvcTest(xyz.ebidding.bid.controller.BidController.class)
@RunWith(SpringRunner.class)
@AutoConfigureDataJpa
@Import({ModelMapperConfig.class, BidService.class})
public class BidControllerIntg {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    BidRepo bidRepo;

    @MockBean
    BwicClient bwicClient;

    ObjectMapper OM = new ObjectMapper().registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Test
    public void testCreateBid() throws Exception {
        when(bwicClient.getBwic("bond-id-test-create")).thenReturn(new GenericResponse<>());
        CreateBidRequest createBidRequest = CreateBidRequest.builder()
                .bwicId("bond-id-test-create").size(100.0).price(101.0)
                .build();
        String res = mockMvc.perform(post("/v1/bid/create")
                .contentType("application/json").content(OM.writeValueAsString(createBidRequest))
        ).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        GenericResponse<BidDto> bidResponse = OM.readValue(res, BID_RESPONSE);
        BidDto bid = bidResponse.getData();
        assertNotNull(bid.getId());
        assertNull(bid.getClientId());
        assertEquals(1, bid.getVersion().longValue());
        assertEquals(1, bid.getRank().longValue());
        assertEquals(100.0, bid.getSize());
        assertEquals(101.0, bid.getPrice());
    }

//    @Test
    public void testRank() throws Exception {
        when(bwicClient.getBwic("bond-id-test-rank"))
                .thenReturn(new GenericResponse<>());
        CreateBidRequest bidRequest1 = CreateBidRequest.builder()
                .bwicId("bond-id-test-rank").size(100.0).price(101.0)
                .build();
        CreateBidRequest bidRequest2 = CreateBidRequest.builder()
                .bwicId("bond-id-test-rank").size(100.0).price(102.0)
                .build();

        List<String> bidIds = new ArrayList<>();
        for (CreateBidRequest bidRequest: Arrays.asList(bidRequest1, bidRequest2)) {
            String res = mockMvc.perform(post("/v1/bid/create")
                    .contentType("application/json").content(OM.writeValueAsString(bidRequest))
            ).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
            GenericResponse<BidDto> bidResponse = OM.readValue(res, BID_RESPONSE);
            bidIds.add((bidResponse.getData()).getId());
        }

        Bid bid1 = bidRepo.findById(bidIds.get(0)).orElse(Bid.builder().build());
        assertEquals("clientA", bid1.getClientId());
        assertEquals(2L, bid1.getRank().longValue());

        Bid bid2 = bidRepo.findById(bidIds.get(1)).orElse(Bid.builder().build());
        assertEquals("clientB", bid2.getClientId());
        assertEquals(1L, bid2.getRank().longValue());
    }

//    @Test
    public void testDeleteBid() throws Exception {
        when(bwicClient.getBwic("bond-id-test-delete"))
                .thenReturn(new GenericResponse<>());
        CreateBidRequest bidRequest1 = CreateBidRequest.builder()
                .bwicId("bond-id-test-delete").size(100.0).price(101.0)
                .build();
        CreateBidRequest bidRequest2 = CreateBidRequest.builder()
                .bwicId("bond-id-test-delete").size(100.0).price(102.0)
                .build();

        List<String> bidIds = new ArrayList<>();
        for (CreateBidRequest bidRequest: Arrays.asList(bidRequest1, bidRequest2)) {
            String res = mockMvc.perform(post("/v1/bid/create")
                    .contentType("application/json").content(OM.writeValueAsString(bidRequest))
            ).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
            GenericResponse<BidDto> bidDtoGenericResponse = OM.readValue(res, BID_RESPONSE);
            bidIds.add(bidDtoGenericResponse.getData().getId());
        }

        mockMvc.perform(post("/v1/bid/delete/" + bidIds.get(1)))
                .andExpect(status().isOk());

        Bid bid1 = bidRepo.findById(bidIds.get(0)).orElse(Bid.builder().build());
        assertEquals("clientA", bid1.getClientId());
        assertEquals(1L, bid1.getRank().longValue());

        assertFalse(bidRepo.findById(bidIds.get(1)).isPresent());
    }

//    @Test
    public void testUpdate() throws Exception {
        when(bwicClient.getBwic("bond-id-test-update"))
                .thenReturn(new GenericResponse<>());
        CreateBidRequest bidRequest1 = CreateBidRequest.builder()
                .bwicId("bond-id-test-update").size(100.0).price(101.0)
                .build();
        CreateBidRequest bidRequest2 = CreateBidRequest.builder()
                .bwicId("bond-id-test-update").size(100.0).price(102.0)
                .build();

        List<String> bidIds = new ArrayList<>();
        for (CreateBidRequest bidRequest: Arrays.asList(bidRequest1, bidRequest2)) {
            String res = mockMvc.perform(post("/v1/bid/create")
                    .contentType("application/json").content(OM.writeValueAsString(bidRequest))
            ).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
            GenericResponse<BidDto> bidResponse = OM.readValue(res, BID_RESPONSE);
            bidIds.add(bidResponse.getData().getId());
        }

        mockMvc.perform(post("/v1/bid/update")
                .contentType("application/json")
                .content(OM.writeValueAsString(UpdateBidRequest.builder().id(bidIds.get(0)).price(103.0).build()))
        ).andExpect(status().isOk());

        Bid bid1 = bidRepo.findById(bidIds.get(0)).orElse(Bid.builder().build());
        assertEquals("clientA", bid1.getClientId());
        assertEquals(1L, bid1.getRank().longValue());

        Bid bid2 = bidRepo.findById(bidIds.get(1)).orElse(Bid.builder().build());
        assertEquals("clientB", bid2.getClientId());
        assertEquals(2L, bid2.getRank().longValue());
    }

//    @Test
    public void listBids() throws Exception {
        when(bwicClient.getBwic("bond-id-test-list"))
                .thenReturn(new GenericResponse<>());
        CreateBidRequest bidRequest1 = CreateBidRequest.builder()
                .bwicId("bond-id-test-list").size(100.0).price(101.0)
                .build();
        CreateBidRequest bidRequest2 = CreateBidRequest.builder()
                .bwicId("bond-id-test-list").size(100.0).price(102.0)
                .build();
        CreateBidRequest bidRequest3 = CreateBidRequest.builder()
                .bwicId("bond-id-test-list").size(100.0).price(102.0)
                .build();

        for (CreateBidRequest bidRequest: Arrays.asList(bidRequest1, bidRequest2, bidRequest3)) {
            String res = mockMvc.perform(post("/v1/bid/create")
                    .contentType("application/json").content(OM.writeValueAsString(bidRequest))
            ).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
            GenericResponse<?> bidResponse = OM.readValue(res, GenericResponse.class);
        }

        String res = mockMvc.perform(get("/v1/bid/list/bond-id-test-list")
                .param("limit", "2").param("offset", "1"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        GenericResponse<PageableDto<BidDto>> listBidResponse = OM.readValue(res, PAGEABLE_BID_RESPONSE);

        PageableDto<BidDto> bidList = listBidResponse.getData();
        assertEquals(3, bidList.getTotalElements().longValue());
        assertEquals(2, bidList.getTotalPages().longValue());
        assertEquals(2, bidList.getLimit().longValue());
        assertEquals(1, bidList.getOffset().longValue());
        assertEquals(1, bidList.getRows().size());
    }
}


