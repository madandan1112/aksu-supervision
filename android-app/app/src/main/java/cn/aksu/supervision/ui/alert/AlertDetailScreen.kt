package cn.aksu.supervision.ui.alert

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
import cn.aksu.supervision.data.api.ApiService
import cn.aksu.supervision.data.model.Alert
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AlertDetailViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _alert = MutableStateFlow<Alert?>(null)
    val alert = _alert.asStateFlow()

    var isLoading by mutableStateOf(false)
    var handleResult by mutableStateOf("")
    var showHandleDialog by mutableStateOf(false)
    var isHandling by mutableStateOf(false)
    var handleSuccess by mutableStateOf(false)

    fun loadAlert(id: Long) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = apiService.getAlert(id)
                if (response.isSuccessful && response.data != null) {
                    _alert.value = response.data
                }
            } catch (_: Exception) { }
            isLoading = false
        }
    }

    fun handleAlert(id: Long) {
        if (handleResult.isBlank()) return
        viewModelScope.launch {
            isHandling = true
            try {
                val response = apiService.handleAlert(id, handleResult)
                if (response.isSuccessful) {
                    handleSuccess = true
                    showHandleDialog = false
                    loadAlert(id)
                }
            } catch (_: Exception) { }
            isHandling = false
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDetailScreen(
    alertId: Long = 0,
    onNavigateBack: () -> Unit = {},
    viewModel: AlertDetailViewModel = hiltViewModel()
) {
    val alert by viewModel.alert.collectAsState()

    LaunchedEffect(alertId) { viewModel.loadAlert(alertId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("预警详情") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "返回")
                    }
                }
            )
        }
    ) { padding ->
        alert?.let { a ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 预警头部
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            Surface(
                                shape = MaterialTheme.shapes.medium,
                                color = when (a.level) {
                                    "CRITICAL" -> MaterialTheme.colorScheme.errorContainer
                                    "WARNING" -> MaterialTheme.colorScheme.tertiaryContainer
                                    else -> MaterialTheme.colorScheme.primaryContainer
                                },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = androidx.compose.ui.Alignment.Center) {
                                    Icon(
                                        when (a.level) {
                                            "CRITICAL" -> Icons.Default.Error
                                            "WARNING" -> Icons.Default.Warning
                                            else -> Icons.Default.Info
                                        },
                                        null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(a.title, style = MaterialTheme.typography.titleMedium)
                                Text("${a.typeLabel()} · ${a.levelLabel()}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                // 预警内容
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("预警内容", style = MaterialTheme.typography.titleSmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(a.content, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                // 关联信息
                a.enterpriseName?.let { name ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("关联企业", style = MaterialTheme.typography.titleSmall)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                Icon(Icons.Default.Business, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(name)
                            }
                        }
                    }
                }

                // 时间信息
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("时间信息", style = MaterialTheme.typography.titleSmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            Icon(Icons.Default.Schedule, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("产生时间：${a.createdAt}")
                        }
                        a.handledAt?.let {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row {
                                Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("处理时间：$it")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // 处理按钮
                if (a.handlerId == null) {
                    Button(
                        onClick = { viewModel.showHandleDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Icon(Icons.Default.Build, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("处理预警")
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("已处理", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("处理人：${a.handlerName ?: "-"}", style = MaterialTheme.typography.bodySmall)
                            a.handleResult?.let {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("处理结果：$it", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }

    // 处理对话框
    if (viewModel.showHandleDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showHandleDialog = false },
            title = { Text("处理预警") },
            text = {
                OutlinedTextField(
                    value = viewModel.handleResult,
                    onValueChange = { viewModel.handleResult = it },
                    label = { Text("处理结果") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.handleAlert(alertId) },
                    enabled = viewModel.handleResult.isNotBlank() && !viewModel.isHandling
                ) {
                    if (viewModel.isHandling) CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    else Text("确认")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showHandleDialog = false }) { Text("取消") }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AlertDetailScreenPreview() {
    MaterialTheme {
        AlertDetailScreen()
    }
}
