import { intercepter, mock } from '../config';
import { BWICItem } from '@/models/bwic/bwic';

mock.mock(RegExp('/api/v1/bwic/list.*'), 'get', (config: any) => {
    let allBWICs: BWICItem[] = [];
    new Array(100).fill(undefined).forEach((item, index) => {
        allBWICs.push({
            id: index.toString(),
            cusip: "CUSIP_" + (index % 3).toString(),
            issuer: "Issuer_" + index.toString(),
            dueDate: new Date(),
            clientId: "Sales_" + index.toString(),
            size: Math.round(Math.random() * 100) * 100000000,
            version: index % 3,
            isOverDue: false
        });
    });

    return intercepter(allBWICs, true);
});
