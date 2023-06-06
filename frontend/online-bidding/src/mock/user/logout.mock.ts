import { mock } from '../config';

mock.mock('/account-svc/account/logout', 'post',
    {
        "message": null,
        "code": "SUCCESS",
        "success": true
    });
