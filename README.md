# 🎯 奥数闯关王 - Android MVP 版本

## 📱 项目介绍
一款专为小学生设计的数学奥数复习游戏化应用，采用 Kotlin + Jetpack Compose + Room + Hilt 技术栈开发。

## ✨ 核心功能（MVP 版本）
- ✅ **每日答题**：每天随机抽取 3 道不重复的奥数题（计算/逻辑/图形三类题型）
- ✅ **积分系统**：答对得 1 分，即时反馈鼓励语
- ✅ **数据持久化**：使用 Room 数据库存储用户积分、勋章、答题历史
- ✅ **成长激励**：勋章解锁系统（新手入门、坚持之星等）
- ✅ **精美 UI**：符合儿童审美的明亮色彩和圆角设计

## 🏗️ 技术架构
```
MVVM + Clean Architecture
├── Data Layer (Room Database, Repository)
├── Domain Layer (Use Cases)
└── Presentation Layer (Jetpack Compose UI)
```

## 📦 依赖清单
- AndroidX Core & Lifecycle
- Jetpack Compose (UI)
- Room Database (SQLite ORM)
- Hilt (Dependency Injection)
- Kotlin Coroutines + Flow

## 🚀 快速开始
1. **打开项目**：用 Android Studio 打开 `math-olympiad-kotlin` 目录
2. **同步 Gradle**：File -> Sync Project with Gradle Files
3. **运行应用**：点击 Run 按钮或按 Shift+F10
4. **首次启动**：会自动创建默认用户和基础题库（已在 `assets/questions.json`）

## 📂 项目结构
```
app/
├── data/                    # 数据层
│   ├── local/
│   │   ├── dao/            # Data Access Object
│   │   ├── database/       # Room Database
│   │   └── model/          # 实体类（User, Question, Badge）
│   └── repository/         # Repository 仓库
├── domain/                  # 业务逻辑层
│   └── usecase/            # UseCase 用例
├── presentation/           # UI 层
│   ├── ui/
│   │   ├── home/          # 首页
│   │   ├── quiz/          # 答题页
│   │   └── profile/       # 个人中心
│   ├── theme/             # 主题配置
│   └── navigation/        # 导航结构
├── di/                     # Hilt DI 模块
└── ui/                     # MainActivity
```

## 🎨 UI 设计规范
- **主色调**：活力橙 (#FF8C42)、天空蓝 (#5FB3D9)
- **圆角半径**：12-16dp（卡片、按钮）
- **字体大小**：标题 28sp，正文 16-18sp
- **图标风格**：Emoji + Material Icons 混合使用

## 📝 题库格式示例
```json
{
  "id": "math_001",
  "content": "小明有 5 个苹果，吃了 2 个，还剩几个？",
  "options": ["3 个", "4 个", "5 个", "2 个"],
  "correctAnswer": 0,
  "explanation": "5 - 2 = 3，所以答案是 3 个。",
  "type": "CALCULATION",
  "difficulty": 1
}
```

## 🔧 下一步开发计划
- [ ] 完整实现 QuizViewModel 中的答题逻辑
- [ ] 集成 Lottie 动画（答对/答错反馈）
- [ ] 完善勋章墙 UI 展示
- [ ] 添加排行榜功能
- [ ] 题库导入/导出功能
- [ ] 单元测试覆盖核心业务逻辑

## 👨‍💻 开发者说明
- **使用模型**：qwen3.5-35b-a3b（全程使用该模型）
- **最低 SDK**：24 (Android 7.0)
- **目标 SDK**：34 (Android 14)
- **语言**：Kotlin 1.9.20

## 📄 许可证
本项目仅供学习使用，版权归 Nous Research 所有。
