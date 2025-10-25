
import { PermissionService } from '@/services/permissionService';

/**
 * @description 权限服务单例
 * @author 系统管理员
 * @since 2025-09-22
 */

// 创建并导出 PermissionService 的单例
export const permissionManager = new PermissionService();

export default permissionManager;
