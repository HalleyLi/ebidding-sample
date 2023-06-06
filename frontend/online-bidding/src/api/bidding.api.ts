import { ListResponseData } from "@/models";
import { BidCancelParams, BiddingItem, BidSubmitParams } from "@/models/bidding/bidding";
import { request } from "./request";

export const listParams = {
    limit: 10000,
    offset: 0
}

/** Get All Bidding */
export const apiGetAllBidding = (data: any) => request<ListResponseData<BiddingItem>>('get', '/api/v1/bid/list', data);

/** Get Bidding By Bwic Id */
export const apiGetBiddingBwicId = (data: any) => request<BiddingItem>('get', '/api/v1/bid/client-bid', data);

/** New Bid */
export const apiSubmitBidding = (data: Partial<BidSubmitParams>) => request<BiddingItem>('post', '/api/v1/bid/create', data);

/** Edit Bid */
export const apiUpdateBidding = (data: Partial<BidSubmitParams>) => request<BiddingItem>('post', '/api/v1/bid/update', data);

/** Cancel Bid */
export const apiCancelBidding = (data: BidCancelParams) => request<BiddingItem>('post', '/api/v1/bid/delete/' + data?.id, data);