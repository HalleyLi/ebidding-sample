import { intercepter, mock } from '../config';
import { BidSubmitParams } from '../../models/bidding/bidding';

mock.mock('/bidding-svc/bid/update', 'post', (config: any) => {
    const body: BidSubmitParams = JSON.parse(config?.body);
    console.log("submit Bidding", body)
    return intercepter({
        id: body.id,
        bwicId: "1",
        clientId: "123",
        effectiveTime: new Date(),
        feedback: "Test",
        price: body.price,
        rank: 1,
        size: 1000000,
        transactionId: "123",
        version: 1,
        bwic: {
            id: "1",
            cusip: "CUSIP_1",
            issuer: "Issuer_1",
            dueDate: new Date(),
            clientId: "Sales_1",
            size: Math.round(Math.random() * 100) * 100000000,
            version: 1
        }
    });
});
