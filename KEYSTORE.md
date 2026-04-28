# 发布密钥信息
# 
# 使用以下信息配置 GitHub Actions 或其他 CI/CD 系统：
# - Store File: release-key.jks
# - Store Password: math-app-2026
# - Key Alias: matholympiad
# - Key Password: math-app-2026
#
# ⚠️ 重要：此密钥仅用于测试/演示目的
# 正式发布时请使用存储在安全库（如 GitHub Secrets）中的密钥
#
# GitHub Actions 配置：
# 1. 将 release-key.jks 上传为 Secrets (base64 编码)
# 2. 添加环境变量：
#    - KEYSTORE_PASSWORD: math-app-2026
#    - KEY_PASSWORD: math-app-2026
