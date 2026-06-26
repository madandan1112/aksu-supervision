package cn.aksu.supervision.ui.enterprise

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
class EnterpriseProfileViewModel @Inject constructor(
    private val enterpriseRepository: EnterpriseRepository
) : ViewModel() {

    private val _enterprise = MutableStateFlow<Enterprise?>(null)
    val enterprise = _enterprise.asStateFlow()

    var isLoading by mutableStateOf(false)

    fun loadEnterprise(id: Long) {
        viewModelScope.launch {
            isLoading = true
            enterpriseRepository.getEnterprise(id).onSuccess { _enterprise.value = it }
            isLoading = false
        }
    }
}

private val profileTabs = listOf("基本信息", "检查记录", "整改记录", "风险信息")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterpriseProfileScreen(
    enterpriseId: Long = 0,
    onNavigateBack: () -> Unit = {},
    viewModel: EnterpriseProfileViewModel = hiltViewModel()
) {
    val enterprise by viewModel.enterprise.collectAsState()
    val pagerState = rememberPagerState(pageCount = { profileTabs.size })

    LaunchedEffect(enterpriseId) { viewModel.loadEnterprise(enterpriseId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(enterprise?.name ?: "企业档案") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "返回")
                    }
                }
            )
        }
    ) { padding ->
        enterprise?.let { e ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // 头部摘要卡片
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(e.name, style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(e.unifiedCode, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${e.inspectionCount}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                                Text("检查次数", style = MaterialTheme.typography.labelSmall)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${e.rectificationCount}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                                Text("整改次数", style = MaterialTheme.typography.labelSmall)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(e.riskLevelLabel(), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                                Text("风险等级", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }

                // Tab 栏
                PrimaryTabRow(selectedTabIndex = pagerState.currentPage) {
                    profileTabs.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = { },
                            text = { Text(title) }
                        )
                    }
                }

                // Tab 内容
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    when (page) {
                        0 -> BasicInfoTab(e)
                        1 -> InspectionHistoryTab(e)
                        2 -> RectificationHistoryTab(e)
                        3 -> RiskInfoTab(e)
                    }
                }
            }
        }
    }
}

@Composable
private fun BasicInfoTab(enterprise: Enterprise) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        InfoRow("法人代表", enterprise.legalPerson ?: "-")
        InfoRow("联系电话", enterprise.phone ?: "-")
        InfoRow("注册地址", enterprise.address ?: "-")
        InfoRow("所属行业", enterprise.industry ?: "-")
        InfoRow("行业类别", enterprise.category ?: "-")
        InfoRow("监管类型", enterprise.supervisionType ?: "-")
        InfoRow("经营状态", enterprise.statusLabel())
        InfoRow("上次检查", enterprise.lastInspectionDate ?: "-")
        InfoRow("下次检查", enterprise.nextInspectionDate ?: "-")
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun InspectionHistoryTab(enterprise: Enterprise) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Assignment, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Text("共 ${enterprise.inspectionCount} 次检查记录", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun RectificationHistoryTab(enterprise: Enterprise) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.FactCheck, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Text("共 ${enterprise.rectificationCount} 次整改记录", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun RiskInfoTab(enterprise: Enterprise) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("风险等级", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))
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
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EnterpriseProfileScreenPreview() {
    MaterialTheme {
        EnterpriseProfileScreen()
    }
}
