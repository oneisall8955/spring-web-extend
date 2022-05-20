# spring-web-extend

基于 springboot 构造一些常用轮子，提供插拔式，可配置的，可扩展的 starter 及对应的 example。

## 近期计划的轮子

- 接口参数验签v1-特定业务对接方
- 接口参数验签v2-open api
- 接口限流
- 接口redis分布式锁

## 待优化

### 竖转横，VerticalAcrossHandler 的优化
- [ ] FieldConvertInfo 转为非静态内部类，移除of方法，使用构造器，提供检测方式，返回 boolean
- [ ] VerticalAcrossMapping 重新命名，太长
- [ ] VerticalConverter 提供JSON解析，convert 接口提供当前FieldConvertInfo或对应的Field属性（或者创建一个类ctx，避免参数过长）
- [ ] VerticalAcrossHandler ，添加注册自定义converter，使用顺序比defaultMappingConverterMap级别要高
- [ ] VerticalAcrossHandler.acrossConstructor 和 FieldConvertInfo.setterMethod 是否会失效（虚拟机回收后更换地址了）？请排查，如是提供兜底，再次创建等
