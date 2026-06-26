package cn.aksu.supervision.ui.profile

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
import cn.aksu.supervision.data.model.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MessageListViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init { loadMessages() }

    fun loadMessages() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getMessages()
                if (response.isSuccessful && response.data != null) {
                    _messages.value = response.data.content
                }
            } catch (_: Exception) { }
            _isLoading.value = false
        }
    }

    fun markRead(id: Long) {
        viewModelScope.launch {
            try { apiService.markMessageRead(id) } catch (_: Exception) { }
        }
    }

    fun markAllRead() {
        viewModelScope.launch {
            try {
                apiService.markAllMessagesRead()
                loadMessages()
            } catch (_: Exception) { }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageListScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: MessageListViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("消息通知") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "返回")
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.markAllRead() }) {
                        Text("全部已读")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (messages.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.NotificationsOff, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("暂无消息", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages, key = { it.id }) { message ->
                    MessageCard(
                        message = message,
                        onClick = {
                            if (!message.isRead) viewModel.markRead(message.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageCard(message: Message, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            // 类型图标
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = when (message.type) {
                    "SYSTEM" -> MaterialTheme.colorScheme.primaryContainer
                    "TASK" -> MaterialTheme.colorScheme.tertiaryContainer
                    "ALERT" -> MaterialTheme.colorScheme.errorContainer
                    else -> MaterialTheme.colorScheme.secondaryContainer
                },
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        when (message.type) {
                            "SYSTEM" -> Icons.Default.Info
                            "TASK" -> Icons.Default.Assignment
                            "ALERT" -> Icons.Default.Warning
                            else -> Icons.Default.Chat
                        },
                        null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        message.title,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (!message.isRead) {
                        Badge(modifier = Modifier.size(8.dp))
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    message.content,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(message.createdAt, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MessageListScreenPreview() {
    MaterialTheme {
        MessageListScreen()
    }
}
