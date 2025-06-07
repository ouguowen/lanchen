#!/bin/bash

# 请替换为您实际创建的GitHub仓库URL
GITHUB_REPO_URL="https://github.com/您的用户名/ai-creator-android.git"

# 切换到main分支
git branch -M main

# 添加远程仓库
git remote add origin $GITHUB_REPO_URL

# 设置GitHub身份验证
echo "请输入GitHub用户名:"
read GITHUB_USERNAME
echo "请输入GitHub个人访问令牌(PAT)或密码:"
read -s GITHUB_TOKEN

# 设置HTTPS凭证
git config credential.helper store
echo "https://$GITHUB_USERNAME:$GITHUB_TOKEN@github.com" > ~/.git-credentials
chmod 600 ~/.git-credentials

# 推送到GitHub
git push -u origin main

echo "项目已成功推送到GitHub仓库: $GITHUB_REPO_URL"