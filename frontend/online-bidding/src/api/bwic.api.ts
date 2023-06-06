import { ListResponseData } from "@/models";
import { BWICBidItem, BwicCancelParams, BWICItem, BwicSubmitParams } from "@/models/bwic/bwic";
import { request } from "./request";

export const listParams = {
    limit: 10000,
    offset: 0
}

/** Get All BWICs */
export const apiGetAllBwics = (data: any) => request<ListResponseData<BWICItem>>('get', '/api/v1/bwic/list', data);


/** Get All BWICs & top Bids */
export const apiGetAllBwicsBids = (data: any) => request<ListResponseData<BWICBidItem>>('get', '/api/v1/bwic/bwic-bid-details', data);

/** New/Edit BWIC */
export const apiSubmitBwic = (data: BwicSubmitParams) => request<BWICItem>('post', '/api/v1/bwic/create', data);

/** Cancel BWIC */
export const apiCancelBwic = (data: BwicCancelParams) => request<BWICItem>('post', '/api/v1/bwic/delete', data);