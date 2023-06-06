import { apiSubmitBwic } from '../../api/bwic.api';
import { BwicSubmitParams } from '@/models/bwic/bwic';
import { setBwicItem } from '../../stores/bwic.store';
import { Button, DatePicker, Form, Input, InputNumber, message } from 'antd';
import { FC } from 'react';
import { useDispatch } from 'react-redux';

const layout = {
  labelCol: {
    span: 5,
  },
  wrapperCol: {
    span: 16,
  },
};

const tailLayout = {
  wrapperCol: {
    offset: 8,
    span: 16,
  },
};

const AddBwicPage: FC<{ onClose: any }> = (props) => {

  const dispatch = useDispatch();
  const [form] = Form.useForm();
  const { onClose } = props;

  const onFinished = async (formParams: BwicSubmitParams) => {
    const { data, success } = await apiSubmitBwic(formParams);
    if (success) {
      if (data) {
        dispatch(
          setBwicItem(data)
        );
        message.success('Bwic added success.');
        onClose.call();
        form.resetFields();
        return;
      }
    }

    message.error('Bwic added fail.');
  };

  return (
    <div>
      <Form<BwicSubmitParams> {...layout} form={form} onFinish={onFinished} name="addBwicForm">
        <Form.Item name="cusip" rules={[{ required: true, message: 'Please enter Bond Cusip.' }]} label="Bond Cusip">
          <Input placeholder="Bond Cusip" />
        </Form.Item>
        <Form.Item name="clientId" rules={[{ required: true, message: 'Please enter Bond Owner.' }]} label="Bond Owner">
          <Input placeholder="Bond Owner" />
        </Form.Item>
        <Form.Item name="dueDate" label="Due Date" rules={[{ required: true, message: 'Please select time!' }]} >
          <DatePicker showTime format="YYYY-MM-DD HH:mm:ss" style={{ width: '100%', }} />
        </Form.Item>
        <Form.Item name="size" rules={[{ required: true, message: 'Please enter Size.' }]} label="Size">
          <Input placeholder="Size"/>
        </Form.Item>
        <Form.Item {...tailLayout}>
          <Button className='float-right' type="primary" htmlType="submit" ghost>
            Submit
          </Button>
        </Form.Item>
      </Form>
    </div>

  );
};

export default AddBwicPage;
