import { apiGetAllBwicPopularList } from '../../api/bwicpopular.api';
import { WorkStatus } from '../../models';
import { BwicPopularItem } from '../../models/bwic/bwic-popular';
import { setLoadingStatus } from '../../stores/bidding.store';
import { setBwicPopularItems } from '../../stores/bwicpopular.store';
import { Card, Col, Spin } from 'antd';
import ReactEcharts from 'echarts-for-react';
import { FC, useCallback, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';



const BwicPopularPage: FC = () => {
    const dispatch = useDispatch();
    const { loadingStatus, bwicPopularList } = useSelector(state => state.bwicpopular);

    const getOption = (bwicPopularList: BwicPopularItem[]) => {
        return {
            title: {
                text: 'Bidding Popular Rank'
            },
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'shadow'
                }
            },
            legend: {},
            grid: {
                left: '3%',
                right: '4%',
                bottom: '3%',
                containLabel: true
            },
            xAxis: {
                type: 'value',
                boundaryGap: [0, 0.01]
            },
            yAxis: {
                type: 'category',
                // data: ['cusip-1', 'cusip-1']
                data: bwicPopularList.map((item: BwicPopularItem) => item.cusip)
            },
            series: [
                {
                    name: 'bidding numbers',
                    type: 'bar',
                    // data: [111, 222]
                    data: bwicPopularList.map((item: BwicPopularItem) => item.numberOfBids).sort()
                }
            ]
        };
    }

    const compareNumbers = (arrayItemA:BwicPopularItem, arrayItemB:BwicPopularItem) => {
        if (arrayItemA.numberOfBids < arrayItemB.numberOfBids) {
            return -1
        }

        if (arrayItemA.numberOfBids > arrayItemB.numberOfBids) {
            return 1
        }

        return 0
    }


    const fetchAllBwicPopularList = useCallback(async () => {
        dispatch(
            setLoadingStatus(WorkStatus.IN_PROGRESS)
        );

        const { data, success } = await apiGetAllBwicPopularList(null);
        if (success) {
            dispatch(
                setBwicPopularItems({ bwicPopularList: data })
            );
        }

        dispatch(
            setLoadingStatus(success ? WorkStatus.SUCCESS : WorkStatus.ERROR)
        );
    }, []);

    useEffect(() => {
        fetchAllBwicPopularList();

    }, [fetchAllBwicPopularList]);

    return (
        <div className="bwic-homepage">
            <Spin size="large" spinning={loadingStatus !== WorkStatus.SUCCESS} >
                <Card title="Bidding Popular Rank" className=''>
                    <ReactEcharts option={getOption(bwicPopularList)} />
                </Card>
            </Spin>
        </div>
    );
};

export default BwicPopularPage;