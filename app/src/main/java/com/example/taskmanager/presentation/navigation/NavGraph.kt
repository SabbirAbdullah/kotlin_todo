package com.example.taskmanager.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.taskmanager.presentation.auth.forgot.ForgotPasswordScreen
import com.example.taskmanager.presentation.auth.login.LoginScreen
import com.example.taskmanager.presentation.auth.register.RegisterScreen
import com.example.taskmanager.presentation.dashboard.DashboardScreen
import com.example.taskmanager.presentation.profile.ProfileScreen
import com.example.taskmanager.presentation.task.create.CreateTaskScreen
import com.example.taskmanager.presentation.task.detail.TaskDetailScreen
import com.example.taskmanager.presentation.task.list.TaskListScreen


sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Dashboard : Screen("dashboard")
    object TaskList : Screen("task_list")
    object CreateTask : Screen("create_task")
    object EditTask : Screen("edit_task/{taskId}") {
        fun createRoute(taskId: Int) = "edit_task/$taskId"
    }
    object TaskDetail : Screen("task_detail/{taskId}") {
        fun createRoute(taskId: Int) = "task_detail/$taskId"
    }
    object Profile : Screen("profile")
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(300))
        },
        exitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(300))
        },
        popEnterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300))
        },
        popExitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300))
        }
    ) {

        // ── Auth Screens ──────────────────────────────────────────────────────
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }

        composable(Screen.Register.route) {
            RegisterScreen(navController = navController)
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(navController = navController)
        }

        // ── Main Screens ──────────────────────────────────────────────────────
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController = navController)
        }

        composable(Screen.TaskList.route) {
            TaskListScreen(navController = navController)
        }

        composable(Screen.CreateTask.route) {
            CreateTaskScreen(navController = navController)
        }

        composable(
            route = Screen.EditTask.route,
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { backStack ->
            val taskId = backStack.arguments?.getInt("taskId") ?: return@composable
            CreateTaskScreen(navController = navController, editTaskId = taskId)
        }

        composable(
            route = Screen.TaskDetail.route,
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { backStack ->
            val taskId = backStack.arguments?.getInt("taskId") ?: return@composable
            TaskDetailScreen(navController = navController, taskId = taskId)
        }

        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
    }
}

