package cn.aksu.supervision.ui.rectification

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import cn.aksu.supervision.data.model.RectificationNotice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class RectificationDetailViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _notice = MutableStateFlow<RectificationNotice?>(null)
    val notice = _notice.asStateFlow()

    var isLoading by mutableStateOf(false)

    fun loadNotice(id: Long) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = apiService.getRectification(id)
                if (response.isSuccessful && response.data != null) {
                    _notice.value = response.data
                }
            } catch (_: Exception) { }
            isLoading = false
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RectificationDetailScreen(
    rectificationId: Long = 0,
    onNavigateBack: () -> Unit = {},
    onNavigateToAcceptance: (Long) -> Unit = {},
    viewModel: RectificationDetailViewModel = hiltViewModel()
) {
    val notice by viewModel.notice.collectAsState()

    LaunchedEffect(rectificationId) { viewModel.loadNotice(rectificationId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("整改详情") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "返回")
                    }
                }
            )
        }
    ) { padding ->
        notice?.let { n ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 基本信息
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("企业：${n.enterpriseName}", style = MaterialTheme.typography.titleSmall)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("检查员：${n.inspectorName}", style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(
                                    shape = MaterialTheme.shapes.small,
                                    color = MaterialTheme.colorScheme.secondaryContainer
                                ) {
                                    Text(
                                        n.statusLabel(),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row {
                                Icon(Icons.Default.Schedule, null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("整改截止：${n.deadline}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }

                // 整改问题列表
                item {
                    Text("整改问题", style = MaterialTheme.typography.titleSmall)
                }

                items(n.issues, key = { it.id }) { issue ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(issue.description, style = MaterialTheme.typography.bodyMedium)
                            issue.standard?.let {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("标准：$it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            issue.requirement?.let {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("整改要求：$it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }

                // 整改反馈
                if (n.status in listOf("SUBMITTED", "ACCEPTED", "REJECTED")) {
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("整改反馈", style = MaterialTheme.typography.titleSmall)
                                Spacer(modifier = Modifier.height(8.dp))
                                n.rectificationRemark?.let {
                                    Text(it, style = MaterialTheme.typography.bodyMedium)
                                }
                                n.acceptorName?.let {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("验收人：$it", style = MaterialTheme.typography.bodySmall)
                                }
                                n.acceptanceRemark?.let {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("验收意见：$it", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }

                // 验收按钮
                if (n.status == "SUBMITTED") {
                    item {
                        Button(
                            onClick = { onNavigateToAcceptance(n.id) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Icon(Icons.Default.FactCheck, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("进行验收")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RectificationDetailScreenPreview() {
    MaterialTheme {
        RectificationDetailScreen()
    }
}
