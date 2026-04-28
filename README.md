# 奥数闯关王 (Math Olympiad King)

一款专为小学生设计的少儿奥数学习App，采用 Kotlin + Jetpack Compose + Hilt + Room 技术栈开发。

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.10-A97BFF?logo=kotlin)
![Android](https://img.shields.io/badge/Android-8.0+-3DDC84?logo=android)
![License](https://img.shields.io/badge/License-MIT-blue.svg)

## ✨ 功能特性

### 🎮 闯关学习
- 每日3道精选题目，轻松无负担
- 积分系统和连续答题 streak
- 丰富的成就勋章系统

### 📝 智能题库
- 覆盖计算、逻辑、图形等多元题型
- 难度阶梯式分级
- 基于艾宾浩斯的错题复习

### 🧠 AI 趣味陪伴
- 鼓励式反馈，培养学习兴趣
- 趣味解题讲解
- 个性化学习建议

### 📊 数据可视化
- 学习进度一目了然
- 周榜激励学习动力
- 错题分类统计

## 🛠 技术栈

| 类别 | 技术 |
|------|------|
| 编程语言 | Kotlin 1.9.10 |
| UI 框架 | Jetpack Compose |
| 依赖注入 | Hilt |
| 数据库 | Room |
| 异步处理 | Kotlin Coroutines + Flow |
| 架构模式 | MVVM + Clean Architecture |

## 📁 项目结构

```
app/
├── src/main/java/com/example/matholympiad/
│   ├── data/              # 数据层 (Repository, DAO, Models)
│   ├── domain/            # 领域层 (UseCases, Business Logic)
│   ├── presentation/      # 表现层 (ViewModels, Screens, UI)
│   └── di/                # 依赖注入模块
├── src/test/              # 单元测试
│   ├── domain/usecase/    # UseCase 测试
│   ├── data/repository/   # Repository 测试
│   └── presentation/      # ViewModel 测试
└── src/androidTest/       # 仪器测试
```

## 🚀 快速开始

### 环境要求
- Android Studio Giraffe (2022.3.1) 或更高版本
- JDK 17
- Android SDK 34

### 编译步骤

```bash
# 克隆仓库
git clone https://github.com/ChrisAppollo/math-olympiad.git
cd math-olympiad

# 编译 Debug 版本
./gradlew assembleDebug

# 编译 Release 版本 (需要签名配置)
./gradlew assembleRelease
```

## 🧪 测试

项目包含完整的单元测试套件：

```bash
# 运行所有单元测试
./gradlew test

# 运行特定模块测试
./gradlew :app:testDebugUnitTest
```

### 测试覆盖
- ✅ UseCases 业务逻辑测试
- ✅ ViewModels 状态管理测试  
- ✅ Repository 数据层测试
- ✅ 间隔重复算法测试
- ✅ AI 趣味讲解生成测试

## 📱 截图

| 首页 | 答题 | 反馈 |
|:-----:|:----:|:----:|
| ![Home](screenshots/home.png) | ![Quiz](screenshots/quiz.png) | ![Feedback](screenshots/feedback.png) |

| 错题本 | 排行榜 | 个人 |
|:------:|:------:|:----:|
| ![Wrong](screenshots/wrong.png) | ![Leaderboard](screenshots/leaderboard.png) | ![Profile](screenshots/profile.png) |

## 📦 发布版本

| 版本 | 日期 | 说明 |
|------|------|------|
| v1.0.0 | 2026-04-27 | 首次发布，完整功能上线 |

## 📄 开源许可

本项目基于 MIT 许可证开源 - 详见 [LICENSE](LICENSE) 文件

## 📮 联系我们

- **作者:** ChrisAppollo
- **项目:** https://github.com/ChrisAppollo/math-olympiad

---

**Built with ❤️ by Nous Research**
