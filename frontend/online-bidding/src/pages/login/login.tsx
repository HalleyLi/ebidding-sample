import { FC } from 'react';
import './index.scss';
import { Button, Form, Input, Radio, message } from 'antd';
import { useNavigate, useLocation } from 'react-router';
import { LoginParams, Role } from '../../models/user/login';
import { useDispatch } from 'react-redux';
import { formatSearch } from '../../utils/formatSearch';
import { setUserItem } from '../../stores/user.store';
import { apiLogin } from '../../api/user.api';

const initialValues: LoginParams = {
  username: 'test-client',
  password: '123456'
};

const LoginForm: FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const dispatch = useDispatch();

  const onFinished = async (form: LoginParams) => {
    const { data, success } = await apiLogin(form);

    if (success) {
      localStorage.setItem('t', data.token);
      localStorage.setItem('username', data.name);
      dispatch(
        setUserItem({
          logged: true,
          username: data.name,
          role: data.role as Role
        }),
      );

      const search = formatSearch(location.search);
      if (search.from) {
        navigate(search.from);
      }
      else if (data.role === Role.SALES) {
        navigate({ pathname: '/salesPortal' })
      }
      else if (data.role === Role.CLIENT) {
        navigate({ pathname: '/clientPortal' })
      }
    } else {
            message.error('Fail to login!');
    }
  };

  return (
    <div className="login-page">
      <Form<LoginParams> onFinish={onFinished} className="login-page-form" initialValues={initialValues}>
        <h2>BWIC eBidding Platform</h2>
        <Form.Item name="username" rules={[{ required: true, message: 'Please enter username.' }]}>
          <Input placeholder="User Name" />
        </Form.Item>
        <Form.Item name="password" rules={[{ required: true, message: 'Please enter password.' }]}>
          <Input type="password" placeholder="Password" />
        </Form.Item>
        {/* <Form.Item name="role">
          <Radio.Group>
            <Radio value="sales">Sales</Radio>
            <Radio value="client">Client</Radio>
          </Radio.Group>
        </Form.Item> */}
        <Form.Item>
          <Button htmlType="submit" type="primary" className="login-page-form_button">
            Login
          </Button>
        </Form.Item>
      </Form>
    </div>
  );
};

export default LoginForm;
