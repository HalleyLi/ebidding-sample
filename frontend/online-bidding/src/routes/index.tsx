import { lazy, FC } from 'react';
import LoginPage from '../pages/login/login';
import NotFoundPage from '../pages/NotFound';
import LayoutPage from '../pages/layout';
import BwicAdminPage from '../pages/sales-portal/bwicAdminList';
import BiddingListPage from '../pages/sales-portal/biddingList';
import BwicListPage from '../pages/client-portal/bwicList';
import MyBiddingPage from '../pages/client-portal/myBidding';
import { Navigate, RouteObject } from 'react-router';
import WrapperRouteComponent from './config';
import { useRoutes } from 'react-router';
import BwicPopularPage from '../pages/sales-portal/bwicPopular';

const routeList: RouteObject[] = [
  {
    path: '/',
    element: <WrapperRouteComponent element={<LoginPage />} titleId="title.login" />,
  },
  {
    path: '/login',
    element: <WrapperRouteComponent element={<LoginPage />} titleId="title.login" />,
  },
  {
    path: '/salesPortal',
    element: <WrapperRouteComponent element={<LayoutPage />} titleId="" auth />,
    children: [
      {
        path: '',
        element: <Navigate to="bwicAdmin" />,
      },
      {
        path: 'bwicAdmin',
        element: <WrapperRouteComponent element={<BwicAdminPage />} titleId="title.bwicAdmin" auth />,
      },
      {
        path: 'biddingList',
        element: <WrapperRouteComponent element={<BiddingListPage />} titleId="title.biddingList" auth />,
      },
      {
        path: 'bwicPopularList',
        element: <WrapperRouteComponent element={<BwicPopularPage />} titleId="title.bwicPopularList" auth />,
      },
    ],
  },
  {
    path: '/clientPortal',
    element: <WrapperRouteComponent element={<LayoutPage />} titleId="" auth />,
    children: [
      {
        path: '',
        element: <Navigate to="bwicList" />,
      },
      {
        path: 'bwicList',
        element: <WrapperRouteComponent element={<BwicListPage />} titleId="title.bwicList" auth />,
      },
      {
        path: 'mybidding',
        element: <WrapperRouteComponent element={<MyBiddingPage />} titleId="title.mybidding" auth />,
      }
    ],
  },
  {
    path: '/error',
    element: <WrapperRouteComponent element={<NotFoundPage />} titleId="title.notFount" />,
  },
];

const RenderRouter: FC = () => {
  const element = useRoutes(routeList);

  return element;
};

export default RenderRouter;
