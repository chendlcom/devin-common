# devin-common

在实际的项目开发中，如果没有很好的工具类代码抽象和提取，经常会有一些使用频率非常高的代码片段频繁的出现在各个代码文件中，非常影响代码的可读性、可维护性，本项目旨在编写、收集、整理上述的工具类代码，供自己及大家使用。

主要包含以下工具类：
- ClassUtil
- CollectionUtil
- HessianUtil
- JsonUtil
- ObjectUtil
- RestApiUtil *支持各种Http Method、Content-Type的Http Client工具类; 支持代理、SSL*
  >具体用法可参看RestApiUtilTest测试类。RestApiUtilTest是基于Springboot的测试类，可直接Junt Test运行，无需启动WEB服务
- StringUtil
