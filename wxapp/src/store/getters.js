import mainState from './mainState';
import { mapState } from '@wepy/x';

// 将getter中的所有导出属性数组。
function getAllStatasNames() {
  let m = [];
  for (let i in mainState.state) {
    m.push(i);
  }
  return m;
}
// 将导出的属性重新注册成 state.xxx

function convertDataName() {
  let m = mapState(getAllStatasNames());
  let n = {}
  for(let i in m){
    n['store_'+i] = m[i]
  }
  return n;
}

export default convertDataName;
