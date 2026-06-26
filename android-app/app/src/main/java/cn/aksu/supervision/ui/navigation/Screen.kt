package cn.aksu.supervision.ui.navigation

sealed class Screen(val route: String) {
    // 登录
    data object Login : Screen("login")

    // 首页
    data object Home : Screen("home")

    // 任务
    data object TaskList : Screen("tasks?type={type}")
    data object TaskDetail : Screen("tasks/{taskId}")
    data object TaskStat : Screen("tasks/statistics")

    // 检查
    data object InspectionForm : Screen("inspection/form?taskId={taskId}&enterpriseId={enterpriseId}")
    data object InspectionSubmit : Screen("inspection/submit/{recordId}")

    // 整改
    data object RectificationList : Screen("rectifications?status={status}")
    data object RectificationDetail : Screen("rectifications/{rectificationId}")
    data object Acceptance : Screen("rectifications/{rectificationId}/acceptance")

    // 企业
    data object EnterpriseSearch : Screen("enterprises/search")
    data object EnterpriseProfile : Screen("enterprises/{enterpriseId}")

    // 预警
    data object AlertList : Screen("alerts")
    data object AlertDetail : Screen("alerts/{alertId}")

    // 个人中心
    data object Profile : Screen("profile")
    data object MessageList : Screen("messages")

    // 构建路由辅助
    companion object {
        fun taskList(type: String? = null) =
            if (type != null) "tasks?type=$type" else "tasks"

        fun taskDetail(taskId: Long) = "tasks/$taskId"

        fun inspectionForm(taskId: Long, enterpriseId: Long) =
            "inspection/form?taskId=$taskId&enterpriseId=$enterpriseId"

        fun inspectionSubmit(recordId: Long) = "inspection/submit/$recordId"

        fun rectificationList(status: String? = null) =
            if (status != null) "rectifications?status=$status" else "rectifications"

        fun rectificationDetail(id: Long) = "rectifications/$id"
        fun acceptance(id: Long) = "rectifications/$id/acceptance"
        fun enterpriseProfile(id: Long) = "enterprises/$id"
        fun alertDetail(id: Long) = "alerts/$id"
    }
}
