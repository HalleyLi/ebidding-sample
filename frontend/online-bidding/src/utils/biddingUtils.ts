import { BiddingItem } from "../models/bidding/bidding";
import { convertDate } from "./dateUtils";

export const formatBidding = (biddingList: BiddingItem[]) => {
    return biddingList.map(item => {
        return {
            ...item, ...{
                bondCusip: item.bwic?.cusip,
                effectiveTime: convertDate(item.effectiveTime)
            }
        }
    });
}