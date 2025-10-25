package com.dtcomg.system.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据库恢复服务类
 * 提供数据库备份文件列表查询和数据库恢复功能
 * 
 * @author 系统管理员
 * @version 1.0
 * @since 2024-01-01
 */
@Service
public class DatabaseRestoreService {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseRestoreService.class);

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    // 备份文件保存路径，与DatabaseBackupTask保持一致
    private static final String BACKUP_DIR = "backup_sql";

    /**
     * 获取所有数据库备份文件列表
     * 
     * @return 备份文件列表，按时间倒序排列（最新的在前）
     */
    public List<BackupFileInfo> getBackupFiles() {
        List<BackupFileInfo> backupFiles = new ArrayList<>();
        
        try {
            Path backupDir = Paths.get(BACKUP_DIR);
            if (!Files.exists(backupDir)) {
                logger.warn("备份目录不存在: {}", BACKUP_DIR);
                return backupFiles;
            }

            // 遍历备份目录中的所有.sql文件
            Files.list(backupDir)
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".sql"))
                .forEach(p -> {
                    try {
                        File file = p.toFile();
                        BackupFileInfo info = new BackupFileInfo();
                        info.setFileName(file.getName());
                        info.setFilePath(file.getAbsolutePath());
                        info.setFileSize(file.length());
                        info.setCreateTime(file.lastModified());
                        backupFiles.add(info);
                    } catch (Exception e) {
                        logger.error("处理备份文件信息时出错: {}", e.getMessage(), e);
                    }
                });

            // 按创建时间倒序排列，最新的备份在前
            backupFiles.sort(Comparator.comparing(BackupFileInfo::getCreateTime).reversed());

        } catch (IOException e) {
            logger.error("获取备份文件列表时发生异常: {}", e.getMessage(), e);
        }

        return backupFiles;
    }

    /**
     * 从备份文件恢复数据库
     * 
     * @param fileName 备份文件名
     * @return 恢复是否成功
     */
    public boolean restoreDatabase(String fileName) {
        // 验证文件名
        if (fileName == null || !fileName.endsWith(".sql")) {
            logger.error("无效的备份文件名: {}", fileName);
            return false;
        }

        // 确保备份文件存在
        String backupFilePath = BACKUP_DIR + File.separator + fileName;
        File backupFile = new File(backupFilePath);
        if (!backupFile.exists() || !backupFile.isFile()) {
            logger.error("备份文件不存在: {}", backupFilePath);
            return false;
        }

        try {
            // 从URL中提取数据库名
            String databaseName = extractDatabaseName(dbUrl);
            if (databaseName == null) {
                logger.error("无法从数据库URL中提取数据库名: {}", dbUrl);
                return false;
            }

            // 检查mysql命令是否可用
            String mysqlPath = findMysql();
            if (mysqlPath == null) {
                logger.error("未找到mysql命令，请确保MySQL已正确安装且mysql在系统PATH中");
                return false;
            }
            
            logger.info("使用mysql路径: {}", mysqlPath);
            
            // 执行恢复命令
            return executeRestoreCommand(mysqlPath, databaseName, backupFile.getAbsolutePath());
        } catch (Exception e) {
            logger.error("执行数据库恢复时发生异常: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 查找mysql命令的路径
     * @return mysql命令路径，如果找不到则返回null
     */
    private String findMysql() {
        String os = System.getProperty("os.name").toLowerCase();
        String[] possiblePaths = null;
        
        if (os.contains("win")) {
            // Windows常见安装路径
            possiblePaths = new String[] {
                "mysql",  // 检查PATH
                "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysql",
                "C:\\Program Files\\MySQL\\MySQL Server 5.7\\bin\\mysql",
                "C:\\Program Files (x86)\\MySQL\\MySQL Server 8.0\\bin\\mysql",
                "C:\\Program Files (x86)\\MySQL\\MySQL Server 5.7\\bin\\mysql"
            };
        } else {
            // Linux/Unix常见安装路径
            possiblePaths = new String[] {
                "mysql",  // 检查PATH
                "/usr/bin/mysql",
                "/usr/local/bin/mysql",
                "/opt/mysql/mysql/bin/mysql"
            };
        }
        
        for (String path : possiblePaths) {
            try {
                ProcessBuilder pb = new ProcessBuilder();
                if (os.contains("win")) {
                    pb.command("cmd.exe", "/c", path, "--version");
                } else {
                    pb.command("sh", "-c", path + " --version");
                }
                pb.redirectErrorStream(true);
                Process p = pb.start();
                int exitCode = p.waitFor();
                if (exitCode == 0) {
                    return path;
                }
            } catch (Exception ignored) {
                // 路径不存在或无法执行，继续尝试下一个
            }
        }
        
        return null;
    }
    
    /**
     * 执行数据库恢复命令
     * @param mysqlPath mysql命令路径
     * @param databaseName 数据库名
     * @param backupFilePath 备份文件绝对路径
     * @return 是否恢复成功
     */
    private boolean executeRestoreCommand(String mysqlPath, String databaseName, String backupFilePath) {
        try {
            // 确认备份文件存在
            File backupFile = new File(backupFilePath);
            if (!backupFile.exists() || !backupFile.isFile()) {
                logger.error("备份文件不存在或不是有效文件: {}", backupFilePath);
                return false;
            }
            
            // 构建命令数组，正确处理带空格的路径
            ProcessBuilder pb = new ProcessBuilder();
            
            // 构建命令参数列表，不使用命令行重定向符号
            java.util.List<String> commandList = new java.util.ArrayList<>();
            
            // 根据操作系统决定如何执行命令
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                // Windows系统下，使用cmd.exe /c 执行命令
                commandList.add("cmd.exe");
                commandList.add("/c");
                // 将命令作为一个整体传递，确保路径正确引用
                commandList.add(String.format("\"%s\" -u%s -p%s %s", 
                    mysqlPath, dbUsername, dbPassword, databaseName));
            } else {
                // Linux/Unix系统下，直接使用mysql命令
                commandList.add(mysqlPath);
                commandList.add("-u" + dbUsername);
                commandList.add("-p" + dbPassword);
                commandList.add(databaseName);
            }
            
            // 设置命令
            pb.command(commandList);
            
            // 使用ProcessBuilder的文件重定向功能，而不是命令行重定向符号
            pb.redirectInput(backupFile);
            
            logger.info("执行数据库恢复命令: {}", pb.command());
            logger.info("恢复输入重定向自文件: {}", backupFilePath);
            
            // 重定向错误流到标准输出
            pb.redirectErrorStream(true);
            Process process = pb.start();
            
            // 读取进程输出
            java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(process.getInputStream(), java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
            }
            
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                logger.info("数据库恢复成功，从文件: {}", backupFilePath);
                return true;
            } else {
                logger.error("数据库恢复失败，退出码: {}, 错误信息: {}", exitCode, output.toString());
                return false;
            }
        } catch (IOException | InterruptedException e) {
            logger.error("执行恢复命令时发生异常: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 从数据库URL中提取数据库名
     * 
     * @param dbUrl 数据库连接URL
     * @return 数据库名
     */
    private String extractDatabaseName(String dbUrl) {
        // 使用正则表达式从URL中提取数据库名，格式通常为 jdbc:mysql://host:port/databaseName?params
        // 这种方式更加健壮，能处理各种可能的URL格式
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("jdbc:mysql://[^/]+/([^?]+)");
        java.util.regex.Matcher matcher = pattern.matcher(dbUrl);
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        // 备用方案：使用原始的字符串截取方法
        int startIndex = dbUrl.lastIndexOf('/') + 1;
        int endIndex = dbUrl.indexOf('?');
        if (endIndex == -1) {
            endIndex = dbUrl.length();
        }
        if (startIndex > 0 && startIndex < endIndex) {
            return dbUrl.substring(startIndex, endIndex);
        }
        
        logger.warn("无法从数据库URL中提取数据库名，使用默认值 'rui_hua_erp'");
        return "rui_hua_erp"; // 默认返回配置文件中的数据库名
    }

    // 该方法已不再使用，恢复命令构建逻辑已移至executeRestoreCommand方法中


    /**
     * 备份文件信息类
     */
    public static class BackupFileInfo {
        private String fileName;
        private String filePath;
        private long fileSize;
        private long createTime;

        // Getters and Setters
        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public long getFileSize() {
            return fileSize;
        }

        public void setFileSize(long fileSize) {
            this.fileSize = fileSize;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }
    }
}