#!/bin/bash

echo "🚀 Math Olympiad APK Build Script"
set -e

# 进入项目目录
cd "$(dirname "$0")/math-olympiad-kotlin"

# 清理旧构建
echo "🧹 Cleaning project..."
./gradlew clean --daemon 2>/dev/null || true

# 生成 Release APK
echo "📦 Building Release APK..."
./gradlew assembleRelease

# 确认输出
APK_PATH="app/build/outputs/apk/release/app-release.apk"
if [ -f "$APK_PATH" ]; then
    echo "✅ APK generated successfully!"
    echo "📍 Location: $APK_PATH"
    ls -lh "$APK_PATH"
    echo ""
    echo "🎉 To install on device: adb install $APK_PATH"
else
    echo "❌ Build failed. Check logs above."
    exit 1
fi