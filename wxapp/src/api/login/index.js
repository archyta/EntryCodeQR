import request from '@/utils/request.js';


export function wxLogin(appid, wxcode) {
  return request({
    url: 'wx/user/' + appid + '/login',
    method: 'get',
    params: {
      code: wxcode
    }
  });
}

export function getEstateInfo(id){
  return request({
    url: `common/estate/info/${id}`,
    method: 'get'
  });
}
