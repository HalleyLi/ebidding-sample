import { BwicPopularItem } from "@/models/bwic/bwic-popular";
import { request } from "./request";

export const listParams = {
    limit: 10000,
    offset: 0
}

/** Get All BWIC Populars */
export const apiGetAllBwicPopularList = (data: any) => request<BwicPopularItem[]>('get', '/api/v1/bwic/popular', data);

