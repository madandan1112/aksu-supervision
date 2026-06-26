package cn.aksu.supervision.ui.task

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.aksu.supervision.data.model.Task
import cn.aksu.supervision.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _task = MutableStateFlow<Task?>(null)
    val task = _task.asStateFlow()

    var isLoading by mutableStateOf(false)
    var claimSuccess by mutableStateOf(false)
    var showDelayDialog by mutableStateOf(false)
    var delayReason by mutableStateOf("")
    var delayDeadline by mutableStateOf("")

    fun loadTask(taskId: Long) {
        viewModelScope.launch {
            isLoading = true
            taskRepository.getTask(taskId).onSuccess { _task.value = it }
            isLoading = false
        }
    }

    fun claimTask(taskId: Long) {
        viewModelScope.launch {
            isLoading = true
            taskRepository.claimTask(taskId).onSuccess {
                _task.value = it
                claimSuccess = true
            }
            isLoading = false
        }
    }

    fun delayTask(taskId: Long) {
        if (delayReason.isBlank() || delayDeadline.isBlank()) return
        viewModelScope.launch {
            taskRepository.delayTask(taskId, delayReason, delayDeadline).onSuccess {
                _task.value = it
                showDelayDialog = false
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: Long = 0,
    onNavigateBack: () -> Unit = {},
    onNavigateToInspection: (Long, Long) -> Unit = { _, _ -> },
    onNavigateToEnterprise: (Long) -> Unit = {},
    viewModel: TaskDetailViewModel = hiltViewModel()
) {
    val task by viewModel.task.collectAsState()

    LaunchedEffect(taskId) { viewModel.loadTask(taskId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("任务详情") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "返回")
                    }
                }
            )
        }
    ) { padding ->
        if (viewModel.isLoading) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
        } else {
            task?.let { t ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 标题与状态
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(t.title, style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                AssistChip(onClick = {}, label = { Text(t.typeLabel()) })
                                AssistChip(onClick = {}, label = { Text(t.priorityLabel()) })
                                Surface(
                                    shape = MaterialTheme.shapes.small,
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        t.statusLabel(),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    }

                    // 企业信息
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("检查对象", style = MaterialTheme.typography.titleSmall)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(t.enterpriseName, style = MaterialTheme.typography.bodyLarge)
                                TextButton(onClick = { onNavigateToEnterprise(t.enterpriseId) }) {
                                    Text("查看档案")
                                }
                            }
                            if (!t.location.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row {
                                    Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(t.location!!, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }

                    // 任务描述
                    t.description?.let { desc ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("任务描述", style = MaterialTheme.typography.titleSmall)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(desc, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }

                    // 截止日期
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("时间信息", style = MaterialTheme.typography.titleSmall)
                            Spacer(modifier = Modifier.height(8.dp))
                            t.deadline?.let {
                                Row {
                                    Icon(Icons.Default.Schedule, null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("截止日期：$it")
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row {
                                Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("创建时间：${t.createdAt}")
                            }
                            t.assigneeName?.let {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row {
                                    Icon(Icons.Default.Person, null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("负责人：$it")
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // 操作按钮
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        when (t.status) {
                            "PENDING" -> {
                                Button(
                                    onClick = { viewModel.claimTask(t.id) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.HowToReg, null)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("认领任务")
                                }
                            }
                            "CLAIMED", "IN_PROGRESS" -> {
                                Button(
                                    onClick = { onNavigateToInspection(t.id, t.enterpriseId) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Edit, null)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("开始检查")
                                }
                                OutlinedButton(
                                    onClick = { viewModel.showDelayDialog = true },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Schedule, null)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("申请延期")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // 延期对话框
    if (viewModel.showDelayDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showDelayDialog = false },
            title = { Text("申请延期") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = viewModel.delayReason,
                        onValueChange = { viewModel.delayReason = it },
                        label = { Text("延期原因") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = viewModel.delayDeadline,
                        onValueChange = { viewModel.delayDeadline = it },
                        label = { Text("新截止日期") },
                        placeholder = { Text("yyyy-MM-dd") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.delayTask(taskId) }) {
                    Text("确认")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showDelayDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TaskDetailScreenPreview() {
    MaterialTheme {
        TaskDetailScreen()
    }
}
