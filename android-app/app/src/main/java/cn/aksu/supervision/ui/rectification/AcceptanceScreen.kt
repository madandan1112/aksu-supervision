package cn.aksu.supervision.ui.rectification

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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

@HiltViewModel
class AcceptanceViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    var conclusion by mutableStateOf("")
    var remark by mutableStateOf("")
    var isSubmitting by mutableStateOf(false)
    var submitSuccess by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun submit(rectificationId: Long) {
        if (conclusion.isBlank()) {
            errorMessage = "请选择验收结论"
            return
        }
        viewModelScope.launch {
            isSubmitting = true
            errorMessage = null
            try {
                val response = apiService.acceptRectification(rectificationId, conclusion, remark.ifBlank { null })
                if (response.isSuccessful) {
                    submitSuccess = true
                } else {
                    errorMessage = response.message ?: "验收失败"
                }
            } catch (e: Exception) {
                errorMessage = "网络异常：${e.localizedMessage}"
            }
            isSubmitting = false
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcceptanceScreen(
    rectificationId: Long = 0,
    onNavigateBack: () -> Unit = {},
    onAcceptanceComplete: () -> Unit = {},
    viewModel: AcceptanceViewModel = hiltViewModel()
) {
    LaunchedEffect(viewModel.submitSuccess) {
        if (viewModel.submitSuccess) onAcceptanceComplete()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("整改验收") },
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
            // 验收结论
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("验收结论", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        FilterChip(
                            selected = viewModel.conclusion == "PASS",
                            onClick = { viewModel.conclusion = "PASS" },
                            label = { Text("验收通过") },
                            leadingIcon = if (viewModel.conclusion == "PASS") {
                                { Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(18.dp)) }
                            } else null
                        )
                        FilterChip(
                            selected = viewModel.conclusion == "FAIL",
                            onClick = { viewModel.conclusion = "FAIL" },
                            label = { Text("验收不通过") },
                            leadingIcon = if (viewModel.conclusion == "FAIL") {
                                { Icon(Icons.Default.Cancel, null, modifier = Modifier.size(18.dp)) }
                            } else null
                        )
                    }
                }
            }

            // 验收意见
            OutlinedTextField(
                value = viewModel.remark,
                onValueChange = { viewModel.remark = it },
                label = { Text("验收意见") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                placeholder = { Text("请输入验收意见（选填）") }
            )

            // 错误信息
            viewModel.errorMessage?.let { msg ->
                Text(msg, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.weight(1f))

            // 提交按钮
            Button(
                onClick = { viewModel.submit(rectificationId) },
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
                    Text("提交验收结果")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AcceptanceScreenPreview() {
    MaterialTheme {
        AcceptanceScreen()
    }
}
