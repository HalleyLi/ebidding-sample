import { intercepter, mock } from '../config';
import { BwicSubmitParams } from '@/models/bwic/bwic';

mock.mock('/bwic-svc/bwic/create', 'post', (config: any) => {
    const body: BwicSubmitParams = JSON.parse(config?.body);
    console.log("submit Bwic", body)
    let id = "10";
    if (config.id) id = config.id;
    return intercepter({
        id: id,
        cusip: "CUSIP_" + id,
        issuer: "Issuer_" + id,
        dueDate: new Date(),
        clientId: "Sales_" + id,
        size: Math.round(Math.random() * 100) * 100000000,
        version: 1
    });
});
