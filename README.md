# SocketDemo
Android一个简单的Socket Demo

## 说明
每个设备的ID是计算出来的，理论上唯一，Server可以通过这个id进行区分不同的Client。

- 连接成功时Client会向Server发送自己的ID。
  * 如：`#ffffffff-d06f-26cd-ffff-ffffef05ac4a`
- 发送数据的格式为`{DEVICE_ID}#{JSON数据}`。
  * 如：`ffffffff-d06f-26cd-ffff-ffffef05ac4a#{"Name":"Reborn","Number":"201611010991"}`
