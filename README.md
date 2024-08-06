# DingZhen Servlet

![cdbf6c81800a19d8bc3e6ba78aad958ba61ea8d3e843](https://github.com/user-attachments/assets/c9cef282-cdc3-49b3-b730-84c8090bfac3)

> 提示: 吸烟有害健康.

## 兼容性

按理来说通杀, 它的那个验证十分垃圾, 所有的东西都是明文传输的

## 帮助我们绕过验证

> 搭配代理的Websocket可以完美绕过检测, 欢迎搭建租号玩服务

现在这个还不是完整的服务端实现. 我们通过在服务端刷新token来绕过多设备检测, 你可以搭建共享账户服务端来为更多的用户提供服务

请务必把设备码改到`FUMANTHE`, 否则你的账户将面临封禁 (不是玩笑)

同时你还需要提供vapegg_session的cookie (可以在开发者工具中找到), 这个cookie应该有时效性,
但是我们还没有找到绕过reCaptcha的方法.

## 关于政治问题

写程序就是写程序, 为什么要扯政治, 写挂也是一样, 想要有用户就不要歧视用户.

## 参与到我们的开发中

- 抓包工具 httpdebugger, wireshark
- IDE Intellij IDEA
