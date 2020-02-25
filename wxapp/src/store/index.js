import Vuex from '@wepy/x';
import mainState from './mainState';
import getters from './getters';
import wepy from '@wepy/core';

wepy.use(Vuex);
const store = new Vuex.Store(mainState);


export default store;
