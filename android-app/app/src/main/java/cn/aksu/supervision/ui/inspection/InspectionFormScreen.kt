package cn.aksu.supervision.ui.inspection

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cn.aksu.supervision.data.model.InspectionItem
import cn.aksu.supervision.data.model.InspectionTemplate
import cn.aksu.supervision.data.repository.InspectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class InspectionFormViewModel @Inject constructor(
    private val inspectionRepository: InspectionRepository
) : ViewModel() {

    private val _template = MutableStateFlow<InspectionTemplate?>(null)
    val template = _template.asStateFlow()

    private val _items = MutableStateFlow<List<InspectionItem>>(emptyList())
    val items = _items.asStateFlow()

    var conclusion by mutableStateOf("")
    var remark by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var isSubmitting by mutableStateOf(false)
    var submitSuccess by mutableStateOf(false)
    var submittedRecordId by mutableStateOf<Long?>(null)

    fun loadTemplate(category: String? = null) {
        viewModelScope.launch {
            isLoading = true
            inspectionRepository.getTemplates(category).onSuccess { templates ->
                if (templates.isNotEmpty()) {
                    _template.value = templates[0]
                    _items.value = templates[0].items.map { it.copy(result = null) }
                }
            }
            isLoading = false
        }
    }

    fun updateItemResult(index: Int, result: String) {
        val current = _items.value.toMutableList()
        if (index in current.indices) {
            current[index] = current[index].copy(result = result)
            _items.value = current
        }
    }

    fun updateItemRemark(index: Int, remark: String) {
        val current = _items.value.toMutableList()
        if (index in current.indices) {
            current[index] = current[index].copy(remark = remark)
            _items.value = current
        }
    }

    fun submit(taskId: Long, enterpriseId: Long) {
        viewModelScope.launch {
            isSubmitting = true
            val record = cn.aksu.supervision.data.model.InspectionRecord(
                id = 0,
                taskId = taskId,
                enterpriseId = enterpriseId,
                enterpriseName = "",
                inspectorId = 0,
                inspectorName = "",
                inspectionDate = "",
                items = _items.value,
                photos = null,
                conclusion = conclusion,
                remark = remark.ifBlank { null },
                latitude = null,
                longitude = null,
                address = null,
                status = "SUBMITTED",
                createdAt = "",
                updatedAt = null
            )
            inspectionRepository.submitInspection(record).onSuccess {
                submittedRecordId = it.id.ifZero { System.currentTimeMillis() }
                submitSuccess = true
            }
            isSubmitting = false
        }
    }

    private fun Long.ifZero(fallback: () -> Long): Long = if (this == 0L) fallback() else this
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectionFormScreen(
    taskId: Long = 0,
    enterpriseId: Long = 0,
    onNavigateBack: () -> Unit = {},
    onSubmitSuccess: (Long) -> Unit = {},
    viewModel: InspectionFormViewModel = hiltViewModel()
) {
    val items by viewModel.items.collectAsState()
    val template by viewModel.template.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadTemplate() }
    LaunchedEffect(viewModel.submitSuccess) {
        if (viewModel.submitSuccess) {
            viewModel.submittedRecordId?.let { onSubmitSuccess(it) }
        }
    }

    var showCamera by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("现场检查") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { showCamera = true }) {
                        Icon(Icons.Default.CameraAlt, "拍照")
                    }
                    IconButton(onClick = { /* 语音输入 */ }) {
                        Icon(Icons.Default.Mic, "语音")
                    }
                }
            )
        }
    ) { padding ->
        if (viewModel.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 检查模板标题
                item {
                    template?.let {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("检查模板：${it.name}", style = MaterialTheme.typography.titleSmall)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("类别：${it.category}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }

                // 检查项列表
                itemsIndexed(items, key = { _, item -> item.id }) { index, item ->
                    InspectionItemCard(
                        item = item,
                        index = index,
                        onResultChange = { viewModel.updateItemResult(index, it) },
                        onRemarkChange = { viewModel.updateItemRemark(index, it) }
                    )
                }

                // 检查结论
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("检查结论", style = MaterialTheme.typography.titleSmall)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(
                                    selected = viewModel.conclusion == "QUALIFIED",
                                    onClick = { viewModel.conclusion = "QUALIFIED" },
                                    label = { Text("合格") }
                                )
                                FilterChip(
                                    selected = viewModel.conclusion == "UNQUALIFIED",
                                    onClick = { viewModel.conclusion = "UNQUALIFIED" },
                                    label = { Text("不合格") }
                                )
                                FilterChip(
                                    selected = viewModel.conclusion == "CONDITIONAL",
                                    onClick = { viewModel.conclusion = "CONDITIONAL" },
                                    label = { Text("有条件合格") }
                                )
                            }
                        }
                    }
                }

                // 备注
                item {
                    OutlinedTextField(
                        value = viewModel.remark,
                        onValueChange = { viewModel.remark = it },
                        label = { Text("备注") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }

                // 提交按钮
                item {
                    Button(
                        onClick = { viewModel.submit(taskId, enterpriseId) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = viewModel.conclusion.isNotBlank() && !viewModel.isSubmitting
                    ) {
                        if (viewModel.isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Send, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("提交检查结果")
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun InspectionItemCard(
    item: InspectionItem,
    index: Int,
    onResultChange: (String) -> Unit,
    onRemarkChange: (String) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "${index + 1}. ${item.content}",
                style = MaterialTheme.typography.titleSmall
            )
            item.standard?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text("标准：$it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = item.result == "PASS",
                    onClick = { onResultChange("PASS") },
                    label = { Text("符合") },
                    leadingIcon = if (item.result == "PASS") {
                        { Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp)) }
                    } else null
                )
                FilterChip(
                    selected = item.result == "FAIL",
                    onClick = { onResultChange("FAIL") },
                    label = { Text("不符合") },
                    leadingIcon = if (item.result == "FAIL") {
                        { Icon(Icons.Default.Close, null, modifier = Modifier.size(16.dp)) }
                    } else null
                )
                FilterChip(
                    selected = item.result == "N/A",
                    onClick = { onResultChange("N/A") },
                    label = { Text("不适用") }
                )
            }
            if (item.result == "FAIL") {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = item.remark ?: "",
                    onValueChange = onRemarkChange,
                    label = { Text("问题描述") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InspectionFormScreenPreview() {
    MaterialTheme {
        InspectionFormScreen()
    }
}
