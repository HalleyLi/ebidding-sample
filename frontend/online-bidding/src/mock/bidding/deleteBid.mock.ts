import { intercepter, mock } from '../config';
import { BidCancelParams } from '../../models/bidding/bidding';

mock.mock(RegExp('/api/v1/bid/delete/*'), 'post', (config: any) => {
    const body: BidCancelParams = JSON.parse(config?.body);
    console.log("Cancel Bidding", body)
    return intercepter(null);
});
