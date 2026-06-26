package cn.aksu.supervision.ui.task

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.aksu.supervision.data.model.TaskStat
import cn.aksu.supervision.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class TaskStatViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _stat = MutableStateFlow(TaskStat(0, 0, 0, 0, 0, 0f))
    val stat = _stat.asStateFlow()

    init {
        loadStat()
    }

    private fun loadStat() {
        viewModelScope.launch {
            taskRepository.getTaskStatistics().onSuccess { _stat.value = it }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskStatScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: TaskStatViewModel = hiltViewModel()
) {
    val stat by viewModel.stat.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("任务统计") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "返回")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 总览卡片
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("任务总数", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        stat.total.toString(),
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "完成率：${"%.1f".format(stat.completionRate * 100)}%",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // 分类统计
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard("待认领", stat.pending, Color(0xFFFFA726), Modifier.weight(1f))
                StatCard("进行中", stat.inProgress, Color(0xFF42A5F5), Modifier.weight(1f))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard("已完成", stat.completed, Color(0xFF66BB6A), Modifier.weight(1f))
                StatCard("已延期", stat.delayed, Color(0xFFEF5350), Modifier.weight(1f))
            }

            // 完成进度条
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("完成进度", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { stat.completionRate },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("已完成 ${stat.completed}", style = MaterialTheme.typography.bodySmall)
                        Text("总计 ${stat.total}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                count.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TaskStatScreenPreview() {
    MaterialTheme {
        TaskStatScreen()
    }
}
