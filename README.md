# EntryCodeQR

## 介绍
2020年新冠病毒肆虐，百姓恐慌。全国一级戒备，各地小区陆续加强管控，启动配额制只允许每户人家每两天进出一次。
可以由门岗在手机端确认后进出，也可以由租户/访客自助登记后门岗放行（极简模式，登记备案制）。
此小程序是基于小程序的二维码电子通行证，旨在建立无接触式可回溯的门岗通行记录。通行记录同步计入“链飞”区块链，感谢链飞科技的无私扶持。

### 功能说明
1. 无接触式发放：住户线上认证身份，申请社区电子出入证，物业微信端审核发放。
2. 通行频率设置：小区管理员可在PC后台设置小区内每户N天出入X个人。
3. 门岗扫码放行：门岗保安扫描社区电子出入证，自动汇总出入记录。
4. 出入体温记录：门岗保安扫码后需记录测量体温，体现在出入记录中。
5. 防疫信息填报：住户申请社区电子出入证需先填写防疫信息，由物业审核后发放出入证。
6. 物业通知推送：最新政策变动及小区事件通报可通过物业通知全员触达。

这是小程序客户端，用wepy实现。
服务端cas目录。

## 程序截图
免安装免部署，门口放置二维码，住户微信扫码即可。
#### 1. 住户登记基本信息,并将此二维码提供给门岗。或者可以自助登记，免门岗确认。

![住户端](https://images.gitee.com/uploads/images/2020/0226/003951_c594e532_5220875.png "住户端")
![住户通行证](https://images.gitee.com/uploads/images/2020/0226/003822_b371c7e5_5220875.png  "住户通行证")
![自助登记，免门岗确认](https://images.gitee.com/uploads/images/2020/0307/192543_6caf6928_5220875.png "自助登记.png")

####  2. 门岗保安扫描住户二维码，并登记体温后放行或劝返。

![门岗端](https://images.gitee.com/uploads/images/2020/0226/003450_817d418a_5220875.png "门岗放行")

#### 3. 物业查看统计信息,足不出户掌控全局。

![物业端](https://images.gitee.com/uploads/images/2020/0226/003632_e402be6c_5220875.png "物业管理端")

## 联系我们
希望籍此为抗疫尽微薄之力。
体验环境，微信小程序 搜索 “出入福安”

联系电话： 刘先生 173 2307 9376
