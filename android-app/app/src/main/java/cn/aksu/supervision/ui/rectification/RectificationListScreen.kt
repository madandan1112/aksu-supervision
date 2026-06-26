package cn.aksu.supervision.ui.rectification

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
import androidx.compose.ui.text.style.TextOverflow
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

private val statusTabs = listOf(
    null to "全部",
    "ISSUED" to "已下达",
    "RECTIFYING" to "整改中",
    "SUBMITTED" to "待验收",
    "ACCEPTED" to "已通过",
    "REJECTED" to "未通过",
    "OVERDUE" to "已逾期"
)

@HiltViewModel
class RectificationListViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _notices = MutableStateFlow<List<RectificationNotice>>(emptyList())
    val notices = _notices.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    var selectedStatus by mutableStateOf<String?>(null)

    init { loadNotices() }

    fun loadNotices() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getRectifications(status = selectedStatus)
                if (response.isSuccessful && response.data != null) {
                    _notices.value = response.data.content
                }
            } catch (_: Exception) { }
            _isLoading.value = false
        }
    }

    fun onStatusChanged(status: String?) {
        selectedStatus = status
        loadNotices()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RectificationListScreen(
    onNavigateToDetail: (Long) -> Unit = {},
    viewModel: RectificationListViewModel = hiltViewModel()
) {
    val notices by viewModel.notices.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("整改验收") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 8.dp
            ) {
                statusTabs.forEachIndexed { index, (status, label) ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            selectedTabIndex = index
                            viewModel.onStatusChanged(status)
                        },
                        text = { Text(label) }
                    )
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (notices.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("暂无整改记录", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(notices, key = { it.id }) { notice ->
                        RectificationCard(notice = notice, onClick = { onNavigateToDetail(notice.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun RectificationCard(notice: RectificationNotice, onClick: () -> Unit) {
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
                    notice.enterpriseName,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = when (notice.status) {
                        "SUBMITTED" -> MaterialTheme.colorScheme.primaryContainer
                        "ACCEPTED" -> MaterialTheme.colorScheme.tertiaryContainer
                        "OVERDUE", "REJECTED" -> MaterialTheme.colorScheme.errorContainer
                        else -> MaterialTheme.colorScheme.secondaryContainer
                    }
                ) {
                    Text(
                        notice.statusLabel(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("问题数：${notice.issues.size}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                Icon(Icons.Default.Schedule, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(4.dp))
                Text("截止：${notice.deadline}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RectificationListScreenPreview() {
    MaterialTheme {
        RectificationListScreen()
    }
}
