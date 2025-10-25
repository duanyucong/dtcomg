package com.dtcomg.system.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 数据库备份定时任务
 * 实现应用启动时备份一次，并且每天0点自动备份数据库
 * 
 * @author 系统管理员
 * @version 1.0
 * @since 2024-01-01
 */
@Component
@EnableScheduling
public class DatabaseBackupTask implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseBackupTask.class);

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    // 备份文件保存路径
    private static final String BACKUP_DIR = "backup_sql";

    // 保留备份文件的天数
    private static final int RETAIN_DAYS = 7;

    /**
     * 应用启动时执行一次备份
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("应用启动，开始执行数据库备份");
        backupDatabase();
    }

    /**
     * 每天0点执行数据库备份
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void scheduledBackup() {
        logger.info("定时任务触发，开始执行数据库备份");
        backupDatabase();
    }

    /**
     * 执行数据库备份
     */
    public void backupDatabase() {
        // 确保备份目录存在
        File backupDirectory = new File(BACKUP_DIR);
        if (!backupDirectory.exists()) {
            boolean mkdirResult = backupDirectory.mkdirs();
            if (!mkdirResult) {
                logger.error("创建备份目录失败: {}", BACKUP_DIR);
                return;
            }
        }

        try {
            // 从URL中提取数据库名
            String databaseName = extractDatabaseName(dbUrl);
            if (databaseName == null) {
                logger.error("无法从数据库URL中提取数据库名: {}", dbUrl);
                return;
            }

            // 生成备份文件名，包含时间戳
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String backupFileName = databaseName + "_backup_" + timeStamp + ".sql";
            String backupFilePath = BACKUP_DIR + File.separator + backupFileName;
            String absBackupFilePath = new File(backupFilePath).getAbsolutePath();
            
            // 检查mysqldump命令是否可用
            String mysqlDumpPath = findMysqlDump();
            if (mysqlDumpPath == null) {
                logger.error("未找到mysqldump命令，请确保MySQL已正确安装且mysqldump在系统PATH中");
                return;
            }
            
            logger.info("使用mysqldump路径: {}", mysqlDumpPath);
            
            // 执行备份命令
            boolean backupSuccess = executeBackupCommand(mysqlDumpPath, databaseName, absBackupFilePath);
            
            if (backupSuccess) {
                logger.info("数据库备份成功，文件保存至: {}", absBackupFilePath);
                // 清理过期的备份文件
                cleanupOldBackups();
            }
        } catch (Exception e) {
            logger.error("执行数据库备份时发生异常: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 查找mysqldump命令的路径
     * @return mysqldump命令路径，如果找不到则返回null
     */
    private String findMysqlDump() {
        String os = System.getProperty("os.name").toLowerCase();
        String[] possiblePaths = null;
        
        if (os.contains("win")) {
            // Windows常见安装路径
            possiblePaths = new String[] {
                "mysqldump",  // 检查PATH
                "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump",
                "C:\\Program Files\\MySQL\\MySQL Server 5.7\\bin\\mysqldump",
                "C:\\Program Files (x86)\\MySQL\\MySQL Server 8.0\\bin\\mysqldump",
                "C:\\Program Files (x86)\\MySQL\\MySQL Server 5.7\\bin\\mysqldump"
            };
        } else {
            // Linux/Unix常见安装路径
            possiblePaths = new String[] {
                "mysqldump",  // 检查PATH
                "/usr/bin/mysqldump",
                "/usr/local/bin/mysqldump",
                "/opt/mysql/mysql/bin/mysqldump"
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
     * 执行数据库备份命令
     * @param mysqlDumpPath mysqldump命令路径
     * @param databaseName 数据库名
     * @param backupFilePath 备份文件绝对路径
     * @return 是否备份成功
     */
    private boolean executeBackupCommand(String mysqlDumpPath, String databaseName, String backupFilePath) {
        try {
            // 创建输出文件
            File backupFile = new File(backupFilePath);
            
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
                    mysqlDumpPath, dbUsername, dbPassword, databaseName));
            } else {
                // Linux/Unix系统下，直接使用mysqldump命令
                commandList.add(mysqlDumpPath);
                commandList.add("-u" + dbUsername);
                commandList.add("-p" + dbPassword);
                commandList.add(databaseName);
            }
            
            // 设置命令
            pb.command(commandList);
            
            // 使用ProcessBuilder的文件重定向功能，而不是命令行重定向符号
            pb.redirectOutput(backupFile);
            
            logger.info("执行备份命令: {}", pb.command());
            logger.info("备份输出重定向到文件: {}", backupFilePath);
            
            // 重定向错误流到标准输出
            pb.redirectErrorStream(true);
            Process process = pb.start();
            
            // 读取进程错误输出
            java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(process.getInputStream(), java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
            }
            
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                // 验证备份文件是否创建且大小合理
                if (backupFile.exists() && backupFile.length() > 0) {
                    logger.info("备份文件创建成功，大小: {} 字节", backupFile.length());
                    return true;
                } else {
                    logger.error("备份命令执行成功但文件未创建或为空");
                    return false;
                }
            } else {
                logger.error("数据库备份失败，退出码: {}, 错误信息: {}", exitCode, output.toString());
                // 如果备份失败且文件已创建，尝试删除它
                if (backupFile.exists()) {
                    backupFile.delete();
                }
                return false;
            }
        } catch (IOException | InterruptedException e) {
            logger.error("执行备份命令时发生异常: {}", e.getMessage(), e);
            // 尝试清理可能创建的不完整文件
            try {
                File backupFile = new File(backupFilePath);
                if (backupFile.exists()) {
                    backupFile.delete();
                }
            } catch (Exception cleanupEx) {
                logger.warn("清理失败的备份文件时出错: {}", cleanupEx.getMessage());
            }
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

    // 该方法已不再使用，备份命令构建逻辑已移至executeBackupCommand方法中

    /**
     * 清理过期的备份文件
     */
    private void cleanupOldBackups() {
        try {
            File dir = new File(BACKUP_DIR);
            File[] files = dir.listFiles((d, name) -> name.endsWith(".sql"));
            if (files == null) {
                return;
            }

            // 计算保留截止时间
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -RETAIN_DAYS);
            Date cutoffDate = calendar.getTime();

            for (File file : files) {
                if (file.lastModified() < cutoffDate.getTime()) {
                    boolean deleted = file.delete();
                    if (deleted) {
                        logger.info("已删除过期备份文件: {}", file.getName());
                    } else {
                        logger.warn("删除过期备份文件失败: {}", file.getName());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("清理过期备份文件时发生异常: {}", e.getMessage(), e);
        }
    }
}