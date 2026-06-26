package cn.aksu.supervision.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import cn.aksu.supervision.util.BiometricUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    var userName by mutableStateOf("监管员")
    var department by mutableStateOf("监管一科")
    var phone by mutableStateOf("138****8888")
    var role by mutableStateOf("监管员")
    var biometricEnabled by mutableStateOf(false)
    var showLogoutDialog by mutableStateOf(false)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToMessages: () -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("个人中心") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 头像与姓名
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(72.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                viewModel.userName.firstOrNull()?.toString() ?: "",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(viewModel.userName, style = MaterialTheme.typography.titleLarge)
                    Text(viewModel.department, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // 功能列表
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                ProfileSectionItem(Icons.Default.Person, "个人信息", onClick = { /* TODO */ })
                ProfileSectionItem(Icons.Default.Lock, "修改密码", onClick = { /* TODO */ })
                ProfileSectionItem(
                    Icons.Default.Fingerprint,
                    "生物识别登录",
                    trailing = {
                        Switch(
                            checked = viewModel.biometricEnabled,
                            onCheckedChange = { enabled ->
                                viewModel.biometricEnabled = enabled
                                if (enabled) {
                                    // 调用 BiometricUtil 验证
                                }
                            }
                        )
                    }
                )
                Divider(modifier = Modifier.padding(vertical = 4.dp))
                ProfileSectionItem(
                    Icons.Default.Notifications,
                    "消息通知",
                    onClick = onNavigateToMessages,
                    trailing = {
                        Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                )
                ProfileSectionItem(Icons.Default.Settings, "系统设置", onClick = { /* TODO */ })
                ProfileSectionItem(Icons.Default.Info, "关于", onClick = { /* TODO */ })
                Divider(modifier = Modifier.padding(vertical = 4.dp))
                ProfileSectionItem(
                    Icons.Default.Sync,
                    "离线数据同步",
                    onClick = { /* 触发同步 */ }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 退出登录
            OutlinedButton(
                onClick = { viewModel.showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.Logout, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("退出登录")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("v1.0.0", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // 退出确认对话框
    if (viewModel.showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showLogoutDialog = false },
            title = { Text("退出登录") },
            text = { Text("确定要退出登录吗？") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.showLogoutDialog = false
                    onLogout()
                }) {
                    Text("确定", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showLogoutDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
private fun ProfileSectionItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit = {},
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        trailing?.invoke()
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    MaterialTheme {
        ProfileScreen()
    }
}
