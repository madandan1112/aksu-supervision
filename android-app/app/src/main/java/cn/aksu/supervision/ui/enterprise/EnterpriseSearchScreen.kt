package cn.aksu.supervision.ui.enterprise

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
import cn.aksu.supervision.data.model.Enterprise
import cn.aksu.supervision.data.repository.EnterpriseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class EnterpriseSearchViewModel @Inject constructor(
    private val enterpriseRepository: EnterpriseRepository
) : ViewModel() {

    private val _enterprises = MutableStateFlow<List<Enterprise>>(emptyList())
    val enterprises = _enterprises.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    var searchQuery by mutableStateOf("")

    fun search() {
        viewModelScope.launch {
            _isLoading.value = true
            enterpriseRepository.search(
                keyword = searchQuery.ifBlank { null }
            ).collect { result ->
                result.onSuccess { _enterprises.value = it }
                result.onFailure { _enterprises.value = emptyList() }
                _isLoading.value = false
            }
        }
    }

    fun searchByCode(code: String) {
        searchQuery = code
        search()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterpriseSearchScreen(
    onNavigateToProfile: (Long) -> Unit = {},
    viewModel: EnterpriseSearchViewModel = hiltViewModel()
) {
    val enterprises by viewModel.enterprises.collectAsState()
    val isLoading by viewModel.isLoading.collectAsFlow()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("企业查询") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 搜索栏
            SearchBar(
                query = viewModel.searchQuery,
                onQueryChange = { viewModel.searchQuery = it },
                onSearch = { viewModel.search() },
                active = false,
                onActiveChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("搜索企业名称或信用代码") },
                leadingIcon = { Icon(Icons.Default.Search, "搜索") },
                trailingIcon = {
                    Row {
                        // 扫码按钮
                        IconButton(onClick = { /* 启动扫码 */ }) {
                            Icon(Icons.Default.QrCodeScanner, "扫码")
                        }
                        if (viewModel.searchQuery.isNotEmpty()) {
                            IconButton(onClick = {
                                viewModel.searchQuery = ""
                                viewModel.search()
                            }) {
                                Icon(Icons.Default.Clear, "清除")
                            }
                        }
                    }
                }
            ) {}

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (enterprises.isEmpty() && viewModel.searchQuery.isNotBlank()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Business,
                            null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("未找到匹配企业", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(enterprises, key = { it.id }) { enterprise ->
                        EnterpriseCard(
                            enterprise = enterprise,
                            onClick = { onNavigateToProfile(enterprise.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EnterpriseCard(enterprise: Enterprise, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                enterprise.name,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                enterprise.unifiedCode,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                enterprise.industry?.let {
                    AssistChip(onClick = {}, label = { Text(it, style = MaterialTheme.typography.labelSmall) }, modifier = Modifier.height(24.dp))
                }
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = when (enterprise.riskLevel) {
                        "HIGH" -> MaterialTheme.colorScheme.errorContainer
                        "MEDIUM" -> MaterialTheme.colorScheme.tertiaryContainer
                        else -> MaterialTheme.colorScheme.primaryContainer
                    }
                ) {
                    Text(
                        enterprise.riskLevelLabel(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EnterpriseSearchScreenPreview() {
    MaterialTheme {
        EnterpriseSearchScreen()
    }
}
