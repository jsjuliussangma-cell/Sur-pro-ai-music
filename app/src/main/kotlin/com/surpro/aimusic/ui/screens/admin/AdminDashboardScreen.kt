package com.surpro.aimusic.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.surpro.aimusic.ui.components.AdminStatRow
import com.surpro.aimusic.ui.components.StatCard
import com.surpro.aimusic.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val tabs = listOf("Dashboard", "Payments", "Users")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            TabRow(
                selectedTabIndex = tabs.indexOf(
                    when (uiState.selectedTab) {
                        "payments" -> "Payments"
                        "users" -> "Users"
                        else -> "Dashboard"
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = (index == 0 && uiState.selectedTab == "dashboard") ||
                                (index == 1 && uiState.selectedTab == "payments") ||
                                (index == 2 && uiState.selectedTab == "users"),
                        onClick = {
                            when (index) {
                                0 -> viewModel.selectTab("dashboard")
                                1 -> viewModel.selectTab("payments")
                                2 -> viewModel.selectTab("users")
                            }
                        },
                        text = { Text(tab) }
                    )
                }
            }

            when (uiState.selectedTab) {
                "dashboard" -> DashboardTab(
                    uiState = uiState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
                "payments" -> PaymentsTab(
                    uiState = uiState,
                    onApprove = { paymentId -> viewModel.approvePayment(paymentId) },
                    onReject = { paymentId -> viewModel.rejectPayment(paymentId) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
                "users" -> UsersTab(
                    uiState = uiState,
                    onBanUser = { userId -> viewModel.banUser(userId) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
        }
    }
}

@Composable
fun DashboardTab(
    uiState: com.surpro.aimusic.viewmodel.AdminUiState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Overview",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Total Users",
                    value = uiState.stats.totalUsers.toString(),
                    subtitle = "Active accounts",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Daily Active",
                    value = uiState.stats.dailyActiveUsers.toString(),
                    subtitle = "Users today",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Total Revenue",
                    value = "৳${String.format("%.0f", uiState.stats.totalRevenue)}",
                    subtitle = "All time",
                    backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Active Subs",
                    value = uiState.stats.activeSubscriptions.toString(),
                    subtitle = "Paid plans",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Text(
                text = "Key Metrics",
                style = MaterialTheme.typography.titleMedium
            )
        }

        item {
            AdminStatRow(
                label = "Pending Payments",
                value = uiState.stats.pendingPayments.toString()
            )
        }

        item {
            AdminStatRow(
                label = "Total Tokens Distributed",
                value = "${uiState.stats.totalTokensDistributed / 1000}K"
            )
        }

        item {
            AdminStatRow(
                label = "Subscription Retention",
                value = "94.5%"
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun PaymentsTab(
    uiState: com.surpro.aimusic.viewmodel.AdminUiState,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Pending Payments (${uiState.stats.pendingPayments})",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        item {
            Text(
                text = "No pending payments to review",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun UsersTab(
    uiState: com.surpro.aimusic.viewmodel.AdminUiState,
    onBanUser: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "User Management",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        item {
            AdminStatRow(
                label = "Total Users",
                value = uiState.stats.totalUsers.toString()
            )
        }

        item {
            AdminStatRow(
                label = "Daily Active Users",
                value = uiState.stats.dailyActiveUsers.toString()
            )
        }

        item {
            Text(
                text = "No user actions required",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}
