package cn.aksu.supervision.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.aksu.supervision.data.model.Alert
import cn.aksu.supervision.data.model.Task
import cn.aksu.supervision.data.model.TaskStat
import cn.aksu.supervision.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class QuickEntry(
    val icon: ImageVector,
    val label: String,
    val route: String
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _pendingTasks = MutableStateFlow<List<Task>>(emptyList())
    val pendingTasks = _pendingTasks.asStateFlow()

    private val _recentAlerts = MutableStateFlow<List<Alert>>(emptyList())
    val recentAlerts = _recentAlerts.asStateFlow()

    private val _taskStat = MutableStateFlow<TaskStat?>(null)
    val taskStat = _taskStat.asStateFlow()

    var userName by mutableStateOf("监管员")
    var unreadCount by mutableStateOf(0)

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            taskRepository.getTasks(status = "PENDING").collect { result ->
                result.onSuccess { _pendingTasks.value = it.take(5) }
            }
        }
        viewModelScope.launch {
            taskRepository.getTaskStatistics().onSuccess {
                _taskStat.value = it
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToTaskList: (String?) -> Unit = {},
    onNavigateToAlertList: () -> Unit = {},
    onNavigateToTaskDetail: (Long) -> Unit = {},
    onNavigateToAlertDetail: (Long) -> Unit = {},
    onNavigateToMessages: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val pendingTasks by viewModel.pendingTasks.collectAsState()
    val taskStat by viewModel.taskStat.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("阿克苏监管", style = MaterialTheme.typography.titleLarge)
                    }
                },
                actions = {
                    BadgedBox(
                        badge = {
                            if (viewModel.unreadCount > 0) {
                                Badge { Text("${viewModel.unreadCount}") }
                            }
                        }
                    ) {
                        IconButton(onClick = onNavigateToMessages) {
                            Icon(Icons.Default.Notifications, "消息")
                        }
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 欢迎卡片
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "你好，${viewModel.userName}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatItem("待认领", taskStat?.pending ?: 0)
                            StatItem("进行中", taskStat?.inProgress ?: 0)
                            StatItem("已完成", taskStat?.completed ?: 0)
                            StatItem("延期", taskStat?.delayed ?: 0)
                        }
                    }
                }
            }

            // 快捷入口
            item {
                Text("快捷入口", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    QuickEntryButton(Icons.Default.Assignment, "待办任务") { onNavigateToTaskList("PENDING") }
                    QuickEntryButton(Icons.Default.CheckCircle, "现场检查") { onNavigateToTaskList("IN_PROGRESS") }
                    QuickEntryButton(Icons.Default.FactCheck, "整改验收") { onNavigateToTaskList(null) }
                    QuickEntryButton(Icons.Default.Warning, "预警处理") { onNavigateToAlertList() }
                }
            }

            // 待办任务
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("待办任务", style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = { onNavigateToTaskList("PENDING") }) {
                        Text("查看全部")
                    }
                }
            }

            if (pendingTasks.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("暂无待办任务", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                items(pendingTasks, key = { it.id }) { task ->
                    TaskCard(task = task, onClick = { onNavigateToTaskDetail(task.id) })
                }
            }

            // 最近预警
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("最近预警", style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = onNavigateToAlertList) {
                        Text("查看全部")
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun StatItem(label: String, count: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun QuickEntryButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon, label,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun TaskCard(task: Task, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    task.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                AssistChip(
                    onClick = {},
                    label = { Text(task.statusLabel(), style = MaterialTheme.typography.labelSmall) },
                    modifier = Modifier.height(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                task.enterpriseName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (!task.deadline.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "截止：${task.deadline}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen()
    }
}
