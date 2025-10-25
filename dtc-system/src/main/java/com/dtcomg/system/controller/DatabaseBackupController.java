package com.dtcomg.system.controller;

import com.dtcomg.system.common.Result;
import com.dtcomg.system.task.DatabaseBackupTask;
import com.dtcomg.system.task.DatabaseRestoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据库备份控制器
 * 提供数据库备份和恢复的Web接口
 * 
 * @author 系统管理员
 * @version 1.0
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/api/system/backup")
@Tag(name = "数据库备份管理", description = "数据库备份和恢复相关接口")
public class DatabaseBackupController {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseBackupController.class);

    @Autowired
    private DatabaseBackupTask backupTask;

    @Autowired
    private DatabaseRestoreService restoreService;

    /**
     * 立即执行数据库备份
     * 
     * @return 操作结果
     */
    @Operation(summary = "立即执行备份", description = "手动触发一次数据库备份操作")
    @PreAuthorize("hasAuthority('system:backup:create')")
    @PostMapping("/execute")
    public Result<String> executeBackup() {
        try {
            backupTask.backupDatabase();
            return Result.success("数据库备份已开始执行");
        } catch (Exception e) {
            logger.error("执行数据库备份失败: {}", e.getMessage(), e);
            return Result.error("数据库备份执行失败: " + e.getMessage());
        }
    }

    /**
     * 获取备份文件列表
     * 
     * @return 备份文件列表
     */
    @Operation(summary = "获取备份文件列表", description = "获取所有数据库备份文件信息")
    @PreAuthorize("hasAuthority('system:backup:view')")
    @GetMapping("/list")
    public Result<List<DatabaseRestoreService.BackupFileInfo>> getBackupFiles() {
        try {
            List<DatabaseRestoreService.BackupFileInfo> backupFiles = restoreService.getBackupFiles();
            return Result.success(backupFiles);
        } catch (Exception e) {
            logger.error("获取备份文件列表失败: {}", e.getMessage(), e);
            return Result.error("获取备份文件列表失败: " + e.getMessage());
        }
    }

    /**
     * 从备份文件恢复数据库
     * 
     * @param fileName 备份文件名
     * @return 操作结果
     */
    @Operation(summary = "恢复数据库", description = "从指定的备份文件恢复数据库")
    @PreAuthorize("hasAuthority('system:backup:restore')")
    @PostMapping("/restore")
    public Result<String> restoreDatabase(@RequestParam String fileName) {
        try {
            logger.warn("用户正在执行数据库恢复操作，文件: {}", fileName);
            boolean success = restoreService.restoreDatabase(fileName);
            if (success) {
                return Result.success("数据库恢复成功");
            } else {
                return Result.error("数据库恢复失败，请查看日志获取详细信息");
            }
        } catch (Exception e) {
            logger.error("执行数据库恢复失败: {}", e.getMessage(), e);
            return Result.error("数据库恢复失败: " + e.getMessage());
        }
    }
}