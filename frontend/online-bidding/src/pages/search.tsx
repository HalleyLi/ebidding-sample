import { FC } from 'react';
import { Button, DatePicker, Form, FormInstance } from 'antd';
import { SearchParams } from '../models';

const SearchForm: FC<{ onFinish: any }> = (props) => {
  const [form] = Form.useForm();
  const { onFinish } = props;

  const onCustomFinish = async (form: any) => {
    const searchParams: SearchParams = {
      startDate: form?.startDate?.toDate(),
      endDate: form?.endDate?.toDate(),
    }

    onFinish(searchParams);
  }

  return (
    <Form<SearchParams> layout="inline" name="search" form={form} onFinish={onCustomFinish}>
      <Form.Item label="Due Date"
        style={{
          marginBottom: 10,
        }}>
        <Form.Item
          name="startDate"
          style={{
            display: 'inline-block'
          }}>
          <DatePicker />
        </Form.Item>
        <span
          style={{
            display: 'inline-block',
            width: '24px',
            lineHeight: '32px',
            textAlign: 'center',
          }}>
          -
        </span>
        <Form.Item
          name="endDate"
          style={{
            display: 'inline-block'
          }}>
          <DatePicker />
        </Form.Item>
      </Form.Item>
      <Form.Item>
        <Button type="primary" htmlType="submit">Apply</Button>
      </Form.Item>
    </Form>
  );
};

export default SearchForm;
