import { combineReducers } from '@reduxjs/toolkit';
import globalReducer from './global.store';
import userReducer from './user.store';
import BiddingReducer from './bidding.store';
import BwicReducer from './bwic.store';
import BwicPopularReducer from './bwicpopular.store';

const rootReducer = combineReducers({
  user: userReducer,
  global: globalReducer,
  bidding: BiddingReducer,
  bwic: BwicReducer,
  bwicpopular: BwicPopularReducer
});

export default rootReducer;
