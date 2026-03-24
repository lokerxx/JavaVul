const fs = require('fs');
const path = require('path');

// 定义源文件夹和目标文件夹
const sourceDir = path.join(__dirname, 'public');
const targetDir = path.join(__dirname, 'dist');

// 确保目标文件夹存在
if (!fs.existsSync(targetDir)) {
    fs.mkdirSync(targetDir, { recursive: true });
}

// 递归复制文件夹内容
function copyFolderRecursive(source, target) {
    // 读取源文件夹内容
    const files = fs.readdirSync(source);

    for (const file of files) {
        const sourcePath = path.join(source, file);
        const targetPath = path.join(target, file);

        // 判断是文件还是文件夹
        const stat = fs.statSync(sourcePath);
        if (stat.isDirectory()) {
            // 如果是文件夹，递归复制
            if (!fs.existsSync(targetPath)) {
                fs.mkdirSync(targetPath, { recursive: true });
            }
            copyFolderRecursive(sourcePath, targetPath);
        } else {
            // 如果是文件，直接复制
            fs.copyFileSync(sourcePath, targetPath);
            console.log(`Copied: ${sourcePath} -> ${targetPath}`);
        }
    }
}

// 执行复制
copyFolderRecursive(sourceDir, targetDir);
copyFolderRecursive("fake-api-server", targetDir);
console.log('All files copied from public to dist!');
