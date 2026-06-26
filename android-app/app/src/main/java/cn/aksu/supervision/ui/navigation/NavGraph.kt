package cn.aksu.supervision.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cn.aksu.supervision.ui.alert.AlertDetailScreen
import cn.aksu.supervision.ui.alert.AlertListScreen
import cn.aksu.supervision.ui.enterprise.EnterpriseProfileScreen
import cn.aksu.supervision.ui.enterprise.EnterpriseSearchScreen
import cn.aksu.supervision.ui.home.HomeScreen
import cn.aksu.supervision.ui.inspection.InspectionFormScreen
import cn.aksu.supervision.ui.inspection.InspectionSubmitScreen
import cn.aksu.supervision.ui.login.LoginScreen
import cn.aksu.supervision.ui.profile.MessageListScreen
import cn.aksu.supervision.ui.profile.ProfileScreen
import cn.aksu.supervision.ui.rectification.AcceptanceScreen
import cn.aksu.supervision.ui.rectification.RectificationDetailScreen
import cn.aksu.supervision.ui.rectification.RectificationListScreen
import cn.aksu.supervision.ui.task.TaskDetailScreen
import cn.aksu.supervision.ui.task.TaskListScreen
import cn.aksu.supervision.ui.task.TaskStatScreen

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, "首页",
        androidx.compose.material.icons.Icons.Outlined.Home,
        androidx.compose.material.icons.Icons.Filled.Home
    ),
    BottomNavItem(Screen.TaskList(), "任务",
        androidx.compose.material.icons.Icons.Outlined.Assignment,
        androidx.compose.material.icons.Icons.Filled.Assignment
    ),
    BottomNavItem(Screen.EnterpriseSearch, "企业",
        androidx.compose.material.icons.Icons.Outlined.Business,
        androidx.compose.material.icons.Icons.Filled.Business
    ),
    BottomNavItem(Screen.AlertList, "预警",
        androidx.compose.material.icons.Icons.Outlined.Notifications,
        androidx.compose.material.icons.Icons.Filled.Notifications
    ),
    BottomNavItem(Screen.Profile, "我的",
        androidx.compose.material.icons.Icons.Outlined.Person,
        androidx.compose.material.icons.Icons.Filled.Person
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // 需要显示底部导航的路由
    val bottomBarRoutes = bottomNavItems.map { it.screen.route.substringBefore("?") }
    val showBottomBar = currentDestination?.hierarchy?.any {
        it.route?.substringBefore("?") in bottomBarRoutes
    } == true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route?.startsWith(item.screen.route.substringBefore("?")) == true
                        } == true
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.icon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) },
                            selected = selected,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 登录
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            // 首页
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToTaskList = { navController.navigate(Screen.taskList(it)) },
                    onNavigateToAlertList = { navController.navigate(Screen.AlertList.route) },
                    onNavigateToTaskDetail = { navController.navigate(Screen.taskDetail(it)) },
                    onNavigateToAlertDetail = { navController.navigate(Screen.alertDetail(it)) },
                    onNavigateToMessages = { navController.navigate(Screen.MessageList.route) }
                )
            }

            // 任务列表
            composable(
                route = Screen.TaskList.route,
                arguments = listOf(navArgument("type") { type = NavType.StringType; nullable = true; defaultValue = null })
            ) {
                TaskListScreen(
                    onNavigateToDetail = { navController.navigate(Screen.taskDetail(it)) },
                    onNavigateToStat = { navController.navigate(Screen.TaskStat.route) }
                )
            }

            // 任务详情
            composable(
                route = Screen.TaskDetail.route,
                arguments = listOf(navArgument("taskId") { type = NavType.LongType })
            ) {
                TaskDetailScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToInspection = { taskId, enterpriseId ->
                        navController.navigate(Screen.inspectionForm(taskId, enterpriseId))
                    },
                    onNavigateToEnterprise = { enterpriseId ->
                        navController.navigate(Screen.enterpriseProfile(enterpriseId))
                    }
                )
            }

            // 任务统计
            composable(Screen.TaskStat.route) {
                TaskStatScreen(onNavigateBack = { navController.popBackStack() })
            }

            // 检查表单
            composable(
                route = Screen.InspectionForm.route,
                arguments = listOf(
                    navArgument("taskId") { type = NavType.LongType },
                    navArgument("enterpriseId") { type = NavType.LongType }
                )
            ) {
                InspectionFormScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onSubmitSuccess = { recordId ->
                        navController.navigate(Screen.inspectionSubmit(recordId))
                    }
                )
            }

            // 检查提交确认
            composable(
                route = Screen.InspectionSubmit.route,
                arguments = listOf(navArgument("recordId") { type = NavType.LongType })
            ) {
                InspectionSubmitScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onConfirm = { navController.popBackStack() }
                )
            }

            // 整改列表
            composable(
                route = Screen.RectificationList.route,
                arguments = listOf(navArgument("status") { type = NavType.StringType; nullable = true; defaultValue = null })
            ) {
                RectificationListScreen(
                    onNavigateToDetail = { navController.navigate(Screen.rectificationDetail(it)) }
                )
            }

            // 整改详情
            composable(
                route = Screen.RectificationDetail.route,
                arguments = listOf(navArgument("rectificationId") { type = NavType.LongType })
            ) {
                RectificationDetailScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToAcceptance = { id -> navController.navigate(Screen.acceptance(id)) }
                )
            }

            // 验收
            composable(
                route = Screen.Acceptance.route,
                arguments = listOf(navArgument("rectificationId") { type = NavType.LongType })
            ) {
                AcceptanceScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onAcceptanceComplete = { navController.popBackStack() }
                )
            }

            // 企业搜索
            composable(Screen.EnterpriseSearch.route) {
                EnterpriseSearchScreen(
                    onNavigateToProfile = { navController.navigate(Screen.enterpriseProfile(it)) }
                )
            }

            // 企业档案
            composable(
                route = Screen.EnterpriseProfile.route,
                arguments = listOf(navArgument("enterpriseId") { type = NavType.LongType })
            ) {
                EnterpriseProfileScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 预警列表
            composable(Screen.AlertList.route) {
                AlertListScreen(
                    onNavigateToDetail = { navController.navigate(Screen.alertDetail(it)) }
                )
            }

            // 预警详情
            composable(
                route = Screen.AlertDetail.route,
                arguments = listOf(navArgument("alertId") { type = NavType.LongType })
            ) {
                AlertDetailScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 个人中心
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onNavigateToMessages = { navController.navigate(Screen.MessageList.route) },
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // 消息列表
            composable(Screen.MessageList.route) {
                MessageListScreen(onNavigateBack = { navController.popBackStack() })
            }
        }
    }
}
