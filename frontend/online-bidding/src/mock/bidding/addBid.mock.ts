import { intercepter, mock } from '../config';
import { BidSubmitParams } from '../../models/bidding/bidding';

mock.mock('/bidding-svc/bid/create', 'post', (config: any) => {
    const body: BidSubmitParams = JSON.parse(config?.body);
    console.log("submit Bidding", body)
    return intercepter({
        id: body.id,
        bwicId: body.bwicId,
        clientId: "123",
        effectiveTime: new Date(),
        feedback: "Test",
        price: body.price,
        rank: 1,
        size: 1000000,
        transactionId: "123",
        version: 1,
        bwic: {
            id: body.bwicId,
            cusip: "CUSIP_" + body.bwicId.toString(),
            issuer: "Issuer_" + body.bwicId.toString(),
            dueDate: new Date(),
            clientId: "Sales_" + body.bwicId.toString(),
            size: Math.round(Math.random() * 100) * 100000000,
            version: 1
        }

    });
});
