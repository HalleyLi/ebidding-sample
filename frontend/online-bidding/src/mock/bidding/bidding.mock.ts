import { intercepter, mock } from '../config';
import { BiddingItem } from '@/models/bidding/bidding';

mock.mock(RegExp('/api/v1/bid/list.*'), 'get', (config: any) => {
    let allBidding: BiddingItem[] = [];
    new Array(100).fill(undefined).forEach((item, index) => {
        allBidding.push({
            id: index.toString(),
            bwicId: (index % 10).toString(),
            clientId: "123",
            effectiveTime: new Date(),
            feedback: "Test",
            price: Math.round(Math.random() * 100),
            rank: (index % 20),
            size: Math.round(Math.random() * 100) * 1000000,
            transactionId: "123",
            version: index,
            bwic: {
                id: index.toString(),
                cusip: "CUSIP_" + (index % 3).toString(),
                issuer: "Issuer_" + index.toString(),
                dueDate: new Date(),
                clientId: "Sales_" + index.toString(),
                size: Math.round(Math.random() * 100) * 100000000,
                version: index % 3
            }
        });
    });

    return intercepter(allBidding, true);
});

mock.mock(RegExp('/bidding-svc/bid/client-bid.*'), 'get', (config: any) => {
    console.log("Get Bidding", config)
    return intercepter({
        id: "1",
        bwicId: config?.bwicId,
        clientId: config?.clientId,
        effectiveTime: new Date(),
        feedback: "Test",
        price: 100,
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